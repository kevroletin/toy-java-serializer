package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.BooleanNode;
import io.github.kevroletin.json.AST.IntegerNode;
import io.github.kevroletin.json.AST.NullNode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class BooleanAdapterTest {
    
    @Test
    public void testDeserialize() {
        Deserializer d = new Deserializer();
        BooleanAdapter adapter = new BooleanAdapter(true);
        List<String> err = new ArrayList();

        assertEquals(
            Maybe.just(true),
            adapter.deserialize(d, err, Location.empty(), new BooleanNode(true), Boolean.class)
        );

        assertEquals(
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new IntegerNode("123"), Boolean.class)
        );

        assertEquals(
            Maybe.just(null),
            adapter.deserialize(d, err, Location.empty(), NullNode.getInstance(), Boolean.class)
        );
    }

    @Test
    public void testSerialize() {
    }
    
}
