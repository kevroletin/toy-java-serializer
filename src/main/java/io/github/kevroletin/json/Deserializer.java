package io.github.kevroletin.json;

import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.utils.TypeUtils;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.annotations.FieldValidator;
import io.github.kevroletin.json.annotations.ValidationFunction;
import io.github.kevroletin.json.exceptions.ValidationException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import io.github.kevroletin.json.annotations.TypeValidator;

public class Deserializer {

    public static <T> T deserialize(INode ast, Class<T> cls) throws DeserializationException {
        return (T) deserializeWoTypecast(ast, cls);
    }

    private static <T> T createEmptyInstance(Class<T> cls) throws DeserializationException {
        try {
            return (T) TypeUtils.getDefaultConstructor(cls).newInstance();
        } catch (Exception e) {
            String msg = String.format(
                "Failed to create new %s class instance. " +
                "Public default constructor is not implemented or not accesable.",
                cls.getName());
            throw new DeserializationException(msg, e);
        }
    }
    
    private static Map<String, INode> ensureNodeIsObject(INode ast) throws DeserializationException {
        if (!ast.isObject()) {
            throw new DeserializationException(String.format("Expected object, got %s", ast.getClass().getName()));
        }
        return ((ObjectNode)ast).get();
    }

    private static List<INode> ensureNodeIsArray(INode ast) throws DeserializationException {
        if (!ast.isArray()) {
            throw new DeserializationException(String.format("Expected array or list, got %s", ast.getClass().getName()));
        }
        return ((ArrayNode)ast).get();
    }
    
    private static Object deserializeScalar(INode value, Class<?> cls) throws DeserializationException {
        if (TypeUtils.isUnsupportedScalar(cls)) {
            throw new DeserializationException(
                String.format("Deserialization into %s class is not supported.", cls.getName()));
        }
        if (value.isNull()) {
            return null;
        } else {
            assert(TypeUtils.isSupportedScalarClass(cls));
            Object res = value.getUnsafe();
            if (!res.getClass().equals(cls)) {
                throw new DeserializationException(
                    String.format("Failed to deserialize scalar: expected %s but got %s",
                                  cls.getName(), res.getClass().getName()));
            }
            return res;
        }
    }

    private static Object deserializeArray(INode ast, Class<?> arrCls) throws DeserializationException, ValidationException {
        assert(arrCls.isArray());
        Class<?> elemCls = arrCls.getComponentType();

        List<INode> astValues = ensureNodeIsArray(ast);
        Object res = Array.newInstance(elemCls, astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            Object val = deserializeWoTypecast(valAst, elemCls);
            try {
                Array.set(res, i, val);
            } catch (IllegalArgumentException e) {
                throw new DeserializationException(
                    String.format("Failed to set array element #%d. Expected type %s but got %s",
                                  i,
                                  elemCls.getName(),
                                  val.getClass().getName()));
            }
        }

        return res;
    }

    private static void validateField(Field field, Object value) throws ValidationException {
        FieldValidator fieldValidator = field.getAnnotation(FieldValidator.class);
        if (fieldValidator != null) {
            ValidationFunction f;
            try {
                Constructor<?> ctor = TypeUtils.getDefaultConstructor(fieldValidator.cls());
                f = (ValidationFunction) ctor.newInstance();
            } catch (Exception e) {
                throw new ValidationException(
                    String.format("Failed to instantiate %s validator", fieldValidator.cls().getName()));
            }
            Boolean ok = f.validate(value);
            if (!ok) {
                throw new ValidationException(
                    String.format("Validator %s rejected value %s", 
                        fieldValidator.cls().getName(),
                        value.toString()
                    ));
            }
        }
    }

    private static void validateObject(Object obj, Class<?> objCls) throws ValidationException {
        TypeValidator typeValidator = objCls.getAnnotation(TypeValidator.class);
        if (typeValidator != null) {
            ValidationFunction f;
            try {
                Constructor<?> ctor = TypeUtils.getDefaultConstructor(typeValidator.cls());
                f = (ValidationFunction) ctor.newInstance();
            } catch (Exception e) {
                throw new ValidationException(
                    String.format("Failed to instantiate %s validator", typeValidator.cls().getName()));
            }
            Boolean ok = f.validate(obj);
            if (!ok) {
                throw new ValidationException(
                    String.format("Validator %s rejected value %s", 
                        typeValidator.cls().getName(),
                        obj.toString()
                    ));
            }
        }
    }
    
    private static Object deserializeObject(INode ast, Class<?> objCls) throws DeserializationException {
        Object resObj = createEmptyInstance(objCls);
        Map<String, INode> allValues = ensureNodeIsObject(ast);
        
        List<Field> allFields = TypeUtils.getAllFields(objCls);
        for (Field field: allFields) {
            String name = field.getName();
            INode val = allValues.get(name);
            // TODO: how about configurable nullable fields?
            if (val == null) {
                throw new DeserializationException(String.format("%s field is missed in serializer AST", name));
            }
            Object value = deserializeWoTypecast(val, field.getType()); 
            validateField(field, value);
            try {
                field.setAccessible(true);
                field.set(resObj, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new DeserializationException(
                    String.format("Failed to set %s field to value of type %s",
                                  field.getName(),
                                  value.getClass().getName()),
                    e
                );
            }
        }

        validateObject(resObj, objCls);
        return resObj;
    }

    private static Object deserializeWoTypecast(INode ast, Class<?> cls) throws DeserializationException {
        // TODO: find deserializers using annotations
        if (TypeUtils.isUnsupportedScalarClass(cls)) {
            throwUnsupportedClass(cls);
        }
        if (ast.isNull()) {
            return null;
        }
        if (TypeUtils.isSupportedScalarClass(cls)) {
            return deserializeScalar(ast, cls);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(ast, cls);
        }
        return deserializeObject(ast, cls);
    }

    private static void throwUnsupportedClass(Class<?> cls) throws DeserializationException {
        throw new DeserializationException(
            String.format("Deserialization of class %s is not supported", cls.getName()));
    }
}
