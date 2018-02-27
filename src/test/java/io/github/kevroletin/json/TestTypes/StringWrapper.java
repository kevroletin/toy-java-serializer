package io.github.kevroletin.json.TestTypes;

import java.util.Objects;

public class StringWrapper {
    public String value;

    public StringWrapper(String value) {
        this.value = value;
    }
    
    public StringWrapper() {}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.value);
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
        final StringWrapper other = (StringWrapper) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StringWrapper{" + "value=" + value + '}';
    }
}
