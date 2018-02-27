package io.github.kevroletin.json.AST;

import com.sun.istack.internal.Nullable;
import java.util.Objects;
import io.github.kevroletin.json.TypeUtils;

public class ScalarNode implements INode {
    Object child;

    public ScalarNode(@Nullable Object child) {
        assert(TypeUtils.isPrimitive(child));
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
    public Object unsafeGet() {
        return child;
    }

    @Override
    public String toString() {
        return "PrimitiveNode{" + "child=" + child + '}';
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
}
