package io.github.kevroletin.json.adapters;

import io.github.kevroletin.Json;
import io.github.kevroletin.json.TestTypes.Point;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.utils.TypeToken;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

public class ListAdapterTest {

    @Test
    public void testSimpleCases() throws JsonParsingException, DeserializationException {
        Json json = new Json();

        assertEquals(
            Arrays.asList(1, 2, 3),
            json.fromJson("[1, 2, 3]", new TypeToken<List<Integer>>() {}.getType())
        );

        assertEquals(
            Arrays.asList(),
            json.fromJson("[]", new TypeToken<List<Integer>>() {}.getType())
        );

        assertEquals(
            Arrays.asList("1", "2", "3"),
            json.fromJson("[\"1\", \"2\", \"3\"]", new TypeToken<List<String>>() {}.getType())
        );

        assertEquals(
            Arrays.asList(true, false),
            json.fromJson("[true, false]", new TypeToken<List<Boolean>>() {}.getType())
        );

        assertEquals(
            Arrays.asList(null, null),
            json.fromJson("[null, null]", new TypeToken<List<Object>>() {}.getType())
        );

        assertTrue(
            json.fromJsonNoThrow("[false, true]", new TypeToken<List<Integer>>() {}.getType()).hasErrors()
        );

        assertTrue(
            json.fromJsonNoThrow("[1, true, 3]", new TypeToken<List<Integer>>() {}.getType()).hasErrors()
        );
    }

    @Test
    public void testListOfObjects() throws JsonParsingException, DeserializationException {
        Json json = new Json();

        assertEquals(
            Arrays.asList(new Point(1.0, 2.0)),
            json.fromJson("[{\"x\": 1.0, \"y\": 2.0}]", new TypeToken<List<Point>>() {}.getType())
        );

        assertEquals(
            Arrays.asList(Arrays.asList(new Point(1.0, 2.0))),
            json.fromJson("[[{\"x\": 1.0, \"y\": 2.0}]]", new TypeToken<List<List<Point>>>() {}.getType())
        );
    }

    @Test
    public void testArrayVsList() throws JsonParsingException, DeserializationException {
        Json json = new Json();

        assertEquals(
            Arrays.asList(Arrays.asList(1)),
            json.fromJson("[[1]]", new TypeToken<List<List<Integer>>>() {}.getType())
        );

        List<Integer[]> res = (List<Integer[]>)
            json.fromJson("[[2]]", new TypeToken<List<Integer[]>>() {}.getType());

        assertEquals(res.size(), 1);
        assertEquals(res.get(0).length, 1);
        assertEquals(res.get(0)[0], (Integer)2);

        List<Integer>[] res2 = (List<Integer>[])
            json.fromJson("[[2]]", new TypeToken<List<Integer>[]>() {}.getType());
        assertEquals(res2.length, 1);
        assertEquals(res2[0].size(), 1);
        assertEquals(res2[0].get(0), (Integer)2);

        List<int[]>[] res3 = (List<int[]>[])
            json.fromJson("[[[2]]]", new TypeToken<List<int[]>[]>() {}.getType());
        assertEquals(res3.length, 1);
        assertEquals(res3[0].size(), 1);
        assertEquals(res3[0].get(0).length, 1);
        assertEquals(res3[0].get(0)[0], 2);
    }

    @Test
    public void testObjectVsList() throws JsonParsingException, DeserializationException {
    }

    static class ListWrapper {
        List<List<Point>> values;

        public ListWrapper(List<List<Point>> values) {
            this.values = values;
        }

        private ListWrapper() {}

        @Override
        public String toString() {
            return "ListWrapper{" + "values=" + values + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.values);
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
            final ListWrapper other = (ListWrapper) obj;
            if (!Objects.equals(this.values, other.values)) {
                return false;
            }
            return true;
        }

    }

    @Test
    public void testListAsAField() throws JsonParsingException, DeserializationException {
        Json json = new Json();

        assertEquals(
            new ListWrapper(Arrays.asList(Arrays.asList(new Point(1.0, 2.0)))),
            json.fromJson("{\"values\": [[{\"x\": 1.0, \"y\": 2.0}]]}", ListWrapper.class)
        );
    }
}
