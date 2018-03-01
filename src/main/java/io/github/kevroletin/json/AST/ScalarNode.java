package io.github.kevroletin.json.AST;

import java.util.Objects;
import io.github.kevroletin.json.TypeUtils;

public class ScalarNode implements INode {
    Object child;

    public ScalarNode(Object child) {
        assert(TypeUtils.isSupportedScalar(child));
        this.child = child;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean isNull() {
        return TypeUtils.isNull(child);
    }

    @Override
    public boolean isDouble() {
        return TypeUtils.isDouble(child);
    }

    @Override
    public boolean isInteger() {
        return TypeUtils.isInteger(child);
    }

    @Override
    public boolean isString() {
        return TypeUtils.isString(child);
    }

    @Override
    public boolean isBoolean() {
        return TypeUtils.isBoolean(child);
    }

    @Override
    public Object getUnsafe() {
        return child;
    }

    @Override
    public String toString() {
        return "ScalarNode{" + "child=" + child + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.child);
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
        final ScalarNode other = (ScalarNode) obj;
        if (!Objects.equals(this.child, other.child)) {
            return false;
        }
        return true;
    }

    @Override
    public void toJson(StringBuffer res) {
        if (isString()) {
            res.append(PrintingUtils.escapeString((String)child));
        } else {
            res.append(child.toString());
        }
    }

    @Override
    public void toPrettyJson(int offset, StringBuffer res) {
        toJson(res);
    }
}
