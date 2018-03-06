package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.BooleanNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.lang.reflect.Type;
import java.util.List;

public class BooleanAdapter extends BaseTypeAdapter<Boolean> {

    public BooleanAdapter(boolean canBeNull) {
        super(canBeNull);
    }

    @Override
    public Maybe<Boolean> deserializeBody(Deserializer d, List<String> errorsOut, Location loc, INode ast, Type type) {
        return Maybe.just(((BooleanNode)ast).get());
    }

    @Override
    protected Class getNodeType() {
        return BooleanNode.class;
    }
    
}
