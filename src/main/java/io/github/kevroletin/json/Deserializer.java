package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private static Map<String, INode> ensureAstIsObject(INode ast) {
        if (!ast.isObject()) {
            throw new RuntimeException(String.format("Expected objet, got %s", ast.getClass().getName()));
        }
        return ((ObjectNode)ast).get();
    }
    
    public static void deserializeScalar(Object obj, Field field, INode value) {
        Class<?> fieldCls = field.getType();
        if (TypeUtils.isUnsupportedScalar(fieldCls)) {
            throw new RuntimeException(
                String.format("Deserialization into %s class is not supported.", fieldCls.getName()));
        }
        if (value.isNull()) {
            return; // Empty object already initialized with nulls
        }
        assert(TypeUtils.isSupportedScalarClass(fieldCls));
        
        try {
            field.set(obj, value.getUnsafe());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(
                String.format("Failed to set %s field to value of type %s",
                    field.getName(),
                    value.getUnsafe().getClass().getName())
                );
        }
    }
    
    public static <T> T deserializeObject(INode ast, Class<T> objCls) {
        T resObj = createEmptyInstance(objCls);
        Map<String, INode> allValues = ensureAstIsObject(ast);
        
        List<Field> allFields = TypeUtils.getAllFields(objCls);
        for (Field field: allFields) {
            String name = field.getName();
            INode val = allValues.get(name);
            // TODO: how about configurable nullable fields?
            if (val == null) {
                throw new RuntimeException(String.format("%s field is missed", name));
            }
            
            Class<?> fieldCls = field.getType();
            if (TypeUtils.isScalarClass(fieldCls)) {
                deserializeScalar(resObj, field, val);
            }
        }

        return resObj;
    }
    
    public static <T> T deserialize(INode ir, Class<T> cls) {
        return deserializeObject(ir, cls);
    }
}
