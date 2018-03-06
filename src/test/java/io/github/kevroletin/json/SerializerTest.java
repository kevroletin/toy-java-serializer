package io.github.kevroletin.json;

import io.github.kevroletin.json.testHelpers.ScalarNode;
import io.github.kevroletin.json.exceptions.SerializationException;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.TestTypes.IntCons;
import io.github.kevroletin.json.TestTypes.Point;
import io.github.kevroletin.json.TestTypes.GenericWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

public class SerializerTest {

    @org.junit.Test
    public void testSerializeArray() throws Exception {
        String[] strArr = {"hello", "world"};
        assertEquals(
            new Serializer().serialize(strArr),
            new ArrayNode(Arrays.asList(
                ScalarNode.create("hello"),
                ScalarNode.create("world")))
        );

        int[] intArr = {1, 2, 3};
        assertEquals(
            new Serializer().serialize(intArr),
            new ArrayNode(Arrays.asList(
                ScalarNode.create(1),
                ScalarNode.create(2),
                ScalarNode.create(3)))
        );

        double[] doubleArr = {1.0, 2.0, 3.0};
        assertEquals(
            new Serializer().serialize(doubleArr),
            new ArrayNode(Arrays.asList(
                              ScalarNode.create(1.0),
                              ScalarNode.create(2.0),
                              ScalarNode.create(3.0)))
            );

        boolean[] booleanArr = {true, false};
        assertEquals(
            new Serializer().serialize(booleanArr),
            new ArrayNode(Arrays.asList(
                ScalarNode.create(true),
                ScalarNode.create(false)))
        );

        Object[] nullArr = {null, null};
        assertEquals(
            new Serializer().serialize(nullArr),
            new ArrayNode(Arrays.asList(
                ScalarNode.create(null),
                ScalarNode.create(null)))
        );

        Object[] emptyArr = {};
        assertEquals(
            new Serializer().serialize(emptyArr),
            new ArrayNode(new ArrayList())
        );
    }

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeArrayThrows() throws Exception {
        char[] unsupportedArr = {'h', 'e', 'l', 'l', 'o'};
        new Serializer().serialize(unsupportedArr);
    }

    @Test
    public void testSerializeObjectPoint() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", ScalarNode.create(1.0));
        m.put("y", ScalarNode.create(2.0));
        assertEquals(
            new ObjectNode(m),
            new Serializer().serialize(new Point(1.0, 2.0))
        );
    }

    @Test
    public void testSerializeObjectPointWithNull() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", ScalarNode.create(1.0));
        m.put("y", ScalarNode.create(null));
        assertEquals(
            new ObjectNode(m),
            new Serializer().serialize(new Point(1.0, null))
        );
    }

    @Test
    public void testSerializeObjectList() throws Exception {
        IntCons list =
            new IntCons(1,
                new IntCons(2, null));

        assertEquals(
            IntCons.astFromList(Arrays.asList(1, 2)),
            new Serializer().serialize(list)
        );
    }

    @Test
    public void testSerializeObjectPrivateField() throws Exception {
        GenericWrapper<String> val = new GenericWrapper("Secret");

        Map<String, INode> m = new HashMap<>();
        m.put("value", ScalarNode.create("Secret"));

        assertEquals(
            new ObjectNode(m),
            new Serializer().serialize(val)
        );
    }

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeObjectCircularDependency() throws Exception {
        IntCons lastNode = new IntCons(2, null);
        IntCons list = new IntCons(1, lastNode);
        lastNode.next = list;

        new Serializer().serialize(list);
    }

    @Test
    public void testSerializeToJsonInteger() throws Exception {
        assertEquals(
            ScalarNode.create(1),
            new Serializer().serialize(1)
        );
    }

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeToJsonChar() throws Exception {
        new Serializer().serialize('a');
    }

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeToJsonList() throws Exception {
        new Serializer().serialize(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testSerializeToJsonArray() throws Exception {
        int[] arr = {1, 2, 3};
        assertEquals(
            new ArrayNode(Arrays.asList(
                ScalarNode.create(1),
                ScalarNode.create(2),
                ScalarNode.create(3)
            )),
            new Serializer().serialize(arr)
        );
    }

    @org.junit.Test
    public void testSerializeToJsonObject() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", ScalarNode.create(1.0));
        m.put("y", ScalarNode.create(2.0));
        assertEquals(
            new ObjectNode(m),
            new Serializer().serialize(new Point(1.0, 2.0))
        );
    }

}
