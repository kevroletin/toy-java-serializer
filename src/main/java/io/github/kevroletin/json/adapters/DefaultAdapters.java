package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.TypeAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultAdapters {
    private final static Map<Class, TypeAdapter> map;

    static {
        map = new HashMap();
        map.put(Integer.class, new IntegerAdapter(true));
        map.put(int.class,     new IntegerAdapter(false));
        map.put(String.class,  new StringAdapter(true));
        map.put(Double.class,  new DoubleAdapter(true));
        map.put(double.class,  new DoubleAdapter(false));
        map.put(Boolean.class, new BooleanAdapter(true));
        map.put(boolean.class, new BooleanAdapter(false));
        map.put(List.class,    new ListAdapter());
        map.put(Map.class,     new MapAdapter());
    }

    public static Map<Class, TypeAdapter> getMap() {
        return map;
    }
}
