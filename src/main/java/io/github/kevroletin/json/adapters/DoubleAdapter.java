package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.DoubleNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.util.List;

public class DoubleAdapter extends BaseTypeAdapter<Double> {

    public DoubleAdapter(boolean canBeNull) {
        super(canBeNull);
    }

    @Override
    public Maybe<Double> deserializeBody(Deserializer d, List<String> errorsOut, Location loc, INode ast, Class<Double> cls) {
        if (!d.expectNode(errorsOut, loc, ast, DoubleNode.class)) {
            return Maybe.nothing();
        }
        Double res;
        try {
            res = Double.parseDouble(((DoubleNode)ast).get());
            return Maybe.just(res);
        } catch(NumberFormatException ex) {
            d.pushError(errorsOut, loc, "Failed to parse Double %s", ex.getMessage());
            return Maybe.nothing();
        }
    }

    @Override
    protected Class getNodeType() {
        return DoubleNode.class;
    }
    
}
