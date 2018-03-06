package io.github.kevroletin.json;

import io.github.kevroletin.json.utils.TypeUtils;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.adapters.DefaultAdapters;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import io.github.kevroletin.json.utils.Maybe;
import java.util.ArrayList;
import java.util.Collections;
import io.github.kevroletin.json.annotations.TypeAdapterFactory;
import io.github.kevroletin.json.annotations.Adapter;
import java.util.Objects;

public class Deserializer {

    final Config config;

    public Deserializer(Config config) {
        this.config = config;
    }

    public Deserializer() {
        config = new Config();
    }

    public <T> Result<T> deserialize(INode ast, Class<T> cls) {
        List<String> err = new ArrayList();
        Maybe res = deserialize(err, Location.empty(), ast, cls);
        return new Result(res, err);
    }

    public <T> Result<T> deserialize(Location loc, INode ast, Class<T> cls) {
        List<String> err = new ArrayList();
        Maybe res = deserialize(err, loc, ast, cls);
        return new Result(res, err);
    }

    private TypeAdapter findAdapterInConfigs(Class cls) {
        TypeAdapter adapter = config.typeAdapters.get(cls);
        if (adapter != null) {
            return adapter;
        } else {
            return DefaultAdapters.getMap().get(cls);
        }
    }

    public <T> Maybe<T> deserialize(List<String> err, Location loc, INode ast, Class<T> cls) {
        Adapter ann = cls.getAnnotation(Adapter.class);
        if (ann != null) {
            return deserializeUsingAdapterFactory(err, loc, ann.cls(), ast, cls);
        }
        TypeAdapter adapter = findAdapterInConfigs(cls);
        if (adapter != null) {
            return adapter.deserialize(this, err, loc, ast, cls);
        }
        if (ast.isNull()) {
            return Maybe.just(null);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(err, loc, ast, cls);
        }
        return deserializeObject(err, loc, ast, cls);
    }

    public Deserializer withTypeAdapter(Class<?> cls, TypeAdapter<?> adapter) {
        return new Deserializer(config.withTypeAdapter(cls, adapter));
    }

    // Unable to delete default deserializers
    public Deserializer withoutTypeAdapter(Class<?> cls) {
        return new Deserializer(config.withoutTypeAdapter(cls));
    }

    public Config getConfig() {
        return config;
    }

    public void pushError(List<String> err, Location loc, String frmt, Object... args) {
        if (loc.isNull()) {
            err.add(String.format(frmt, args));
        } else {
            err.add(loc.toString() + " " + String.format(frmt, args));
        }
    }

    public <T> boolean expectNode(List<String> err, Location loc, INode node, Class<T> expectedNodeCls) {
        if (!node.getClass().equals(expectedNodeCls)) {
            pushError(err, loc, "Expecting node %s but got %s",
                      expectedNodeCls.getName(), node.getClass().getName());
            return false;
        }
        return true;
    }

    public <T> T createEmptyInstance(List<String> err, Location loc, Class<T> cls) {
        try {
            return (T) TypeUtils.getDefaultConstructor(cls).newInstance();
        } catch (Exception e) {
            pushError(err, loc,
                      "Failed to create new %s class instance. " +
                      "Public default constructor is not implemented or not accesable.",
                      cls.getName());
            return null;
        }
    }

    public Map<String, INode> ensureNodeIsObject(List<String> err, Location loc, Class<?> cls, INode ast) {
        if (!ast.isObject()) {
            pushError(err, loc, "Expected objet %s but got %s", 
                      cls.getName(), ast.getClass().getName());
            return null;
        }
        return ((ObjectNode)ast).get();
    }

    public List<INode> ensureNodeIsArray(List<String> err, Location loc, Class<?> cls, INode ast) {
        if (!ast.isArray()) {
            pushError(err, loc, "Expected array %s but got %s",
                      cls.getName(), ast.getClass().getName());
            return null;
        }
        return ((ArrayNode)ast).get();
    }

    public <T> Maybe<T> deserializeArray(List<String> err, Location arrLoc, INode ast, Class<T> arrCls) {
        assert(arrCls.isArray());
        Class<?> elemCls = arrCls.getComponentType();

        List<INode> astValues = ensureNodeIsArray(err, arrLoc, arrCls, ast);
        if (astValues == null) {
            return Maybe.nothing();
        }

        Object res = Array.newInstance(elemCls, astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            Location valLoc = arrLoc.addIndex(i);
            Maybe<?> val = deserialize(err, valLoc, valAst, elemCls);
            if (!val.isJust()) {
                continue;
            }
            try {
                Array.set(res, i, val.get());
            } catch (IllegalArgumentException e) {
                pushError(err, valLoc,
                          "Failed to set value: expected type %s but got %s",
                          i, 
                          elemCls.getName(), 
                          val.get().getClass().getName());
            }
        }
        return Maybe.just((T)res);
    }

    private <T> Maybe<T> deserializeUsingAdapterFactory(
        List<String> err, Location loc, Class<? extends TypeAdapterFactory> factoryCls, INode ast, Class<T> cls) 
    {
        if (factoryCls == null) {
            pushError(err, loc, "@Adapter.cls is null");
            return Maybe.nothing();
        }
        TypeAdapterFactory<TypeAdapter<T>> factory;
        try {
            Constructor<?> ctor = TypeUtils.getDefaultConstructor(factoryCls);
            factory = (TypeAdapterFactory) ctor.newInstance();
        } catch (Exception ex) {
            pushError(err, loc, "Failed to instantiate TypeAdapter usring %s: %s",
                      factoryCls.getName(), ex.getMessage());
            return Maybe.nothing();
        }
        TypeAdapter<T> adapter = factory.create();
        if (adapter == null) {
            pushError(err, loc, "Adapter factory returned null");
            return Maybe.nothing();
        }
        return adapter.deserialize(this, err, loc, ast, cls);
    }

    public <T> Maybe<T> deserializeConsideringAnnotation(
        List<String> err, Location loc, Adapter fieldAdapter, INode ast, Class<T> cls) 
    {
        if (fieldAdapter == null) {
            return deserialize(err, loc, ast, cls);
        } else {
            return deserializeUsingAdapterFactory(err, loc, fieldAdapter.cls(), ast, cls);
        }
    }

    public <T> Maybe<T> deserializeObject(List<String> err, Location objLoc, INode ast, Class<T> objCls) {
        T resObj = createEmptyInstance(err, objLoc, objCls);
        if (resObj == null) {
            return Maybe.nothing();
        }
        Map<String, INode> allValues = ensureNodeIsObject(err, objLoc, objCls, ast);
        if (allValues == null) {
            return Maybe.nothing();
        }

        List<Field> allFields = TypeUtils.getAllFields(objCls);
        Collections.sort(allFields, (a, b) -> a.getName().compareTo(b.getName()));

        for (Field field: allFields) {
            String name = field.getName();
            INode val = allValues.get(name);
            Location fieldLoc = objLoc.addField(name);
            // TODO: how about configurable nullable fields?
            if (val == null) {
                pushError(err, fieldLoc, "%s field is missed in serialized AST", name);
                continue;
            }

            Adapter ann = field.getAnnotation(Adapter.class);
            Maybe<?> value = deserializeConsideringAnnotation(err, fieldLoc, ann, val, field.getType());
            if (value.isNothing()) {
                continue;
            }

            try {
                field.setAccessible(true);
                field.set(resObj, value.get());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                pushError(err, fieldLoc, "Failed to set value: epected type %s but got %s",
                          field.getType().getName(), value.get().getClass().getName());
            }
        }
        return Maybe.just(resObj);
    }

    @Override
    public String toString() {
        return "Deserializer{" + "config=" + config + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.config);
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
        if (!Objects.equals(this.config, other.config)) {
            return false;
        }
        return true;
    }
}
