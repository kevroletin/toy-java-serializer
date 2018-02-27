package io.github.kevroletin.json.TestTypes;

import java.util.Objects;

public class IntegerWrapper {
    public Integer value;

    public IntegerWrapper(Integer value) {
        this.value = value;
    }
    
    public IntegerWrapper() {}

    @Override
    public String toString() {
        return "IntegerWrapper{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.value);
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
        final IntegerWrapper other = (IntegerWrapper) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}
