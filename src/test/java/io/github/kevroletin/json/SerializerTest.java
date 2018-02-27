package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.ScalarNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @org.junit.Test(expected = RuntimeException.class)
    public void testSerializeArrayThrows() throws Exception {
        char[] unsupportedArr = {'h', 'e', 'l', 'l', 'o'};
        Serializer.serializeArray(unsupportedArr);
    }

    @Test
    public void testSerializeList() throws Exception {
        List<String> strArr = Arrays.asList("hello", "world");
        assertEquals(
            Serializer.serializeList(strArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode("hello"),
                              new ScalarNode("world")))
            );

        List<Integer> intArr = Arrays.asList(1, 2, 3);
        assertEquals(
            Serializer.serializeList(intArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(1),
                              new ScalarNode(2),
                              new ScalarNode(3)))
            );

        List<Double> doubleArr = Arrays.asList(1.0, 2.0, 3.0);
        assertEquals(
            Serializer.serializeList(doubleArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(1.0),
                              new ScalarNode(2.0),
                              new ScalarNode(3.0)))
            );

        List<Boolean> booleanArr = Arrays.asList(true, false);
        assertEquals(
            Serializer.serializeList(booleanArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(true),
                              new ScalarNode(false)))
            );

        List<Object> nullArr = Arrays.asList(null, null);
        assertEquals(
            Serializer.serializeList(nullArr),
            new ArrayNode(Arrays.asList(
                              new ScalarNode(null),
                              new ScalarNode(null)))
            );

        assertEquals(
            Serializer.serializeList(new ArrayList()),
            new ArrayNode(new ArrayList())
            );

    }
    
    @org.junit.Test(expected = RuntimeException.class)
    public void testSerializeListThrows() throws Exception {
        List<Character> unsupportedArr = Arrays.asList('h', 'e', 'l', 'l', 'o');
        Serializer.serializeList(unsupportedArr);
    }
}
