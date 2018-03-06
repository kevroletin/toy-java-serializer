package io.github.kevroletin.json;

import io.github.kevroletin.json.testHelpers.ScalarNode;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.TestTypes.BooleanWrapper;
import io.github.kevroletin.json.TestTypes.Point;
import io.github.kevroletin.json.TestTypes.IntegerWrapper;
import io.github.kevroletin.json.TestTypes.StringWrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class DeserializerTest {

    public DeserializerTest() {
    }

    @Test
    public void testDeserializeObjectPoint() throws DeserializationException {
        Map<String, INode> m = new HashMap<>();
        m.put("x", ScalarNode.create(1.0));
        m.put("y", ScalarNode.create(2.0));

        assertEquals(
            new Point(1.0, 2.0),
            new Deserializer().deserialize(new ObjectNode(m), Point.class).get()
        );
    }

    @Test
    public void testDeserializeObjectWithInteger() throws DeserializationException {
        Map<String, INode> m = new HashMap<>();
        m.put("value", ScalarNode.create(1));

        assertEquals(
            new IntegerWrapper(1),
            new Deserializer().deserialize(new ObjectNode(m), IntegerWrapper.class).get()
        );
    }

    @Test
    public void testDeserializeObjectWithString() throws DeserializationException {
        Map<String, INode> m = new HashMap<>();
        m.put("value", ScalarNode.create("hello"));

        assertEquals(
            new StringWrapper("hello"),
            new Deserializer().deserialize(new ObjectNode(m), StringWrapper.class).get()
        );
    }

    @Test
    public void testDeserializeObjectWithBoolean() throws DeserializationException {
        Map<String, INode> m = new HashMap<>();
        m.put("value", ScalarNode.create(true));

        assertEquals(
            new BooleanWrapper(true),
            new Deserializer().deserialize(new ObjectNode(m), BooleanWrapper.class).get()
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

    @Test
    public void testDeserializeScalar() throws DeserializationException {
        assertEquals(
            (Integer)1,
            new Deserializer().deserialize(ScalarNode.create(1), Integer.class).get()
        );

        assertEquals(
            "hello",
            new Deserializer().deserialize(ScalarNode.create("hello"), String.class).get()
        );

        assertEquals(
            true,
            new Deserializer().deserialize(ScalarNode.create(true), Boolean.class).get()
        );
    }

    @Test
    public void testDeserializeScalarWrongType() throws DeserializationException {
        Result<Integer> res = new Deserializer().deserialize(ScalarNode.create(true), Integer.class);
        assertTrue(res.hasErrors());
        assertTrue(res.getErrors().get(0).contains(
            "Expecting node io.github.kevroletin.json.AST.IntegerNode but got io.github.kevroletin.json.AST.BooleanNode"));
    }

    @Test
    public void testDeserializeArrayOfScalars() throws DeserializationException {
        INode ir = new ArrayNode(Arrays.asList(
            ScalarNode.create(1),
            ScalarNode.create(2),
            ScalarNode.create(3)
        ));
        Integer[] arr = {1, 2, 3};
        assertArrayEquals(
            arr,
            new Deserializer().deserialize(ir, Integer[].class).get()
        );
    }

    @Test
    public void testDeserializeEmptyArrayOfScalars() throws DeserializationException {
        INode ir = new ArrayNode(Arrays.asList());
        Integer[] arr = {};
        assertArrayEquals(
            arr,
            new Deserializer().deserialize(ir, Integer[].class).get()
        );
    }

    @Test
    public void testDeserializeArrayOfObjects() throws DeserializationException {
        INode ir = new ArrayNode(Arrays.asList(
            StringWrapper.astNode("hello"),
            StringWrapper.astNode("world")
        ));
        StringWrapper[] arr = {new StringWrapper("hello"), new StringWrapper("world")};
        assertArrayEquals(
            arr,
            new Deserializer().deserialize(ir, StringWrapper[].class).get()
        );
    }

    @Test
    public void testDeserializeArrayOfArraysOfObjects() throws DeserializationException {
        INode ir = new ArrayNode(Arrays.asList(
            new ArrayNode(Arrays.asList(
                IntegerWrapper.astNode(1),
                IntegerWrapper.astNode(2))),
            new ArrayNode(Arrays.asList(
                IntegerWrapper.astNode(3),
                IntegerWrapper.astNode(4)))
        ));
        IntegerWrapper[][] arr = {{new IntegerWrapper(1),
                                   new IntegerWrapper(2)},
                                  {new IntegerWrapper(3),
                                   new IntegerWrapper(4)}};
        assertArrayEquals(
            arr,
            new Deserializer().deserialize(ir, IntegerWrapper[][].class).get()
        );
    }

    static class InnerClass {
        String value;

        public InnerClass(String value) {
            this.value = value;
        }

        public InnerClass() {}

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.value);
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
            final InnerClass other = (InnerClass) obj;
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "InnerClass{" + "value=" + value + '}';
        }
    }

    static class OuterClass {
        InnerClass value;

        public OuterClass(InnerClass value) {
            this.value = value;
        }

        public OuterClass() {}

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.value);
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
            final OuterClass other = (OuterClass) obj;
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "OuterClass{" + "value=" + value + '}';
        }
    }

    @Test
    public void testDeserializeNestedObjects() throws DeserializationException {
        Map<String, INode> mInner = new HashMap();
        mInner.put("value", ScalarNode.create("secret"));
        Map<String, INode> mOuter = new HashMap();
        mOuter.put("value", new ObjectNode(mInner));
        INode ir = new ObjectNode(mOuter);
        OuterClass ans = new OuterClass(new InnerClass("secret"));

        assertEquals(
            ans,
            new Deserializer().deserialize(ir, OuterClass.class).get()
        );
    }

    @Test
    public void testDeserialize() {
    }
}
