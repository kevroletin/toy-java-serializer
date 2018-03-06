package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.IntegerNode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.lang.reflect.Type;
import java.util.List;

public class IntegerAdapter extends BaseTypeAdapter<Integer> {

    public IntegerAdapter(boolean canBeNull) {
        super(canBeNull);
    }

    @Override
    public Maybe<Integer> deserializeBody(Deserializer d, List<String> errorsOut, Location loc, INode ast, Type type) {
        Integer res;
        try {
            res = Integer.parseInt(((IntegerNode)ast).get());
            return Maybe.just(res);
        } catch(NumberFormatException ex) {
            d.pushError(errorsOut, loc, "Failed to parse Integer %s", ex.getMessage());
            return Maybe.nothing();
        }
    }

    @Override
    protected Class getNodeType() {
        return IntegerNode.class;
    }
    
}
