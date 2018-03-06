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
import java.lang.reflect.Type;
import java.util.Objects;
import io.github.kevroletin.json.annotations.SanitizerFactory;
import io.github.kevroletin.json.annotations.Sanitizer;

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
        Maybe<T> res = (Maybe<T>) deserializeIt(err, Location.empty(), ast, cls);
        return new Result(res, err);
    }

    public Result<?> deserialize(INode ast, Type type) {
        List<String> err = new ArrayList();
        Maybe res = deserializeIt(err, Location.empty(), ast, type);
        return new Result(res, err);
    }

    public <T> Result<T> deserialize(Location loc, INode ast, Type type) {
        List<String> err = new ArrayList();
        Maybe res = deserializeIt(err, loc, ast, type);
        return new Result(res, err);
    }

    public <T> Maybe<T> deserialize(List<String> err, Location loc, INode ast, Class<T> cls) {
        return (Maybe<T>) deserializeIt(err, loc, ast, cls);
    }

    public Maybe deserialize(List<String> err, Location loc, INode ast, Type type) {
        return deserializeIt(err, loc, ast, type);
    }

    private Maybe<?> deserializeIt(List<String> err, Location loc, INode ast, Type type) {
        Class cls = TypeUtils.getClassFromTypeNoThrow(err, loc, type);
        TypeAdapter adapter = config.typeAdapters.get(cls);
        if (adapter != null) {
            return adapter.deserialize(this, err, loc, ast, type);
        }
        TypeAdapter defaultAdapter = DefaultAdapters.getMap().get(cls);
        if (defaultAdapter != null) {
            return defaultAdapter.deserialize(this, err, loc, ast, type);
        }
        if (ast.isNull()) {
            return Maybe.just(null);
        }
        if (TypeUtils.isArrayClass(cls)) {
            return deserializeArray(err, loc, ast, type);
        }
        return deserializeObject(err, loc, ast, type);
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
        err.add(loc.toStringWith(frmt, args));
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

    public Map<String, INode> ensureNodeIsObject(List<String> err, Location loc, INode ast) {
        if (!ast.isObject()) {
            pushError(err, loc, "Expected object but got %s", ast.getClass().getName());
            return null;
        }
        return ((ObjectNode)ast).get();
    }

    public List<INode> ensureNodeIsArray(List<String> err, Location loc, INode ast) {
        if (!ast.isArray()) {
            pushError(err, loc, "Expected array but got %s", ast.getClass().getName());
            return null;
        }
        return ((ArrayNode)ast).get();
    }

    private Maybe<?> deserializeArray(List<String> err, Location arrLoc, INode ast, Type type) {
        Class<?> arrCls = TypeUtils.getClassFromTypeNoThrow(err, arrLoc, type);
        if (arrCls == null) {
            return Maybe.nothing();
        }
        Class<?> elemCls = arrCls.getComponentType();

        List<INode> astValues = ensureNodeIsArray(err, arrLoc, ast);
        if (astValues == null) {
            return Maybe.nothing();
        }

        Object res = Array.newInstance(elemCls, astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            Location valLoc = arrLoc.addIndex(i);
            Maybe<?> val = deserializeIt(err, valLoc, valAst, elemCls);
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
        return Maybe.just(res);
    }

    private <T> Maybe<T> sanitize(
        List<String> err, Location loc, Class<? extends SanitizerFactory> factoryCls, T value)
    {
        if (factoryCls == null) {
            pushError(err, loc, "@Adapter.cls is null");
            return Maybe.nothing();
        }
        SanitizerFactory<ValueSanitizer<T>> factory;
        try {
            Constructor<?> ctor = TypeUtils.getDefaultConstructor(factoryCls);
            factory = (SanitizerFactory) ctor.newInstance();
        } catch (Exception ex) {
            pushError(err, loc, "Failed to instantiate TypeAdapter usring %s: %s",
                      factoryCls.getName(), ex.getMessage());
            return Maybe.nothing();
        }
        ValueSanitizer<T> sanitizer = factory.create();
        if (sanitizer == null) {
            pushError(err, loc, "Sanitizer factory returned null");
            return Maybe.nothing();
        }
        return sanitizer.sanitize(err, loc, value);
    }

    private Maybe<?> deserializeObject(List<String> err, Location objLoc, INode ast, Type type) {
        Class<?> objCls = TypeUtils.getClassFromTypeNoThrow(err, objLoc, type);
        if (objCls == null) {
            return Maybe.nothing();
        }
        Object resObj = createEmptyInstance(err, objLoc, objCls);
        if (resObj == null) {
            return Maybe.nothing();
        }
        Map<String, INode> allValues = ensureNodeIsObject(err, objLoc, ast);
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

            Maybe<?> value = deserializeIt(err, fieldLoc, val, field.getGenericType());
            if (value.isNothing()) {
                continue;
            }
            Sanitizer ann = field.getAnnotation(Sanitizer.class);
            if (ann != null) {
                value = sanitize(err, objLoc, ann.cls(), value.get());
                if (value.isNothing()) {
                    continue;
                }
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
