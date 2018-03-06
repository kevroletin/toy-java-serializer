package io.github.kevroletin.json;

import io.github.kevroletin.json.testHelpers.ScalarNode;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.DoubleNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.IntegerNode;
import io.github.kevroletin.json.AST.ObjectNode;
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
            new IntegerNode("1"),
            JsonParser.parse("1")
        );

        assertEquals(
            new IntegerNode("0"),
            JsonParser.parse("0")
        );

        assertEquals(
            new IntegerNode("-0"),
            JsonParser.parse("-0")
        );

        assertEquals(
            new IntegerNode("10"),
            JsonParser.parse("10")
        );

        assertEquals(
            new IntegerNode("-10"),
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
            ScalarNode.create(1.0),
            JsonParser.parse("1.0")
        );

        assertEquals(
            ScalarNode.create(-1.0),
            JsonParser.parse("-1.0")
        );

        assertEquals(
            ScalarNode.create(-123.456),
            JsonParser.parse("-123.456")
        );

        assertEquals(
            new DoubleNode("-123.456e10"),
            JsonParser.parse("-123.456e10")
        );

        assertEquals(
            new DoubleNode("-123.456e-10"),
            JsonParser.parse("-123.456e-10")
        );

        assertEquals(
            new DoubleNode("123.456e10"),
            JsonParser.parse("123.456e10")
        );

        assertEquals(
            new DoubleNode("123.456e-10"),
            JsonParser.parse("123.456e-10")
        );

        assertEquals(
            new DoubleNode("123.456e0"),
            JsonParser.parse("123.456e0")
        );

        assertEquals(
            new DoubleNode("123e10"),
            JsonParser.parse("123e10")
        );

        assertEquals(
            new DoubleNode("123e-10"),
            JsonParser.parse("123e-10")
        );

        assertEquals(
            new DoubleNode("123e0"),
            JsonParser.parse("123e0")
        );
    }

    @Test
    public void testParseString() throws Exception {
        assertEquals(
            ScalarNode.create("hello"),
            JsonParser.parse("\"hello\"")
        );

        assertEquals(
            ScalarNode.create(""),
            JsonParser.parse("\"\"")
        );

        assertEquals(
            ScalarNode.create("\b \f \n \r \t \""),
            JsonParser.parse("\"\\b \\f \\n \\r \\t \\\"\"")
        );

        assertEquals(
            ScalarNode.create("\\b"),
            JsonParser.parse("\"\\\\b\"")
        );
    }

    @Test
    public void testParseStringQuotes() throws Exception {
        assertEquals(
            ScalarNode.create("hello mr. \"White\""),
            JsonParser.parse("\"hello mr. \\\"White\\\"\"")
        );
    }

    @Test
    public void testParseBoolean() throws Exception {
        assertEquals(
            ScalarNode.create(true),
            JsonParser.parse("true")
        );

        assertEquals(
            ScalarNode.create(false),
            JsonParser.parse("false")
        );
    }

    @Test
    public void testParseNull() throws Exception {
        assertEquals(
            ScalarNode.create(null),
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
                ScalarNode.create(1),
                ScalarNode.create(1.0),
                ScalarNode.create("one")
            )),
            JsonParser.parse("[1, 1.0, \"one\"]")
        );
    }

    @Test
    public void testParseObject() throws Exception {
        Map<String, INode> m = new HashMap();
        m.put("x", ScalarNode.create(1.0));
        m.put("y", ScalarNode.create(2.0));
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
