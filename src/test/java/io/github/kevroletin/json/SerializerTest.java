package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
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
            Serializer.serializeArray(strArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode("hello"),
                              new ScalarNode("world")))
            );
        
        int[] intArr = {1, 2, 3};
        assertEquals(
            Serializer.serializeArray(intArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(1),
                              new ScalarNode(2),
                              new ScalarNode(3)))
            );
        
        double[] doubleArr = {1.0, 2.0, 3.0};
        assertEquals(
            Serializer.serializeArray(doubleArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(1.0),
                              new ScalarNode(2.0),
                              new ScalarNode(3.0)))
            );

        boolean[] booleanArr = {true, false};
        assertEquals(
            Serializer.serializeArray(booleanArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(true),
                              new ScalarNode(false)))
            );

        Object[] nullArr = {null, null};
        assertEquals(
            Serializer.serializeArray(nullArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(null),
                              new ScalarNode(null)))
            );
        
        Object[] emptyArr = {};
        assertEquals(
            Serializer.serializeArray(emptyArr),
            new ArrayNode(new ArrayList())
            );
    }

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeArrayThrows() throws Exception {
        char[] unsupportedArr = {'h', 'e', 'l', 'l', 'o'};
        Serializer.serializeArray(unsupportedArr);
    }
    
    @Test
    public void testSerializeObjectPoint() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(2.0));
        assertEquals(
            new ObjectNode(m),
            Serializer.serialize(new Point(1.0, 2.0))
        );
    }

    @Test
    public void testSerializeObjectPointWithNull() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(null));
        assertEquals(
            new ObjectNode(m),
            Serializer.serialize(new Point(1.0, null))
        );
    }
    
    @Test
    public void testSerializeObjectList() throws Exception {
        IntCons list = 
            new IntCons(1, 
                new IntCons(2, null));
        
        assertEquals(
            IntCons.astFromList(Arrays.asList(1, 2)),
            Serializer.serialize(list)
        );
    }

    @Test
    public void testSerializeObjectPrivateField() throws Exception {
        GenericWrapper<String> val = new GenericWrapper("Secret");
        
        Map<String, INode> m = new HashMap<>();
        m.put("value", new ScalarNode("Secret"));
            
        assertEquals(
            new ObjectNode(m),
            Serializer.serialize(val)
        );
    }
    
    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeObjectCircularDependency() throws Exception {
        IntCons lastNode = new IntCons(2, null);
        IntCons list = new IntCons(1, lastNode);
        lastNode.next = list;
        
        Serializer.serialize(list);
    }
    
    @Test
    public void testSerializeToJsonInteger() throws Exception {
        assertEquals(
            new ScalarNode(1),
            Serializer.serialize(1)
        );
    }

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeToJsonChar() throws Exception {
        Serializer.serialize('a');
    }    

    @org.junit.Test(expected = SerializationException.class)
    public void testSerializeToJsonList() throws Exception {
        Serializer.serialize(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testSerializeToJsonArray() throws Exception {
        int[] arr = {1, 2, 3};
        assertEquals(
            new ArrayNode(Arrays.asList(
                new ScalarNode(1),
                new ScalarNode(2),
                new ScalarNode(3)
            )),
            Serializer.serialize(arr)
        );
    }
 
    @org.junit.Test
    public void testSerializeToJsonObject() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(2.0));
        assertEquals(
            new ObjectNode(m),
            Serializer.serialize(new Point(1.0, 2.0))
        );
    }    
    
}
