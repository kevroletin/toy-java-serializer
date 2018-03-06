package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.IntegerNode;
import io.github.kevroletin.json.AST.NullNode;
import io.github.kevroletin.json.AST.StringNode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class StringAdapterTest {
    
    public StringAdapterTest() {
    }

    @Test
    public void testDeserialize() {
        Deserializer d = new Deserializer();
        StringAdapter adapter = new StringAdapter(true);
        List<String> err = new ArrayList();

        assertEquals(
            Maybe.just("hello"),
            adapter.deserialize(d, err, Location.empty(), new StringNode("hello"), String.class)
        );

        assertEquals(
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new IntegerNode("123"), String.class)
        );

        assertEquals(
            Maybe.just(null),
            adapter.deserialize(d, err, Location.empty(), NullNode.getInstance(), String.class)
        );
    }

    @Test
    public void testSerialize() {
    }
    
}
