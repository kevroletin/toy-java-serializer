package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    class Point {
        public Double x, y;

        public Point(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Point() {}

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.x);
            hash = 79 * hash + Objects.hashCode(this.y);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Point other = (Point) obj;
            if (!Objects.equals(this.x, other.x)) {
                return false;
            }
            if (!Objects.equals(this.y, other.y)) {
                return false;
            }
            return true;
        }
    }
    
    public static class Cons<T> {
        public Cons next;
        
        public T value;

        public Cons(T value, Cons<T> next) {
            this.next = next;
            this.value = value;
        }
        
        public Cons() {}

        @Override
        public String toString() {
            return "Cons{" + "next=" + next + ", value=" + value + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.next);
            hash = 59 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Cons<?> other = (Cons<?>) obj;
            if (!Objects.equals(this.next, other.next)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }
    }
    
    @Test
    public void testSerializeObjectPoint() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(2.0));
        assertEquals(
            new ObjectNode(m),
            Serializer.serializeInner(new Point(1.0, 2.0))
        );
    }

    @Test
    public void testSerializeObjectPointWithNull() throws Exception {
        Map<String, INode> m = new HashMap<>();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(null));
        assertEquals(
            new ObjectNode(m),
            Serializer.serializeInner(new Point(1.0, null))
        );
    }
    
    @Test
    public void testSerializeObjectList() throws Exception {
        Cons<Integer> list = 
            new Cons<Integer>(1, 
                new Cons<Integer>(2, null));
        
        Map<String, INode> m2 = new HashMap<>();
        m2.put("value", new ScalarNode(2));
        m2.put("next", new ScalarNode(null));
        INode n2 = new ObjectNode(m2);
        
        Map<String, INode> m1 = new HashMap<>();
        m1.put("value", new ScalarNode(1));
        m1.put("next", n2);
        INode n1 = new ObjectNode(m1);
        
        assertEquals(
            n1,
            Serializer.serializeInner(list)
        );
    }

    static class Wrapper<T> {
        private T value;

        public Wrapper(T value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Wrapper<?> other = (Wrapper<?>) obj;
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Wrapper{" + "value=" + value + '}';
        }
    }

    @Test
    public void testSerializeObjectPrivateField() throws Exception {
        Wrapper<String> val = new Wrapper("Secret");
        
        Map<String, INode> m = new HashMap<>();
        m.put("value", new ScalarNode("Secret"));
            
        assertEquals(
            new ObjectNode(m),
            Serializer.serializeInner(val)
        );
    }
    
    @org.junit.Test(expected = RuntimeException.class)
    public void testSerializeObjectCircularDependency() throws Exception {
        Cons<Integer> lastNode = new Cons<Integer>(2, null);
        Cons<Integer> list = new Cons<Integer>(1, lastNode);
        lastNode.next = list;
        
        Serializer.serialize(list);
    }
}
