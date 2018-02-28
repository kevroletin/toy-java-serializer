package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Deserializer {

    private static <T> T createEmptyInstance(Class<T> cls) {
        try {
            return (T) TypeUtils.getDefaultConstructor(cls).newInstance();
        } catch (Exception e) {
            String msg = String.format(
                "Failed to create new %s class instance. " +
                "Public default constructor is not implemented or not accesable.",
                cls.getName());
            throw new RuntimeException(msg, e);
        }
    }
    
    private static Map<String, INode> ensureNodeIsObject(INode ast) {
        if (!ast.isObject()) {
            throw new RuntimeException(String.format("Expected object, got %s", ast.getClass().getName()));
        }
        return ((ObjectNode)ast).get();
    }

    private static List<INode> ensureNodeIsArray(INode ast) {
        if (!ast.isArray()) {
            throw new RuntimeException(String.format("Expected array or list, got %s", ast.getClass().getName()));
        }
        return ((ArrayNode)ast).get();
    }
    
    public static Object deserializeScalar(INode value, Class<?> cls) {
        if (TypeUtils.isUnsupportedScalar(cls)) {
            throw new RuntimeException(
                String.format("Deserialization into %s class is not supported.", cls.getName()));
        }
        if (value.isNull()) {
            return null;
        } else {
            assert(TypeUtils.isSupportedScalarClass(cls));
            Object res = value.getUnsafe();
            if (!res.getClass().equals(cls)) {
                throw new RuntimeException(
                    String.format("Failed to deserialize scalar: expected %s but got %s",
                                  cls.getName(), res.getClass().getName()));
            }
            return res;
        }
    }

    public static Object deserializeArray(INode ast, Class<?> arrCls) {
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
                throw new RuntimeException(
                    String.format("Failed to set array element #%d. Expected type %s but got %s",
                                  i,
                                  elemCls.getName(),
                                  val.getClass().getName()));
            }
        }

        return res;
    }
    
    public static Object deserializeObject(INode ast, Class<?> objCls) {
        Object resObj = createEmptyInstance(objCls);
        Map<String, INode> allValues = ensureNodeIsObject(ast);
        
        List<Field> allFields = TypeUtils.getAllFields(objCls);
        for (Field field: allFields) {
            String name = field.getName();
            INode val = allValues.get(name);
            // TODO: how about configurable nullable fields?
            if (val == null) {
                throw new RuntimeException(String.format("%s field is missed in serializer AST", name));
            }
            
            Object value = deserializeWoTypecast(val, field.getType()); 
            try {
                field.set(resObj, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(
                    String.format("Failed to set %s field to value of type %s",
                                  field.getName(),
                                  value.getClass().getName()));
            }
        }

        return resObj;
    }

    private static Object deserializeWoTypecast(INode ir, Class<?> cls) {
        // TODO: find deserializers using annotations
        if (TypeUtils.isUnsupportedScalarClass(cls)) {
            throwUnsupportedClass(cls);
        }
        if (TypeUtils.isSupportedScalarClass(cls)) {
            return deserializeScalar(ir, cls);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(ir, cls);
        }
        return deserializeObject(ir, cls);
    }

    public static <T> T deserialize(INode ir, Class<T> cls) {
        return (T) deserializeWoTypecast(ir, cls);
    }

    static private void throwUnsupportedClass(Class<?> cls) {
        throw new RuntimeException(
            String.format("Deserialization of class %s is not supported", cls.getName()));
    }
}
