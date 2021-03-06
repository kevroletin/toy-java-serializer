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

    public Deserializer withTypeAdapter(Class<?> cls, TypeAdapter<?> adapter) {
        return new Deserializer(config.withTypeAdapter(cls, adapter));
    }

    public Deserializer withoutTypeAdapter(Class<?> cls) {
        return new Deserializer(config.withoutTypeAdapter(cls));
    }

    public Config getConfig() {
        return config;
    }

    public <T> Result<T> deserialize(INode ast, Class<T> cls) {
        return (Result<T>) Deserializer.this.deserialize(ast, (Type)cls);
    }

    public Result<?> deserialize(INode ast, Type type) {
        List<String> err = new ArrayList();
        Maybe res = deserialize(err, Location.empty(), ast, type);
        return new Result(res, err);
    }

    public <T> Result<T> deserialize(Location loc, INode ast, Class<T> cls) {
        return Deserializer.this.deserialize(loc, ast, (Type)cls);
    }

    public <T> Result<T> deserialize(Location loc, INode ast, Type type) {
        List<String> err = new ArrayList();
        Maybe res = deserialize(err, loc, ast, type);
        return new Result(res, err);
    }

    public <T> Maybe<T> deserialize(List<String> err, Location loc, INode ast, Class<T> cls) {
        return (Maybe<T>) deserialize(err, loc, ast, (Type)cls);
    }

    public Maybe deserialize(List<String> err, Location loc, INode ast, Type type) {
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
        if (TypeUtils.isArrayType(type)) {
            return deserializeArray(err, loc, ast, type);
        }
        return deserializeObject(err, loc, ast, type);
    }

    public void pushError(List<String> err, Location loc, String frmt, Object... args) {
        err.add(loc.toStringWith(frmt, args));
    }

    public boolean expectNode(List<String> err, Location loc, INode node, Class<?> expectedNodeCls) {
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

    private Maybe deserializeArray(List<String> err, Location arrLoc, INode ast, Type type) {
        Class<?> arrCls = TypeUtils.getClassFromTypeNoThrow(err, arrLoc, type);
        if (arrCls == null) {
            return Maybe.nothing();
        }
        Type elemType = TypeUtils.getArrayElementType(err, arrLoc, type);
        if (elemType == null) {
            return Maybe.nothing();
        }
        Class elemCls = TypeUtils.getClassFromTypeNoThrow(err, arrLoc, elemType);
        if (elemCls == null) {
            return Maybe.nothing();
        }

        List<INode> astValues = ensureNodeIsArray(err, arrLoc, ast);
        if (astValues == null) {
            return Maybe.nothing();
        }

        Object res = Array.newInstance(elemCls, astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            Location valLoc = arrLoc.addIndex(i);
            Maybe<?> val = deserialize(err, valLoc, valAst, elemType);
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
            pushError(err, loc, "@Sanitizer.cls is null");
            return Maybe.nothing();
        }
        SanitizerFactory<ValueSanitizer<T>> factory;
        try {
            Constructor<?> ctor = TypeUtils.getDefaultConstructor(factoryCls);
            factory = (SanitizerFactory) ctor.newInstance();
        } catch (Exception ex) {
            pushError(err, loc, "Failed to instantiate Sanitizer usring %s: %s",
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

    private void deserealizeAndAssignField(
        List<String> err, Location fieldLoc, Object resObj, Field field, INode val)
    {
        String name = field.getName();
        // TODO: how about configurable nullable fields?
        if (val == null) {
            pushError(err, fieldLoc, "%s field is missed in serialized AST", name);
            return;
        }

        Maybe<?> value = deserialize(err, fieldLoc, val, field.getGenericType());
        if (value.isNothing()) {
            return;
        }
        Sanitizer ann = field.getAnnotation(Sanitizer.class);
        if (ann != null) {
            value = sanitize(err, fieldLoc, ann.cls(), value.get());
            if (value.isNothing()) {
                return;
            }
        }

        try {
            field.setAccessible(true);
            field.set(resObj, value.get());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            pushError(err, fieldLoc, "Failed to set value: %s", e.getMessage());
        }
    }

    private Maybe deserializeObject(List<String> err, Location objLoc, INode ast, Type type) {
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
            deserealizeAndAssignField(err, fieldLoc, resObj, field, val);
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
