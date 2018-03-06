package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.AST.DoubleNode;
import io.github.kevroletin.json.AST.NullNode;
import io.github.kevroletin.json.AST.StringNode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.utils.Maybe;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleAdapterTest {

    @Test
    public void testDeserialize() {
        Deserializer d = new Deserializer();
        DoubleAdapter adapter = new DoubleAdapter(true);
        List<String> err = new ArrayList();

        assertEquals(
            Maybe.just(123.0),
            adapter.deserialize(d, err, Location.empty(), new DoubleNode("123.0"), Double.class)
        );

        assertEquals(
            Maybe.nothing(),
            adapter.deserialize(d, err, Location.empty(), new StringNode("123"), Double.class)
        );

        assertEquals(
            Maybe.just(null),
            adapter.deserialize(d, err, Location.empty(), NullNode.getInstance(), Double.class)
        );
    }

    @Test
    public void testSerialize() {
    }

}
