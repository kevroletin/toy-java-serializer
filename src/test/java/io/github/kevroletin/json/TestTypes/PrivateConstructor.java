package io.github.kevroletin.json.TestTypes;

import java.util.Objects;

public class PrivateConstructor {
    public Integer value;

    @Override
    public String toString() {
        return "PrivateConstructor{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final PrivateConstructor other = (PrivateConstructor) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    private PrivateConstructor() {};
}
