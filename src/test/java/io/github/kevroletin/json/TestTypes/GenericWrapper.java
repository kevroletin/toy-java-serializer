package io.github.kevroletin.json.TestTypes;

import java.util.Objects;

public class GenericWrapper<T> {
    private T value;

    public GenericWrapper(T value) {
        this.value = value;
    }
    
    public GenericWrapper() {}

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
        final GenericWrapper<?> other = (GenericWrapper<?>) obj;
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
