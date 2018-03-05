package io.github.kevroletin.json.annotations;

import io.github.kevroletin.Json;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.Result;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.utils.Maybe;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

class PositiveIntegerAdapter implements TypeAdapter<Integer> {

    @Override
    public Maybe<Integer> deserialize(Deserializer d, List<String> err, Location loc, INode ast, Class<?> cls) {
        Maybe<Integer> res = d.deserialize(err, loc, ast, Integer.class);
        if (res.isNothing()) {
            return Maybe.nothing();
        }
        if (res.get() == null || res.get() <= 0) {
            d.pushError(err, loc, "Integer should be positive");
        }
        return res;
    }

}

class PositiveIntegerValidator implements TypeAdapterFactory {

    @Override
    public TypeAdapter create() {
        return new PositiveIntegerAdapter();
    }

}

class PointInCircleAdapter implements TypeAdapter<Point> {

    int radius;

    public PointInCircleAdapter(int radius) {
        this.radius = radius;
    }

    @Override
    public Maybe<Point> deserialize(Deserializer d, List<String> err, Location loc, INode ast, Class<?> cls) {
        Maybe<Point> point = d.deserializeObject(err, loc, ast, Point.class);
        if (point.isJust()) {
            Integer x = point.get().x;
            Integer y = point.get().y;
            if (x == null || y == null || (x*x + y*y > radius*radius)) {
                d.pushError(err, loc, "Point is not in circle of radious %d", radius);
            }
        }
        return point;
    }

}

class PointInCircle10Validator implements TypeAdapterFactory {

    @Override
    public TypeAdapter create() {
        return new PointInCircleAdapter(10);
    }

}

@Adapter(cls = PointInCircle10Validator.class)
class Point {
    public Integer x;

    @Adapter(cls = PositiveIntegerValidator.class)
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
        assertTrue(p.hasValue());
        assertEquals(new Point(-1, 1), p.get());
    }
    
    @Test
    public void testRejectNegativeNumber() throws JsonParsingException {
        Result<Point> p = new Json().fromJsonNoThrow("{\"x\":1,\"y\":-1}", Point.class);
        assertTrue(p.hasErrors());
        assertTrue(p.getErrors().get(0).contains("Integer should be positive"));
    }

    @Test
    public void testClassAnnotation() {
        Result<Point> p = new Json().fromJsonNoThrow("{\"x\":100,\"y\":100}", Point.class);
        assertTrue(p.hasErrors());
    }
    
}
