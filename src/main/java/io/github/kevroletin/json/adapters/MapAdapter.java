package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.StringNode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.utils.Maybe;
import io.github.kevroletin.json.utils.TypeUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapAdapter implements TypeAdapter<Map> {

    @Override
    public Maybe<Map> deserialize(Deserializer d, List<String> err, Location mapLoc, INode ast, Type type) {
        if (ast.isNull()) {
            return Maybe.just(null);
        }
        Class<?> arrCls = TypeUtils.getClassFromTypeNoThrow(err, mapLoc, type);
        if (arrCls == null) {
            return Maybe.nothing();
        }
        Type keyType = TypeUtils.getGenericParameterTypeNoThrow(err, mapLoc, 0, type);
        Type valueType = TypeUtils.getGenericParameterTypeNoThrow(err, mapLoc, 1, type);
        if (keyType == null || valueType == null) {
            return Maybe.nothing();
        }
        Class keyCls = TypeUtils.getClassFromTypeNoThrow(err, mapLoc, keyType);
        if (keyCls == null) {
            return Maybe.nothing();
        }

        Map<String, INode> astValues = d.ensureNodeIsObject(err, mapLoc, ast);
        if (astValues == null) {
            return Maybe.nothing();
        }

        List<String> keys = new ArrayList(astValues.keySet());
        Collections.sort(keys);

        Map res = new HashMap();
        for (String key: keys) {
            Location fieldLoc = mapLoc.addField(key);
            INode valAst = astValues.get(key);
            Maybe<Object> val = d.deserialize(err, fieldLoc, valAst, valueType);
            if (val.isNothing()) {
                continue;
            }
            if (keyCls == String.class) {
                res.put(key, val.get());
            } else {
                Maybe<Object> convertedKey = d.deserialize(err, fieldLoc, new StringNode(key), keyType);
                if (convertedKey.isJust()) {
                    res.put(convertedKey.get(), val.get());
                }
            }
        }

        return Maybe.just(res);
    }
    
}
