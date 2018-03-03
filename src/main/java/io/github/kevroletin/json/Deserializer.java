package io.github.kevroletin.json;

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
import io.github.kevroletin.json.utils.Maybe;
import java.util.ArrayList;
import java.util.Objects;

public class Deserializer {

    private List<String> errors;

    public Deserializer(List<String> errors) {
        this.errors = errors;
    }

    public Deserializer() {
        errors = new ArrayList();
    }

    public List<String> getErrors() {
        return errors;
    }

    public <T> Result<T> deserialize(INode ast, Class<T> cls) {
        Deserializer d = new Deserializer();
        Maybe res = d.deserializeInternal(ast, cls);
        return new Result(res, d.errors);
    }

    private <T> T createEmptyInstance(Class<T> cls) {
        try {
            return (T) TypeUtils.getDefaultConstructor(cls).newInstance();
        } catch (Exception e) {
            errors.add(String.format(
                "Failed to create new %s class instance. " +
                "Public default constructor is not implemented or not accesable.",
                cls.getName()));
            return null;
        }
    }

    private Map<String, INode> ensureNodeIsObject(INode ast) {
        if (!ast.isObject()) {
            errors.add(String.format("Expected object, got %s", ast.getClass().getName()));
            return null;
        }
        return ((ObjectNode)ast).get();
    }

    private List<INode> ensureNodeIsArray(INode ast) {
        if (!ast.isArray()) {
            errors.add(String.format("Expected array or list, got %s", ast.getClass().getName()));
            return null;
        }
        return ((ArrayNode)ast).get();
    }

    private Maybe<Object> deserializeScalar(INode value, Class<?> cls) {
        if (TypeUtils.isUnsupportedScalar(cls)) {
            errors.add(String.format("Deserialization into %s class is not supported.", cls.getName()));
            return Maybe.nothing();
        }
        if (value.isNull()) {
            return Maybe.just(null);
        } else {
            assert(TypeUtils.isSupportedScalarClass(cls));
            Object res = value.getUnsafe();
            if (!res.getClass().equals(cls)) {
                errors.add(
                    String.format("Failed to deserialize scalar: expected %s but got %s",
                                  cls.getName(), res.getClass().getName()));
                return Maybe.nothing();
            }
            return Maybe.just(res);
        }
    }

    private Maybe<Object> deserializeArray(INode ast, Class<?> arrCls) {
        assert(arrCls.isArray());
        Class<?> elemCls = arrCls.getComponentType();

        List<INode> astValues = ensureNodeIsArray(ast);
        if (astValues == null) {
            return Maybe.nothing();
        }

        Object res = Array.newInstance(elemCls, astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            Maybe<Object> val = deserializeInternal(valAst, elemCls);
            if (!val.isJust()) {
                continue;
            }
            try {
                Array.set(res, i, val.get());
            } catch (IllegalArgumentException e) {
                errors.add(
                    String.format("Failed to set array element #%d. Expected type %s but got %s",
                                    i,
                                    elemCls.getName(),
                                    val.get().getClass().getName()));
            }
        }
        return Maybe.just(res);
    }

    private boolean validateField(Field field, Object value) {
        FieldValidator fieldValidator = field.getAnnotation(FieldValidator.class);
        if (fieldValidator == null) {
            return true;
        }
        ValidationFunction f;
        try {
            Constructor<?> ctor = TypeUtils.getDefaultConstructor(fieldValidator.cls());
            f = (ValidationFunction) ctor.newInstance();
        } catch (Exception e) {
            errors.add(String.format("Failed to instantiate %s validator", fieldValidator.cls().getName()));
            return false;
        }
        Boolean ok = f.validate(value);
        if (!ok) {
            errors.add(String.format("Validator %s rejected value %s",
                                     fieldValidator.cls().getName(),
                                     value.toString()));
        }
        return ok;
    }

    private Maybe<Object> deserializeObject(INode ast, Class<?> objCls) {
        Object resObj = createEmptyInstance(objCls);
        if (resObj == null) {
            return Maybe.nothing();
        }
        Map<String, INode> allValues = ensureNodeIsObject(ast);
        if (allValues == null) {
            return Maybe.nothing();
        }

        List<Field> allFields = TypeUtils.getAllFields(objCls);
        for (Field field: allFields) {
            String name = field.getName();
            INode val = allValues.get(name);
            // TODO: how about configurable nullable fields?
            if (val == null) {
                errors.add(String.format("%s field is missed in serializer AST", name));
                continue;
            }

            Maybe<Object> value = deserializeInternal(val, field.getType());
            if (value.isNothing()) {
                continue;
            }

            Boolean ok = validateField(field, value.get());
            if (!ok) {
                continue;
            }

            try {
                field.setAccessible(true);
                field.set(resObj, value.get());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                errors.add(
                    String.format("Failed to set %s field to value of type %s",
                                  field.getName(),
                                  value.get().getClass().getName()));
            }
        }
        return Maybe.just(resObj);
    }

    private Maybe<Object> deserializeInternal(INode ast, Class<?> cls) {
        // TODO: find deserializers using annotations
        if (TypeUtils.isUnsupportedScalarClass(cls)) {
            errors.add(
                String.format("Deserialization of class %s is not supported", cls.getName()));
            return Maybe.nothing();
        }
        if (ast.isNull()) {
            return Maybe.just(null);
        }
        if (TypeUtils.isSupportedScalarClass(cls)) {
            return deserializeScalar(ast, cls);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(ast, cls);
        }
        return deserializeObject(ast, cls);
    }

    @Override
    public String toString() {
        return "Deserializer{" + "errors=" + errors + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.errors);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Deserializer other = (Deserializer) obj;
        if (!Objects.equals(this.errors, other.errors)) {
            return false;
        }
        return true;
    }
}
