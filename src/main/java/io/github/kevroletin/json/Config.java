package io.github.kevroletin.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config {
    public final Map<Class, TypeAdapter> typeAdapters;

    public Config(Map<Class, TypeAdapter> typeAdapters) {
        this.typeAdapters = typeAdapters;
    }

    public Config() {
        typeAdapters = new HashMap();
    }

    public Config copy() {
        return new Config(new HashMap(typeAdapters));
    }

    public Config withoutTypeAdapter(Class<?> cls) {
        Map<Class, TypeAdapter> newAdapters = new HashMap(typeAdapters);
        newAdapters.remove(cls);
        return new Config(newAdapters);
    }

    public Config withTypeAdapter(Class<?> cls, TypeAdapter<?> adapter) {
        Map<Class, TypeAdapter> newAdapters = new HashMap(typeAdapters);
        newAdapters.put(cls, adapter);
        return new Config(newAdapters);
    }

    @Override
    public String toString() {
        return "Config{" + "typeAdapters=" + typeAdapters + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.typeAdapters);
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
        final Config other = (Config) obj;
        if (!Objects.equals(this.typeAdapters, other.typeAdapters)) {
            return false;
        }
        return true;
    }
}
