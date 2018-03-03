package io.github.kevroletin.json;

import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.utils.TypeUtils;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.annotations.FieldValidator;
import io.github.kevroletin.json.annotations.ValidationFunction;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import io.github.kevroletin.json.utils.WithErrors;
import java.util.ArrayList;

public class Deserializer {

    public static <T> T deserialize(INode ast, Class<T> cls) throws DeserializationException {
        WithErrors res = new Deserializer().deserializeInternal(ast, cls);
        if (res.hasErrors()) {
            throw new DeserializationException(String.join("; ", res.getErrors()));
        }
        if (!res.hasValue()) {
            return null;
        }
        return (T) res.getValue();
    }

    private <T> WithErrors<T> createEmptyInstance(Class<T> cls) {
        try {
            return WithErrors.of((T) TypeUtils.getDefaultConstructor(cls).newInstance());
        } catch (Exception e) {
            String msg = String.format(
                "Failed to create new %s class instance. " +
                "Public default constructor is not implemented or not accesable.",
                cls.getName());
            return WithErrors.errors(msg);
        }
    }

    private WithErrors<Map<String, INode>> ensureNodeIsObject(INode ast) {
        if (!ast.isObject()) {
            return WithErrors.errors(String.format("Expected object, got %s", ast.getClass().getName()));
        }
        return WithErrors.of(((ObjectNode)ast).get());
    }

    private WithErrors<List<INode>> ensureNodeIsArray(INode ast) {
        if (!ast.isArray()) {
            return WithErrors.errors(String.format("Expected array or list, got %s", ast.getClass().getName()));
        }
        return WithErrors.of(((ArrayNode)ast).get());
    }

    private WithErrors<Object> deserializeScalar(INode value, Class<?> cls) {
        if (TypeUtils.isUnsupportedScalar(cls)) {
            return WithErrors.errors(
                String.format("Deserialization into %s class is not supported.", cls.getName()));
        }
        if (value.isNull()) {
            return WithErrors.of(null);
        } else {
            assert(TypeUtils.isSupportedScalarClass(cls));
            Object res = value.getUnsafe();
            if (!res.getClass().equals(cls)) {
                return WithErrors.errors(
                    String.format("Failed to deserialize scalar: expected %s but got %s",
                                  cls.getName(), res.getClass().getName()));
            }
            return WithErrors.of(res);
        }
    }

    private WithErrors<Object> deserializeArray(INode ast, Class<?> arrCls) {
        assert(arrCls.isArray());
        Class<?> elemCls = arrCls.getComponentType();

        WithErrors<List<INode>> astValuesErr = ensureNodeIsArray(ast);
        if (!astValuesErr.hasValue()) {
            return astValuesErr.copyErrors(Object.class);
        }

        List<String> errors = new ArrayList();
        List<INode> astValues = astValuesErr.getValue();
        Object res = Array.newInstance(elemCls, astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            WithErrors<Object> val = deserializeInternal(valAst, elemCls);
            errors.addAll(val.getErrors());
            if (val.hasValue()) {
                try {
                    Array.set(res, i, val.getValue());
                } catch (IllegalArgumentException e) {
                    errors.add(
                        String.format("Failed to set array element #%d. Expected type %s but got %s",
                                    i,
                                    elemCls.getName(),
                                    val.getClass().getName()));
                }
            }
        }

        WithErrors<Object> ans = WithErrors.of(res);
        ans.addAllErrors(errors);
        return ans;
    }

    private String validateField(Field field, Object value) {
        FieldValidator fieldValidator = field.getAnnotation(FieldValidator.class);
        if (fieldValidator != null) {
            ValidationFunction f;
            try {
                Constructor<?> ctor = TypeUtils.getDefaultConstructor(fieldValidator.cls());
                f = (ValidationFunction) ctor.newInstance();
            } catch (Exception e) {
                return String.format("Failed to instantiate %s validator", fieldValidator.cls().getName());
            }
            Boolean ok = f.validate(value);
            if (!ok) {
                return
                    String.format("Validator %s rejected value %s",
                        fieldValidator.cls().getName(),
                        value.toString()
                    );
            }
        }
        return null;
    }

    private WithErrors<Object> deserializeObject(INode ast, Class<?> objCls) {
        WithErrors resObjErr = createEmptyInstance(objCls);
        if (!resObjErr.hasValue()) {
            return resObjErr;
        }
        WithErrors<Map<String, INode>> allValuesErr = ensureNodeIsObject(ast);
        if (!allValuesErr.hasValue()) {
            return allValuesErr.copyErrors(Object.class);
        }

        Map<String, INode> allValues = allValuesErr.getValue();
        Object resObj = resObjErr.getValue();
        List<String> errors = new ArrayList();

        List<Field> allFields = TypeUtils.getAllFields(objCls);
        for (Field field: allFields) {
            String name = field.getName();
            INode val = allValues.get(name);
            // TODO: how about configurable nullable fields?
            if (val == null) {
                errors.add(String.format("%s field is missed in serializer AST", name));
                continue;
            }

            WithErrors<Object> valueErr = deserializeInternal(val, field.getType());
            errors.addAll(valueErr.getErrors());
            if (!valueErr.hasValue()) {
                continue;
            }
            Object value = valueErr.getValue();

            String validError = validateField(field, value);
            if (validError != null) {
                errors.add(validError);
            }

            try {
                field.setAccessible(true);
                field.set(resObj, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                errors.add(
                    String.format("Failed to set %s field to value of type %s",
                                  field.getName(),
                                  value.getClass().getName()));
            }
        }

        WithErrors<Object> res = WithErrors.of(resObj);
        res.addAllErrors(errors);
        return res;
    }

    private WithErrors<Object> deserializeInternal(INode ast, Class<?> cls) {
        // TODO: find deserializers using annotations
        if (TypeUtils.isUnsupportedScalarClass(cls)) {
            return WithErrors.errors(
                String.format("Deserialization of class %s is not supported", cls.getName()));
        }
        if (ast.isNull()) {
            return WithErrors.of(null);
        }
        if (TypeUtils.isSupportedScalarClass(cls)) {
            return deserializeScalar(ast, cls);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(ast, cls);
        }
        return deserializeObject(ast, cls);
    }

    private void throwUnsupportedClass(Class<?> cls) throws DeserializationException {
        throw new DeserializationException(
            String.format("Deserialization of class %s is not supported", cls.getName()));
    }
}
