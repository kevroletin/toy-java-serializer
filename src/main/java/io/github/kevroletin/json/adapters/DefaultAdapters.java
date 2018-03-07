package io.github.kevroletin.json.adapters;

import io.github.kevroletin.json.TypeAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultAdapters {
    private final static Map<Class, TypeAdapter> MAP;

    static {
        MAP = new HashMap();
        MAP.put(Integer.class, new IntegerAdapter(true));
        MAP.put(int.class,     new IntegerAdapter(false));
        MAP.put(String.class,  new StringAdapter(true));
        MAP.put(Double.class,  new DoubleAdapter(true));
        MAP.put(double.class,  new DoubleAdapter(false));
        MAP.put(Boolean.class, new BooleanAdapter(true));
        MAP.put(boolean.class, new BooleanAdapter(false));
        MAP.put(List.class,    new ListAdapter());
        MAP.put(Map.class,     new MapAdapter());
    }

    public static Map<Class, TypeAdapter> getMap() {
        return MAP;
    }
}
