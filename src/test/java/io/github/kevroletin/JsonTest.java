package io.github.kevroletin;

import io.github.kevroletin.json.Result;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.TestTypes.AllSupportedTypesWrapper;
import io.github.kevroletin.json.TestTypes.EmptyObject;
import io.github.kevroletin.json.TestTypes.IntCons;
import io.github.kevroletin.json.TestTypes.Point;
import io.github.kevroletin.json.TestTypes.PrivateConstructor;
import io.github.kevroletin.json.TestTypes.PrivateField;
import io.github.kevroletin.json.utils.Maybe;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class JsonTest {
    
    public JsonTest() {
    }

    @Test
    public void testJsonTrivialCases() throws Exception {
        assertEquals((Integer)10, Json.fromJson("10", Integer.class));
        assertEquals((Double)10.0, Json.fromJson("10.0", Double.class));
        assertEquals("hello", Json.fromJson("\"hello\"", String.class));
        assertEquals(true, Json.fromJson("true", Boolean.class));
    }

    @Test
    public void testJsonObject() throws Exception {
        String str = "{\"x\":1.0,\"y\":2.0}";
        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, Point.class))
        );
    }

    @Test 
    public void testJsonArray() {
        Result<Boolean[]> res = Json.fromJsonNoThrow("[1, true, 3]", Boolean[].class);
        assertEquals(
            Arrays.asList(
                "[0] Expected java.lang.Boolean but got java.lang.Integer",
                "[2] Expected java.lang.Boolean but got java.lang.Integer"),
            res.getErrors()
        );
        assertTrue(res.hasValue());
        assertArrayEquals(new Boolean[] {null, true, null}, res.get());
    }

    @Test
    public void testAssignEveryType() throws Exception {
        class TestData {
            public Class cls;
            public Object res;
            public String json;

            public TestData(Class cls, Object res, String json) {
                this.cls = cls;
                this.res = res;
                this.json = json;
            }
        }
        TestData[] tests = new TestData[] {
            new TestData(Integer.class, (Integer)10, "10"),
            new TestData(String.class, "10", "\"10\""),
            new TestData(Boolean.class, true, "true"),
            new TestData(Double.class, 10.0, "10.0"),
            new TestData(Point.class, new Point(1.0, 2.0), "{\"x\":1.0,\"y\":2.0}"),
            new TestData(Integer[].class, new Integer[] { 10 }, "[10]"),
            new TestData(String[].class, new String[] { "10" }, "[\"10\"]"),
            new TestData(Boolean[].class, new Boolean[] { true }, "[true]"),
            new TestData(Double[].class, new Double[] { 10.0 }, "[10.0]"),
            new TestData(Point[].class, new Object[] { new Point(1.0, 2.0) }, "[{\"x\":1.0,\"y\":2.0}]")
        };

        for (TestData to: tests) {
            for (TestData from: tests) {
                String msg = String.format("%s - %s", to.cls.toString(), from.cls.toString());
                if (to != from) {
                    Result res = Json.fromJsonNoThrow(from.json, to.cls);
                    assertTrue(res.hasErrors());
                    assertEquals(1, res.getErrors().size());
                    System.out.println(res.getErrors().get(0));
                } else {
                    if (from.cls.isArray()) {
                        assertArrayEquals(msg, 
                            (Object[])from.res, 
                            (Object[])Json.fromJson(from.json, from.cls) );
                    } else {
                        assertEquals(msg, 
                            from.res, 
                            Json.fromJson(from.json, from.cls) );
                    }
                }
            }
        }
    }

    @Test
    public void testJsonNestedObjects() throws Exception {
        String str = "{\"next\":{\"next\":null,\"value\":2},\"value\":1}";
        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, IntCons.class))
        );
    }

    String threeNestedObjectsJson = 
          "{\n"
        + "  \"booleanArray\": [\n"
        + "    true,\n"
        + "    false\n"
        + "  ],\n"
        + "  \"booleanValue\": true,\n"
        + "  \"doubleArray\": [\n"
        + "    1.0,\n"
        + "    2.0,\n"
        + "    3.0\n"
        + "  ],\n"
        + "  \"doubleValue\": 3.0,\n"
        + "  \"intArray\": [\n"
        + "    1,\n"
        + "    2,\n"
        + "    3\n"
        + "  ],\n"
        + "  \"intValue\": 3,\n"
        + "  \"object\": {\n"
        + "    \"booleanArray\": [\n"
        + "      true,\n"
        + "      false\n"
        + "    ],\n"
        + "    \"booleanValue\": true,\n"
        + "    \"doubleArray\": [\n"
        + "      1.0,\n"
        + "      2.0,\n"
        + "      3.0\n"
        + "    ],\n"
        + "    \"doubleValue\": 1.0,\n"
        + "    \"intArray\": [\n"
        + "      1,\n"
        + "      2,\n"
        + "      3\n"
        + "    ],\n"
        + "    \"intValue\": 1,\n"
        + "    \"object\": null,\n"
        + "    \"objectArray\": [],\n"
        + "    \"stringArray\": [\n"
        + "      \"hello\",\n"
        + "      \"world\"\n"
        + "    ],\n"
        + "    \"stringValue\": \"one\"\n"
        + "  },\n"
        + "  \"objectArray\": [\n"
        + "    {\n"
        + "      \"booleanArray\": [\n"
        + "        true,\n"
        + "        false\n"
        + "      ],\n"
        + "      \"booleanValue\": false,\n"
        + "      \"doubleArray\": [\n"
        + "        1.0,\n"
        + "        2.0,\n"
        + "        3.0\n"
        + "      ],\n"
        + "      \"doubleValue\": 2.0,\n"
        + "      \"intArray\": [\n"
        + "        1,\n"
        + "        2,\n"
        + "        3\n"
        + "      ],\n"
        + "      \"intValue\": 2,\n"
        + "      \"object\": null,\n"
        + "      \"objectArray\": [],\n"
        + "      \"stringArray\": [\n"
        + "        \"hello\",\n"
        + "        \"world\"\n"
        + "      ],\n"
        + "      \"stringValue\": \"two\"\n"
        + "    }\n"
        + "  ],\n"
        + "  \"stringArray\": [\n"
        + "    \"hello\",\n"
        + "    \"world\"\n"
        + "  ],\n"
        + "  \"stringValue\": \"three\"\n"
        + "}";

    @Test
    public void testAllTypesObject() throws Exception {
        AllSupportedTypesWrapper obj1 = new AllSupportedTypesWrapper();
        AllSupportedTypesWrapper obj2 = new AllSupportedTypesWrapper();
        AllSupportedTypesWrapper obj3 = new AllSupportedTypesWrapper();

        obj1.intValue = 1;
        obj1.doubleValue = 1.0;
        obj1.stringValue = "one";
        obj1.booleanValue = true;
        obj1.object = null;
        obj1.intArray = new Integer[] {1, 2, 3};
        obj1.doubleArray = new Double[] {1.0, 2.0, 3.0};
        obj1.stringArray = new String[] {"hello", "world"};
        obj1.booleanArray = new Boolean[] {true, false};
        obj1.objectArray = new AllSupportedTypesWrapper[] {};

        obj2.intValue = 2;
        obj2.doubleValue = 2.0;
        obj2.stringValue = "two";
        obj2.booleanValue = false;
        obj2.object = null;
        obj2.intArray = new Integer[] {1, 2, 3};
        obj2.doubleArray = new Double[] {1.0, 2.0, 3.0};
        obj2.stringArray = new String[] {"hello", "world"};
        obj2.booleanArray = new Boolean[] {true, false};
        obj2.objectArray = new AllSupportedTypesWrapper[] {};

        obj3.intValue = 3;
        obj3.doubleValue = 3.0;
        obj3.stringValue = "three";
        obj3.booleanValue = true;
        obj3.object = obj1;
        obj3.intArray = new Integer[] {1, 2, 3};
        obj3.doubleArray = new Double[] {1.0, 2.0, 3.0};
        obj3.stringArray = new String[] {"hello", "world"};
        obj3.booleanArray = new Boolean[] {true, false};
        obj3.objectArray = new AllSupportedTypesWrapper[] {obj2};

        assertEquals(
            threeNestedObjectsJson, 
            Json.toPrettyJson(obj3)
        );

        assertEquals(
            threeNestedObjectsJson,
            Json.toPrettyJson(Json.fromJson(threeNestedObjectsJson, AllSupportedTypesWrapper.class))
        );
    }

    @Test
    public void testAllTypesObjectWithNulls() throws Exception {
        AllSupportedTypesWrapper obj1 = new AllSupportedTypesWrapper();

        obj1.intValue = null;
        obj1.doubleValue = null;
        obj1.stringValue = null;
        obj1.booleanValue = null;
        obj1.object = null;
        obj1.intArray = null;
        obj1.doubleArray = null;
        obj1.stringArray = null;
        obj1.booleanArray = null;
        obj1.objectArray = null;

        String str = "{\"booleanArray\":null,\"booleanValue\":null,\"doubleArray\":null,\"doubleValue\":null,\"intArray\":null,\"intValue\":null,\"object\":null,\"objectArray\":null,\"stringArray\":null,\"stringValue\":null}";

        assertEquals(
            str, 
            Json.toJson(obj1)
        );

        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, AllSupportedTypesWrapper.class))
        );
    }

    @Test
    public void testArrayWithNulls() throws Exception {
        AllSupportedTypesWrapper obj1 = new AllSupportedTypesWrapper();

        obj1.intValue = null;
        obj1.doubleValue = null;
        obj1.stringValue = null;
        obj1.booleanValue = null;
        obj1.object = null;
        obj1.intArray = new Integer[] {null, null, null};
        obj1.doubleArray = new Double[] {null, null, null};
        obj1.stringArray = new String[] {null, null};
        obj1.booleanArray = new Boolean[] {null, null};
        obj1.objectArray = new AllSupportedTypesWrapper[] {null};
        
        String str = "{\"booleanArray\":[null,null],\"booleanValue\":null,\"doubleArray\":[null,null,null],\"doubleValue\":null,\"intArray\":[null,null,null],\"intValue\":null,\"object\":null,\"objectArray\":[null],\"stringArray\":[null,null],\"stringValue\":null}";

        assertEquals(
            str, 
            Json.toJson(obj1)
        );

        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, AllSupportedTypesWrapper.class))
        );
    }

    @Test
    public void testEmptyArrays() throws Exception {
        AllSupportedTypesWrapper obj1 = new AllSupportedTypesWrapper();

        obj1.intValue = null;
        obj1.doubleValue = null;
        obj1.stringValue = null;
        obj1.booleanValue = null;
        obj1.object = null;
        obj1.intArray = new Integer[] {};
        obj1.doubleArray = new Double[] {};
        obj1.stringArray = new String[] {};
        obj1.booleanArray = new Boolean[] {};
        obj1.objectArray = new AllSupportedTypesWrapper[] {};

        String str = "{\"booleanArray\":[],\"booleanValue\":null,\"doubleArray\":[],\"doubleValue\":null,\"intArray\":[],\"intValue\":null,\"object\":null,\"objectArray\":[],\"stringArray\":[],\"stringValue\":null}";

        assertEquals(
            str, 
            Json.toJson(obj1)
        );

        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, AllSupportedTypesWrapper.class))
        );
    }

    @Test
    public void testSpacesInJsonInput() throws Exception {
        AllSupportedTypesWrapper obj1 = new AllSupportedTypesWrapper();

        obj1.intValue = 1;
        obj1.doubleValue = 1.0;
        obj1.stringValue = "one";
        obj1.booleanValue = true;
        obj1.object = null;
        obj1.intArray = new Integer[] {1, 2, 3};
        obj1.doubleArray = new Double[] {1.0, 2.0, 3.0};
        obj1.stringArray = new String[] {"hello", "world"};
        obj1.booleanArray = new Boolean[] {true, false};
        obj1.objectArray = new AllSupportedTypesWrapper[] {};

        String strWithSpaces = " { \"booleanArray\" :  [ true , false ] , \"booleanValue\" : true , \"doubleArray\" :  [ 1.0 , 2.0 , 3.0 ] , \"doubleValue\" : 1.0 , \"intArray\" :  [ 1 , 2 , 3 ] , \"intValue\" : 1 , \"object\" : null , \"objectArray\" :  [ ] , \"stringArray\" :  [ \"hello\" , \"world\" ]  , \"stringValue\" : \"one\" } ";
        String str = "{\"booleanArray\":[true,false],\"booleanValue\":true,\"doubleArray\":[1.0,2.0,3.0],\"doubleValue\":1.0,\"intArray\":[1,2,3],\"intValue\":1,\"object\":null,\"objectArray\":[],\"stringArray\":[\"hello\",\"world\"],\"stringValue\":\"one\"}";

        assertEquals(
            str, 
            Json.toJson(obj1)
        );

        assertEquals(
            str,
            Json.toJson(Json.fromJson(strWithSpaces, AllSupportedTypesWrapper.class))
        );
    }

    @Test
    public void testMultipleTypeErrors() throws Exception {
        String str = "{\"x\":true,\"y\":false}";

        assertEquals(
            Json.fromJsonNoThrow(str, Point.class),
            new Result(
                Maybe.just(new Point(null, null)),
                Arrays.asList(
                    "{x} Expected java.lang.Double but got java.lang.Boolean",
                    "{y} Expected java.lang.Double but got java.lang.Boolean"))
        );
    }

    @Test
    public void testMultipleNestedErrors() throws Exception {
        Result<AllSupportedTypesWrapper> res1 = Json.fromJsonNoThrow(
            threeNestedObjectsJson.replaceAll("2.0", "true"),
            AllSupportedTypesWrapper.class
        );

        List<String> ans1 = Arrays.asList(
            "{doubleArray}[1] Expected java.lang.Double but got java.lang.Boolean",
            "{object}{doubleArray}[1] Expected java.lang.Double but got java.lang.Boolean",
            "{objectArray}[0]{doubleArray}[1] Expected java.lang.Double but got java.lang.Boolean",
            "{objectArray}[0]{doubleValue} Expected java.lang.Double but got java.lang.Boolean");
        assertEquals(ans1, res1.getErrors());

        Result<AllSupportedTypesWrapper> res2 = Json.fromJsonNoThrow(
            threeNestedObjectsJson.replaceAll("true", "0"),
            AllSupportedTypesWrapper.class
        );

        List<String> ans2 = Arrays.asList(
            "{booleanArray}[0] Expected java.lang.Boolean but got java.lang.Integer",
            "{booleanValue} Expected java.lang.Boolean but got java.lang.Integer",
            "{object}{booleanArray}[0] Expected java.lang.Boolean but got java.lang.Integer",
            "{object}{booleanValue} Expected java.lang.Boolean but got java.lang.Integer",
            "{objectArray}[0]{booleanArray}[0] Expected java.lang.Boolean but got java.lang.Integer");
        assertEquals(ans2, res2.getErrors());
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertEquals("{}", Json.toJson(Json.fromJson("{}", EmptyObject.class)));
    }

    @Test(expected = DeserializationException.class)
    public void testMissedFields() throws Exception {
        String str = "{\"x\":1.0}";
        Json.toJson(Json.fromJson(str, Point.class));
    }

    public class Nested { }

    @Test(expected = DeserializationException.class)
    public void testNestedObject() throws Exception {
        Json.toJson(Json.fromJson("{}", Nested.class));
    }

    static class NestedStatic { }

    @Test()
    public void testNestedStaticObject() throws Exception {
        Json.toJson(Json.fromJson("{}", NestedStatic.class));
    }

    static class UnboxedField {
        int value;
    }

    @Test(expected = DeserializationException.class)
    public void testUnboxedField() throws Exception {
        Json.toJson(Json.fromJson("{\"value\": 10}", UnboxedField.class));
    }

    static class Father {
        public Integer value;

        public Father(Integer value) {
            this.value = value;
        }

        public Father() {}

        @Override
        public String toString() {
            return "Father{" + "value=" + value + '}';
        }
    }

    static class Child extends Father {
        public Child(Integer value) {
            super(value);
        }

        public Child() {}
    }

    @Test()
    public void testInheritance() throws Exception {
        String str = "{\"value\":10}";
        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, Child.class))
        );
    }

    // TODO: ensure it works, add more tests!
    @Test()
    public void testPrivateField() throws Exception {
        String str = "{\"value\":10}";
        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, PrivateField.class))
        );
    }

    // TODO: ensure it works, add more tests!
    @Test()
    public void testPrivateConstructor() throws Exception {
        String str = "{\"value\":10}";
        assertEquals(
            str,
            Json.toJson(Json.fromJson(str, PrivateConstructor.class))
        );
    }
}
