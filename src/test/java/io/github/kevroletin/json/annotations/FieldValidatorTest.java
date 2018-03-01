package io.github.kevroletin.json.annotations;

import io.github.kevroletin.Json;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.exceptions.SerializationException;
import io.github.kevroletin.json.exceptions.ValidationException;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

class PositiveInteger implements ValidationFunction {
    @Override
    public Boolean validate(Object data) throws ValidationException {
        return data instanceof Integer && ((Integer)data) > 0;
    }

    public PositiveInteger() {} 
}

class Point {
    public Integer x;

    @FieldValidator(cls = PositiveInteger.class)
    public Integer y;

    public Point(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Point() {}

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.x);
        hash = 97 * hash + Objects.hashCode(this.y);
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

    @Override
    public String toString() {
        return "Point{" + "x=" + x + ", y=" + y + '}';
    }
}

public class FieldValidatorTest {
    
    @Test
    public void testPositeveNumber() throws JsonParsingException, DeserializationException, SerializationException {
        Point p = Json.fromJson("{\"x\":-1,\"y\":1}", Point.class);
        assertEquals(new Point(-1, 1), p);
    }
    
    @Test(expected = ValidationException.class)
    public void testRejectNegativeNumber() throws JsonParsingException, DeserializationException, SerializationException {
        Point p = Json.fromJson("{\"x\":1,\"y\":-1}", Point.class);
    }
    
}
