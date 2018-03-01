package io.github.kevroletin.json.TestTypes;

import java.util.Arrays;
import java.util.Objects;

public class AllSupportedTypesWrapper {
    public Integer intValue;

    public Double doubleValue;

    public String stringValue;

    public Boolean booleanValue;

    public AllSupportedTypesWrapper object;

    public Integer[] intArray;

    public Double[] doubleArray;

    public String[] stringArray;

    public Boolean[] booleanArray;

    public AllSupportedTypesWrapper[] objectArray;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.intValue);
        hash = 47 * hash + Objects.hashCode(this.doubleValue);
        hash = 47 * hash + Objects.hashCode(this.stringValue);
        hash = 47 * hash + Objects.hashCode(this.booleanValue);
        hash = 47 * hash + Objects.hashCode(this.object);
        hash = 47 * hash + Arrays.deepHashCode(this.intArray);
        hash = 47 * hash + Arrays.deepHashCode(this.doubleArray);
        hash = 47 * hash + Arrays.deepHashCode(this.stringArray);
        hash = 47 * hash + Arrays.deepHashCode(this.booleanArray);
        hash = 47 * hash + Arrays.deepHashCode(this.objectArray);
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
        final AllSupportedTypesWrapper other = (AllSupportedTypesWrapper) obj;
        if (!Objects.equals(this.stringValue, other.stringValue)) {
            return false;
        }
        if (!Objects.equals(this.intValue, other.intValue)) {
            return false;
        }
        if (!Objects.equals(this.doubleValue, other.doubleValue)) {
            return false;
        }
        if (!Objects.equals(this.booleanValue, other.booleanValue)) {
            return false;
        }
        if (!Objects.equals(this.object, other.object)) {
            return false;
        }
        if (!Arrays.deepEquals(this.intArray, other.intArray)) {
            return false;
        }
        if (!Arrays.deepEquals(this.doubleArray, other.doubleArray)) {
            return false;
        }
        if (!Arrays.deepEquals(this.stringArray, other.stringArray)) {
            return false;
        }
        if (!Arrays.deepEquals(this.booleanArray, other.booleanArray)) {
            return false;
        }
        if (!Arrays.deepEquals(this.objectArray, other.objectArray)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AllSupportedTypesWrapper{" + "intValue=" + intValue + ", doubleValue=" + doubleValue + ", stringValue=" + stringValue + ", booleanValue=" + booleanValue + ", object=" + object + ", intArray=" + intArray + ", doubleArray=" + doubleArray + ", stringArray=" + stringArray + ", booleanArray=" + booleanArray + ", objectArray=" + objectArray + '}';
    }
}
