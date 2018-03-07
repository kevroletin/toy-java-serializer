package io.github.kevroletin;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Config;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.JsonParser;
import io.github.kevroletin.json.Result;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.Serializer;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.SerializationException;
import java.lang.reflect.Type;

public class Json {
    final Deserializer deserializer;

    final Serializer serializer;

    final Config config;

    public Json() {
        this(new Config());
    }

    public Json(Config config) {
        this.config = config;
        this.deserializer = new Deserializer(config);
        this.serializer = new Serializer(config);
    }

    public String toJson(Object obj) throws SerializationException {
        return serializer.serialize(obj).toJson();
    }

    public String toPrettyJson(Object obj) throws SerializationException {
        return serializer.serialize(obj).toPrettyJson();
    }

    public <T> T fromJson(String str, Class<T> cls) throws JsonParsingException, DeserializationException
    {
        return (T) fromJson(str, (Type) cls);
    }

    public Object fromJson(String str, Type cls) throws JsonParsingException, DeserializationException
    {
        INode ast = JsonParser.parse(str);
        Result res = deserializer.deserialize(ast, cls);
        if (res.hasErrors()) {
            throw new DeserializationException(String.join("; ", res.getErrors()));
        }
        return res.orElse(null);
    }

    public Result<?> fromJsonNoThrow(String str, Type type) {
        INode ast;
        try {
            ast = JsonParser.parse(str);
        } catch (JsonParsingException ex) {
            return Result.error("Json parsing error: " + ex.getMessage());
        }
        Result res = deserializer.deserialize(ast, type);
        return res;
    }

    public <T> Result<T> fromJsonNoThrow(String str, Class<T> cls) {
        return (Result<T>) fromJsonNoThrow(str, (Type)cls);
    }

    public Json withTypeAdapter(Class<?> cls, TypeAdapter<?> adapter) {
        return new Json(config.withTypeAdapter(cls, adapter));
    }

    public Json withoutTypeAdapter(Class<?> cls) {
        return new Json(config.withoutTypeAdapter(cls));
    }
}
