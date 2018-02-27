package io.github.kevroletin;

import io.github.kevroletin.json.AST.*;
import io.github.kevroletin.json.Serializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class InnerTestClass {
    Integer z;
}

class TestClass {
    Integer x;
    Integer y;

    InnerTestClass t;
    
    List list;
}

class Wrapper<T> {
    private T value;

    public Wrapper(T value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.value);
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
        final Wrapper<?> other = (Wrapper<?>) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Wrapper{" + "value=" + value + '}';
    }
}

public class Main {
    static public void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        
        HashSet<Object> c = new HashSet();
        c.add(new Wrapper("hello"));
        
        System.out.println( c.contains( new Wrapper("hello") ) );
    }
}
