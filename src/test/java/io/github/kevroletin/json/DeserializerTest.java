package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
import io.github.kevroletin.json.TestTypes.BooleanWrapper;
import io.github.kevroletin.json.TestTypes.Point;
import io.github.kevroletin.json.TestTypes.IntegerWrapper;
import io.github.kevroletin.json.TestTypes.StringWrapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author behemoth
 */
public class DeserializerTest {
    
    public DeserializerTest() {
    }

    @Test
    public void testDeserializeObjectPoint() {
        Map<String, INode> m = new HashMap<>();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(2.0));

        assertEquals(
            new Point(1.0, 2.0),
            Deserializer.deserialize(new ObjectNode(m), Point.class)
        );
    }

    @Test
    public void testDeserializeObjectWithInteger() {
        Map<String, INode> m = new HashMap<>();
        m.put("value", new ScalarNode(1));

        assertEquals(
            new IntegerWrapper(1),
            Deserializer.deserialize(new ObjectNode(m), IntegerWrapper.class)
        );
    }

    @Test
    public void testDeserializeObjectWithString() {
        Map<String, INode> m = new HashMap<>();
        m.put("value", new ScalarNode("hello"));

        assertEquals(
            new StringWrapper("hello"),
            Deserializer.deserialize(new ObjectNode(m), StringWrapper.class)
        );
    }

    @Test
    public void testDeserializeObjectWithBoolean() {
        Map<String, INode> m = new HashMap<>();
        m.put("value", new ScalarNode(true));

        assertEquals(
            new BooleanWrapper(true),
            Deserializer.deserialize(new ObjectNode(m), BooleanWrapper.class)
        );
    }

    @Ignore
    public void testDeserializeObjectWithArray() {
    }

    @Ignore
    public void testDeserializeObjectWithList() {
    }

    @Ignore
    public void testDeserializeObjectWithOtherObjects() {
    }
    
    /* TODO:
     * + nested objects
     * + objects with unboxed fields (throw)
     * + object without public default constructor (throw)
     * + object with generics (throw)
     * + private fields
     * + innder objects (throw)
     */
}
