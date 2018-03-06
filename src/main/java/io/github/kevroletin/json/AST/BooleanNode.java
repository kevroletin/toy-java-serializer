package io.github.kevroletin.json.AST;

public class BooleanNode implements INode {
    final private boolean value;   

    public BooleanNode(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    @Override
    public boolean isBoolean() {
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
        return "BooleanNode{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.value ? 1 : 0);
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
        final BooleanNode other = (BooleanNode) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

}
