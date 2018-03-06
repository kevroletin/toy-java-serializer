package io.github.kevroletin.json.TestTypes;

import java.util.Objects;

public class BooleanWrapper {
    public Boolean value;

    public BooleanWrapper(Boolean values) {
        this.value = values;
    }

    public BooleanWrapper() {}

    @Override
    public String toString() {
        return "BooleanWrapper{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.value);
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
        final BooleanWrapper other = (BooleanWrapper) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}
