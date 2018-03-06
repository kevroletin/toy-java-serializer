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

public class Json {
    final Deserializer deserializer;

    final Serializer serializer;

    final Config config;

    public Json() {
        this.config = new Config();
        this.deserializer = new Deserializer(this.config);
        this.serializer = new Serializer(this.config);
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
        INode ast = JsonParser.parse(str);
        Result<T> res = deserializer.deserialize(ast, cls);
        if (res.hasErrors()) {
            throw new DeserializationException(String.join("; ", res.getErrors()));
        }
        return res.orElse(null);
    }

    public <T> Result<T> fromJsonNoThrow(String str, Class<T> cls) {
        INode ast;
        try {
            ast = JsonParser.parse(str);
        } catch (JsonParsingException ex) {
            return Result.error("Json parsing error: " + ex.getMessage());
        }
        Result<T> res = deserializer.deserialize(ast, cls);
        return res;
    }

    public Json withTypeAdapter(Class<?> cls, TypeAdapter<?> adapter) {
        return new Json(config.withTypeAdapter(cls, adapter));
    }

    // Unable to delete default deserializers
    public Json withoutTypeAdapter(Class<?> cls) {
        return new Json(config.withoutTypeAdapter(cls));
    }
}
