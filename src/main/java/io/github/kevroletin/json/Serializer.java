package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Serializer {
	
	// TODO: detect cyclic references and fail
    static public INode serialize(Object x) {
        return null;
    }

    static public INode serializeArray(Object x) throws IllegalArgumentException, IllegalAccessException {
	    assert(TypeUtils.isArray(x));
	    
        ArrayList<INode> res = new ArrayList<>();

        for (int i = 0; i < Array.getLength(x); i++) {
            Object value = Array.get(x, i);
            res.add(serializeInner(value));
        }

        return new ArrayNode(res);
    }

    static public INode serializeList(Object x) throws IllegalArgumentException, IllegalAccessException {
	    assert(TypeUtils.isList(x));
	    
        List list = (List)x;
        ArrayList<INode> res = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            res.add(serializeInner(value));
        }

        return new ArrayNode(res);
    }

    static public INode serializeObject(Object x) throws IllegalArgumentException, IllegalAccessException {
        List<Field> fields = TypeUtils.getAllFields(x.getClass());

        HashMap<String, INode> map = new HashMap();
        for (Field f: fields) {
            String name = f.getName();
            if (!isSpecialFieldName(name)) {
                f.setAccessible(true); // to access private fields
                // TODO: check annotations to skip or validate fields
                INode value = serializeInner(f.get(x));
                map.put(name, value);
            }
        }
        return new ObjectNode(map);
    }

    private static void throwUnsupportedClass(Class<?> cls) {
        throw new RuntimeException(
            String.format("Serialization of class %s is not supported", cls.getName()));
    }

    private static boolean isSpecialFieldName(String x) {
        return x.equals("this") || x.startsWith("this$");
    }

    static public INode serializeInner(Object x) throws IllegalArgumentException, IllegalAccessException {
        // TODO: find serializers using annotations
        if (TypeUtils.isUnsupportedScalar(x)) {
            throwUnsupportedClass(x.getClass());
        }
        if (TypeUtils.isSupportedScalar(x)) {	    
            return new ScalarNode(x);
        }
        if (TypeUtils.isArray(x)) {
            return serializeArray(x);
        }
        if (TypeUtils.isList(x)) {
            return serializeList(x);
        }
        return serializeObject(x);
    }
}
