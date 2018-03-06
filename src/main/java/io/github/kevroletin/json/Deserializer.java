package io.github.kevroletin.json;

import io.github.kevroletin.json.utils.TypeUtils;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.BooleanNode;
import io.github.kevroletin.json.AST.DoubleNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.IntegerNode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.StringNode;
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

    public <T> Maybe<T> deserialize(List<String> err, Location loc, INode ast, Class<T> cls) {
        Adapter ann = cls.getAnnotation(Adapter.class);
        if (ann != null) {
            return deserializeUsingAdapter(err, loc, ann.cls(), ast, cls);
        }
        if (config.typeAdapters.containsKey(cls)) {
            return config.typeAdapters.get(cls).deserialize(this, err, loc, ast, cls);
        }
        if (TypeUtils.isUnsupportedScalarClass(cls)) {
            pushError(err, loc, "Deserialization of class %s is not supported", cls.getName());
            return Maybe.nothing();
        }
        if (ast.isNull()) {
            return Maybe.just(null);
        }
        if (TypeUtils.isSupportedScalarClass(cls)) {
            return deserializeScalar(err, loc, ast, cls);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(err, loc, ast, cls);
        }
        return deserializeObject(err, loc, ast, cls);
    }

    public Deserializer withTypeAdapter(Class<?> cls, TypeAdapter<?> adapter) {
        return new Deserializer(config.withTypeAdapter(cls, adapter));
    }

    public Deserializer withoutTypeAdapter(Class<?> cls) {
        return new Deserializer(config.withoutTypeAdapter(cls));
    }

    public void pushError(List<String> err, Location loc, String frmt, Object... args) {
        if (loc.isNull()) {
            err.add(String.format(frmt, args));
        } else {
            err.add(loc.toString() + " " + String.format(frmt, args));
        }
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

    public <T> Maybe<T> deserializeScalar(List<String> err, Location loc, INode value, Class<T> cls) {
        if (TypeUtils.isUnsupportedScalar(cls)) {
            pushError(err, loc, "Deserialization into %s class is not supported.", cls.getName());
            return Maybe.nothing();
        }
        if (value.isNull()) {
            return Maybe.just(null);
        } else {
            assert(TypeUtils.isSupportedScalarClass(cls));
            T res = null;
            // TODO: move into type adapters
            if (cls == Integer.class && value.isInteger()) {
                res = (T) Integer.valueOf(((IntegerNode)value).get());
            }
            else if (cls == Double.class && value.isDouble()) {
                res = (T) Double.valueOf(((DoubleNode)value).get());
            }
            else if (cls == Boolean.class && value.isBoolean()) {
                res = (T) (Boolean)((BooleanNode)value).get();
            }
            else if (cls == String.class && value.isString()) {
                res = (T) ((StringNode)value).get();
            } else {
                pushError(err, loc, "Expected %s but got %s node", cls.getName(), 
                          value.getClass().getName());
                return Maybe.nothing();
            }
            return Maybe.just(res);
        }
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

    private <T> Maybe<T> deserializeUsingAdapter(
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
            return deserializeUsingAdapter(err, loc, fieldAdapter.cls(), ast, cls);
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
}
