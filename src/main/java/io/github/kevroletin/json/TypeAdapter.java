package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.utils.Maybe;
import java.util.List;

public interface TypeAdapter<T> {
    Maybe<T> deserialize(Deserializer d, List<String> errorsOut, Location loc, INode ast, Class<?> cls);
}
