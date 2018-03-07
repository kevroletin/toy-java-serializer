package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.utils.Maybe;
import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseTypeAdapter<T> implements TypeAdapter<T> {
    final boolean canBeNull;

    public BaseTypeAdapter(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    abstract protected Class getNodeType();

    abstract protected Maybe<T> deserializeBody(Deserializer d, List<String> errorsOut, Location loc, INode ast, Type type);

    @Override
    public Maybe<T> deserialize(Deserializer d, List<String> errorsOut, Location loc, INode ast, Type type) {
        if (ast.isNull()) {
            if (canBeNull) {
                return Maybe.just(null);
            } else {
                d.pushError(errorsOut, loc, "Can't be null");
                return Maybe.nothing();
            }
        }
        if (!d.expectNode(errorsOut, loc, ast, this.getNodeType())) {
            return Maybe.nothing();
        }
        return deserializeBody(d, errorsOut, loc, ast, type);
    }

}
