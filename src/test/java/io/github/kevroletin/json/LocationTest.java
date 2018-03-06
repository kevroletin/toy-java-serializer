package io.github.kevroletin.json;

import org.junit.Test;
import static org.junit.Assert.*;

public class LocationTest {

    @Test
    public void testEmpty() {
        assertEquals(
            "",
            Location.empty().toString()
        );
    }

    @Test
    public void testNonEmpty() {
        assertEquals(
            "{hello}[1]+",
            Location.empty().addField("hello").addIndex(1).addString("+").toString()
        );
    }

    @Test
    public void testImmutability() {
        Location base = Location.empty().addString("->");

        assertEquals(
            "->{hello}",
            base.addField("hello").toString()
        );

        assertEquals(
            "->[123]",
            base.addIndex(123).toString()
        );

        assertEquals(
            "-><-",
            base.addString("<-").toString()
        );

        assertEquals(
            "->",
            base.toString()
        );
    }

    @Test
    public void testJoin() {
        assertEquals(
            "[1]+[2]+[3]",
            Location.empty().addIndex(1).addIndex(2).addIndex(3).join("+")
        );
    }
}
