package io.github.kevroletin.json.annotations;

import io.github.kevroletin.Json;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.Result;
import io.github.kevroletin.json.ValueSanitizer;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.utils.Maybe;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

class PositiveIntegerSanitizer implements ValueSanitizer<Integer> {

    @Override
    public Maybe<Integer> sanitize(List<String> err, Location loc, Integer val) {
        if (val == null || val <= 0) {
            err.add(loc.toStringWith("Integer should be positive"));
            return Maybe.nothing();
        }
        return Maybe.just(val);
    }

}

class PositiveIntegerValidator implements SanitizerFactory<PositiveIntegerSanitizer> {

    @Override
    public PositiveIntegerSanitizer create() {
        return new PositiveIntegerSanitizer();
    }

}

class Point {
    public Integer x;

    @Sanitizer(cls = PositiveIntegerValidator.class)
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

public class AdapterAnnotationsTest {
    
    @Test
    public void testPositeveNumber() throws JsonParsingException {
        Result<Point> p = new Json().fromJsonNoThrow("{\"x\":-1,\"y\":1}", Point.class);
        for (String x: p.getErrors()) {
            System.out.println(x);
        }
        assertTrue(p.hasValue());
        assertEquals(new Point(-1, 1), p.get());
    }
    
    @Test
    public void testRejectNegativeNumber() throws JsonParsingException {
        Result<Point> p = new Json().fromJsonNoThrow("{\"x\":1,\"y\":-1}", Point.class);
        assertTrue(p.hasErrors());
        assertTrue(p.getErrors().get(0).contains("Integer should be positive"));
    }
    
}
