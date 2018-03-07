package io.github.kevroletin.json;

import io.github.kevroletin.json.exceptions.SerializationException;
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
import java.util.Objects;
import java.util.Set;

public class Serializer {
    private final Config config;

    public Serializer(Config config) {
        this.config = config;
    }

    public Serializer() {
        this.config = new Config();
    }

    public Config getConfig() {
        return config;
    }

    public INode serialize(Object x) throws SerializationException {
        return serialize(x, newIdentetySet());
    }

    private INode serializeArray(Object x) throws SerializationException {
        return serializeArray(x, new HashSet<>());
    }

    private INode serializeArray(Object x, Set visited) throws SerializationException {
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

    private INode serializeObject(Object x) throws SerializationException {
        return serializeObject(x, newIdentetySet());
    }

    private INode serializeObject(Object x, Set visited) throws SerializationException {
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

    private void throwUnsupportedClass(Class<?> cls) throws SerializationException {
        throw new SerializationException(
            String.format("Serialization of class %s is not supported", cls.getName()));
    }

    private INode serialize(Object x, Set visited) throws SerializationException {
        // TODO: find serializers using annotations
        if (TypeUtils.isUnsupportedScalar(x)) {
            throwUnsupportedClass(x.getClass());
        } if (x == null) {
            return NullNode.getInstance();
        } if (TypeUtils.isSupportedScalar(x)) {
            // TODO: move into type adapters
            if (TypeUtils.isInteger(x)) {
                return new IntegerNode(String.valueOf(x));
            } else if (TypeUtils.isDouble(x)) {
                return new DoubleNode(String.valueOf(x));
            } else if (TypeUtils.isBoolean(x)) {
                return new BooleanNode((Boolean)x);
            } else if (TypeUtils.isString(x)) {
                return new StringNode((String)x);
            }
        } if (TypeUtils.isArray(x)) {
            return serializeArray(x, visited);
        }
        return serializeObject(x, visited);
    }

    private boolean isSpecialFieldName(String x) {
        return x.equals("this") || x.startsWith("this$");
    }

    private void markAsVisited(Object x, Set<Object> visited) throws SerializationException {
        if (visited.contains(x)) {
            // TODO: improve error message
            throw new SerializationException("Circular dependency");
        }
        visited.add(x);
    }

    private void clearVisited(Object x, Set visited) {
        visited.remove(x);
    }

    // creates set which compares elevemnt by reference instead of .equals method
    private Set newIdentetySet() {
        IdentityHashMap<Object, Boolean> c = new IdentityHashMap();
        return newSetFromMap(c);
    }

    @Override
    public String toString() {
        return "Serializer{" + "config=" + config + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.config);
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
        final Serializer other = (Serializer) obj;
        if (!Objects.equals(this.config, other.config)) {
            return false;
        }
        return true;
    }
}
