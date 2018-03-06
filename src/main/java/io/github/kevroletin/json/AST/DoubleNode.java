package io.github.kevroletin.json.AST;

import java.util.Objects;

public class DoubleNode implements INode {
    final private String value;

    public DoubleNode(String value) {
        assert(value != null);
        this.value = value;
    }

    public String get() {
        return value;
    }

    @Override
    public boolean isDouble() {
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
        return "DoubleNode{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.value);
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
        final DoubleNode other = (DoubleNode) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}
