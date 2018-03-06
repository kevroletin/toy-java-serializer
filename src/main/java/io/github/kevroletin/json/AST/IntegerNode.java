package io.github.kevroletin.json.AST;

import java.util.Objects;

public class IntegerNode implements INode {
    final private String value;

    public IntegerNode(String value) {
        assert(value != null);
        this.value = value;
    }

    public String get() {
        return value;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    @Override
    public Object getUnsafe() {
        return value;
    }

    @Override
    public void toJson(StringBuffer res) {
        res.append(value);
    }

    @Override
    public String toString() {
        return "IntegerNode{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final IntegerNode other = (IntegerNode) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}
