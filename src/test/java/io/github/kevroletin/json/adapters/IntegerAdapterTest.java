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

public class IntegerAdapterTest {
    
    @Test
    public void testDeserialize() {
        Deserializer d = new Deserializer();
        IntegerAdapter adapter = new IntegerAdapter(true);
        List<String> err = new ArrayList();

        assertEquals(
            Maybe.just(123),
            adapter.deserialize(d, err, Location.empty(), new IntegerNode("123"), Integer.class)
        );

        assertEquals(
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new StringNode("123"), Integer.class)
        );

        assertEquals(
            Maybe.just(null),
            adapter.deserialize(d, err, Location.empty(), NullNode.getInstance(), Integer.class)
        );

        assertEquals(
            "Too long number",
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new IntegerNode("12345678901234567890"), Integer.class)
        );
    }

    @Test
    public void testDeserializeNonNull() {
        Deserializer d = new Deserializer();
        IntegerAdapter adapter = new IntegerAdapter(false);
        List<String> err = new ArrayList();

        assertEquals(
            Maybe.just(123),
            adapter.deserialize(d, err, Location.empty(), new IntegerNode("123"), Integer.class)
        );

        assertEquals(
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new StringNode("123"), Integer.class)
        );

        assertEquals(
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), NullNode.getInstance(), Integer.class)
        );

        assertEquals(
            "Too long number",
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new IntegerNode("12345678901234567890"), Integer.class)
        );
    }

    @Test
    public void testSerialize() {
    }
    
}
