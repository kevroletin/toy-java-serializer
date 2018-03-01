package io.github.kevroletin;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.DeserializationException;
import io.github.kevroletin.json.JsonParser;
import io.github.kevroletin.json.JsonParsingException;
import io.github.kevroletin.json.Serializer;
import io.github.kevroletin.json.SerializationException;

public class Json {
    static public String toJson(Object obj) throws SerializationException {
        return Serializer.serialize(obj).toJson();
    }

    static public String toPrettyJson(Object obj) throws SerializationException {
        return Serializer.serialize(obj).toPrettyJson();
    }

    static public <T> T fromJson(String str, Class<T> cls) throws JsonParsingException, DeserializationException {
        INode ast = JsonParser.parse(str);
        return Deserializer.deserialize(ast, cls);
    }
}
