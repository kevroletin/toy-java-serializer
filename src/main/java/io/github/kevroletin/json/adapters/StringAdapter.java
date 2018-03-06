package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.StringNode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.util.List;

public class StringAdapter extends BaseTypeAdapter<String> {

    public StringAdapter(boolean canBeNull) {
        super(canBeNull);
    }

    @Override
    public Maybe<String> deserializeBody(Deserializer d, List<String> errorsOut, Location loc, INode ast, Class<String> cls) {
        return Maybe.just(((StringNode)ast).get());
    }

    @Override
    protected Class getNodeType() {
        return StringNode.class;
    }
}
