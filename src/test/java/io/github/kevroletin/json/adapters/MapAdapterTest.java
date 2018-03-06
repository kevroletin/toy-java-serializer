package io.github.kevroletin.json.adapters;

import io.github.kevroletin.Json;
import io.github.kevroletin.TelephoneNumber;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.utils.TypeToken;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class MapAdapterTest {
    
    @Test
    public void testDeserialize() throws JsonParsingException, DeserializationException {
        Json json = new Json().withTypeAdapter(Map.class, new MapAdapter());

        Map<String, Integer> m1 = new HashMap();
        m1.put("f1", 1);
        m1.put("f2", 2);
        m1.put("f3", 3);
        assertEquals(
            m1,
            json.fromJson(
                "{\"f1\": 1, \"f2\": 2, \"f3\": 3}",
                new TypeToken<Map<String, Integer>>() {}.getType())
        );

        assertTrue(
            json.fromJsonNoThrow(
                "{\"f1\": 1, \"f2\": 2, \"f3\": 3}",
                new TypeToken<Map<String, Double>>() {}.getType()
            ).hasErrors()
        );
    }

    @Test
    public void testNonStringFields() throws JsonParsingException, DeserializationException {
        Json json = new Json().withTypeAdapter(Map.class, new MapAdapter())
                              .withTypeAdapter(TelephoneNumber.class, new TelephoneNumber.TelephoneNumberAdapter());

        Map<TelephoneNumber, Boolean> m1 = new HashMap();
        m1.put(new TelephoneNumber("71234567890"), true);
        m1.put(new TelephoneNumber("00000000000"), false);

        assertEquals(
            m1,
            json.fromJson(
                "{\"71234567890\": true, \"00000000000\": false}",
                new TypeToken<Map<TelephoneNumber, Boolean>>() {}.getType())
        );
    
        assertTrue(
            json.fromJsonNoThrow(
                "{\"bad\": true, \"00000000000\": false}",
                new TypeToken<Map<TelephoneNumber, Boolean>>() {}.getType()
            ).hasErrors()
        );
    }
}
