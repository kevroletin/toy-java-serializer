package io.github.kevroletin.json;

import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class JsonParserTest {
    
    public JsonParserTest() {
    }

    @Test
    public void testParseInt() throws Exception {
        assertEquals(
            new ScalarNode(1),
            JsonParser.parse("1")
        );

        assertEquals(
            new ScalarNode(0),
            JsonParser.parse("0")
        );

        assertEquals(
            new ScalarNode(0),
            JsonParser.parse("-0")
        );

        assertEquals(
            new ScalarNode(10),
            JsonParser.parse("10")
        );

        assertEquals(
            new ScalarNode(-10),
            JsonParser.parse("-10")
        );
    }
    
    @Test(expected = JsonParsingException.class)
    public void testParseIntThrows() throws Exception {
        JsonParser.parse("01");
    }

    @Test(expected = JsonParsingException.class)
    public void testParseIntThrowsNegative() throws Exception {
        JsonParser.parse("-01");
    }

    @Test
    public void testParseDoble() throws Exception {
        assertEquals(
            new ScalarNode(1.0),
            JsonParser.parse("1.0")
        );

        assertEquals(
            new ScalarNode(-1.0),
            JsonParser.parse("-1.0")
        );

        assertEquals(
            new ScalarNode(-123.456),
            JsonParser.parse("-123.456")
        );

        assertEquals(
            new ScalarNode(-123.456e10),
            JsonParser.parse("-123.456e10")
        );

        assertEquals(
            new ScalarNode(-123.456e-10),
            JsonParser.parse("-123.456e-10")
        );

        assertEquals(
            new ScalarNode(123.456e10),
            JsonParser.parse("123.456E10")
        );

        assertEquals(
            new ScalarNode(123.456e-10),
            JsonParser.parse("123.456E-10")
        );

        assertEquals(
            new ScalarNode(123.456e0),
            JsonParser.parse("123.456E0")
        );

        assertEquals(
            new ScalarNode(123e10),
            JsonParser.parse("123E10")
        );

        assertEquals(
            new ScalarNode(123e-10),
            JsonParser.parse("123E-10")
        );

        assertEquals(
            new ScalarNode(123e0),
            JsonParser.parse("123E0")
        );
    }

    @Test
    public void testParseString() throws Exception {
        assertEquals(
            new ScalarNode("hello"),
            JsonParser.parse("\"hello\"")
        );

        assertEquals(
            new ScalarNode(""),
            JsonParser.parse("\"\"")
        );
        
        assertEquals(
            new ScalarNode("\b \f \n \r \t \""),
            JsonParser.parse("\"\\b \\f \\n \\r \\t \\\"\"")
        );

        assertEquals(
            new ScalarNode("\\b"),
            JsonParser.parse("\"\\\\b\"")
        );
    }

    @Test
    public void testParseStringQuotes() throws Exception {
        assertEquals(
            new ScalarNode("hello mr. \"White\""),
            JsonParser.parse("\"hello mr. \\\"White\\\"\"")
        );
    }

    @Test
    public void testParseBoolean() throws Exception {
        assertEquals(
            new ScalarNode(true),
            JsonParser.parse("true")
        );

        assertEquals(
            new ScalarNode(false),
            JsonParser.parse("false")
        );
    }

    @Test
    public void testParseNull() throws Exception {
        assertEquals(
            new ScalarNode(null),
            JsonParser.parse("null")
        );
    }

    @Test
    public void testParseArray() throws Exception {
        assertEquals(
            new ArrayNode(Arrays.asList()),
            JsonParser.parse("[]")
        );

        assertEquals(
            new ArrayNode(Arrays.asList(
                new ScalarNode(1),
                new ScalarNode(1.0),
                new ScalarNode("one")
            )),
            JsonParser.parse("[1, 1.0, \"one\"]")
        );
    }

    @Test
    public void testParseObject() throws Exception {
        Map<String, INode> m = new HashMap();
        m.put("x", new ScalarNode(1.0));
        m.put("y", new ScalarNode(2.0));
        assertEquals(
            new ObjectNode(m),
            JsonParser.parse("{\"x\": 1.0, \"y\": 2.0}")
        );
    }

    @Test
    public void testJsonPrint() throws JsonParsingException {
        assertEquals(
            "{\"a\":1,\"b\":2.0,\"c\":\"hello\"}",
            JsonParser.parse("{\"a\": 1, \"b\": 2.0, \"c\": \"hello\"}").toJson()
        );
    }

    @Test
    public void testJsonPrettyPrint() throws JsonParsingException {
        assertEquals(
            "{\n"
            + "  \"a\": 1,\n"
            + "  \"b\": 2.0,\n"
            + "  \"c\": \"hello\"\n"
            + "}",
            JsonParser.parse("{\"a\": 1, \"b\": 2.0, \"c\": \"hello\"}").toPrettyJson()
        );
    }

    @Test
    public void testJsonNestedPrint() throws JsonParsingException {
        assertEquals(
            "{\n"
            + "  \"child\": [\n"
            + "    {},\n"
            + "    [],\n"
            + "    {\n"
            + "      \"x\": true,\n"
            + "      \"y\": false\n"
            + "    },\n"
            + "    [\n"
            + "      1,\n"
            + "      2,\n"
            + "      3\n"
            + "    ]\n"
            + "  ]\n"
            + "}",
            JsonParser.parse("{\"child\": [{}, [], {\"x\": true, \"y\": false}, [1, 2, 3]]}").toPrettyJson()
        );
    }
}
