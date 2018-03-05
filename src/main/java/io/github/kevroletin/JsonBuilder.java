package io.github.kevroletin;

import io.github.kevroletin.json.Config;
import io.github.kevroletin.json.TypeAdapter;
import java.util.HashMap;
import java.util.Map;

public class JsonBuilder {
    Map<Class, TypeAdapter> adapters = new HashMap();

    public <T> JsonBuilder typeAdapter(Class<T> cls, TypeAdapter<T> adapter) {
        if (cls == null) {
            throw new RuntimeException("cls parameter shouldn't be null");
        }
        if (adapter == null) {
            throw new RuntimeException("adapter parameter shouldn't be null");
        }
        adapters.put(cls, adapter);
        return this;
    }

    public Json build() {
        return new Json(new Config(adapters));
    }
}
