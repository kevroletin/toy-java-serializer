package io.github.kevroletin;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.JsonParser;
import io.github.kevroletin.json.Result;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.Serializer;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.SerializationException;

public class Json {
    static public String toJson(Object obj) throws SerializationException {
        return Serializer.serialize(obj).toJson();
    }

    static public String toPrettyJson(Object obj) throws SerializationException {
        return Serializer.serialize(obj).toPrettyJson();
    }

    static public <T> T fromJson(String str, Class<T> cls) throws JsonParsingException, DeserializationException 
    {
        INode ast = JsonParser.parse(str);
        Result<T> res = new Deserializer().deserialize(ast, cls);
        if (res.hasErrors()) {
            throw new DeserializationException(String.join("; ", res.getErrors()));
        }
        return res.orElse(null);
    }

    static public <T> Result<T> fromJsonNoThrow(String str, Class<T> cls) {
        INode ast;
        try {
            ast = JsonParser.parse(str);
        } catch (JsonParsingException ex) {
            return Result.error("Json parsing error: " + ex.getMessage());
        }
        Result<T> res = new Deserializer().deserialize(ast, cls);
        return res;
    }

}
