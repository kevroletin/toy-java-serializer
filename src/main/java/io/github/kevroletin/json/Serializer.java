package io.github.kevroletin.json;

import io.github.kevroletin.json.utils.TypeUtils;
import io.github.kevroletin.json.AST.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import static java.util.Collections.newSetFromMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class Serializer {
    
    static public INode serializeArray(Object x) throws SerializationException {
        return serializeArray(x, new HashSet<>());
    }
    
    static public INode serializeArray(Object x, Set visited) throws SerializationException {
        assert(TypeUtils.isArray(x));
        markAsVisited(x, visited);
        
        ArrayList<INode> res = new ArrayList<>();

        for (int i = 0; i < Array.getLength(x); i++) {
            Object value = Array.get(x, i);
            res.add(serialize(value, visited));
        }

        clearVisited(x, visited);
        return new ArrayNode(res);
    }

    static public INode serializeObject(Object x) throws SerializationException {
        return serializeObject(x, newIdentetySet());
    }
    
    static public INode serializeObject(Object x, Set visited) throws SerializationException {
        markAsVisited(x, visited);

        List<Field> fields = TypeUtils.getAllFields(x.getClass());

        HashMap<String, INode> map = new HashMap<>();
        for (Field f: fields) {
            String name = f.getName();
            if (!isSpecialFieldName(name)) {
                f.setAccessible(true); // to access private fields
                // TODO: check annotations to skip or validate fields
                Object val;
                try {
                    val = f.get(x);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new SerializationException("Failed to get object field", e);
                }
                INode value = serialize(val, visited);
                map.put(name, value);
            }
        }

        clearVisited(x, visited);
        return new ObjectNode(map);
    }

    static private void throwUnsupportedClass(Class<?> cls) throws SerializationException {
        throw new SerializationException(
            String.format("Serialization of class %s is not supported", cls.getName()));
    }

    static public INode serialize(Object x) throws SerializationException {
        return serialize(x, newIdentetySet());
    }

    static public INode serialize(Object x, Set visited) throws SerializationException {
        // TODO: find serializers using annotations
        if (TypeUtils.isUnsupportedScalar(x)) {
            throwUnsupportedClass(x.getClass());
        }
        if (TypeUtils.isSupportedScalar(x)) {
            return new ScalarNode(x);
        }
        if (TypeUtils.isArray(x)) {
            return serializeArray(x, visited);
        }
        return serializeObject(x, visited);
    }

    private static boolean isSpecialFieldName(String x) {
        return x.equals("this") || x.startsWith("this$");
    }

    private static void markAsVisited(Object x, Set<Object> visited) throws SerializationException {
        if (visited.contains(x)) {
            // TODO: improve error message
            throw new SerializationException("Circular dependency");
        }
        visited.add(x);
    }

    private static void clearVisited(Object x, Set visited) {
        visited.remove(x);
    }

    // creates set which compares elevemnt by reference instead of .equals method
    private static Set newIdentetySet() {
        IdentityHashMap<Object, Boolean> c = new IdentityHashMap();
        return newSetFromMap(c);
    }
}
