package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.utils.Maybe;
import io.github.kevroletin.json.utils.TypeUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter implements TypeAdapter<List> {

    @Override
    public Maybe<List> deserialize(Deserializer d, List<String> err, Location arrLoc, INode ast, Type type) {
        if (ast.isNull()) {
            return Maybe.just(null);
        }
        Type elemType = TypeUtils.getGenericParameterTypeNoThrow(err, arrLoc, 0, type);
        if (elemType == null) {
            return Maybe.nothing();
        }

        List<INode> astValues = d.ensureNodeIsArray(err, arrLoc, ast);
        if (astValues == null) {
            return Maybe.nothing();
        }

        List res = new ArrayList(astValues.size());
        for (int i = 0; i < astValues.size(); ++i) {
            INode valAst = astValues.get(i);
            Location valLoc = arrLoc.addIndex(i);
            Maybe<Object> val = d.deserialize(err, valLoc, valAst, elemType);
            if (val.isJust()) {
                res.add(val.get());
            } else {
                res.add(null);
            }
        }
        return Maybe.just(res);
    }
    
}
