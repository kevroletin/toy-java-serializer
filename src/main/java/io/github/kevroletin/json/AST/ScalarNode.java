package io.github.kevroletin.json.AST;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.Objects;

public class ScalarNode implements INode {
    Object child;

    public ScalarNode(@Nullable Object child) {
        assert(child != null);
        this.child = child;
        
        assert(isNull() || isDouble() || isInteger()
               || isString() || isBoolean());
    }
    
    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean isNull() {
        return child == null;
    }

    @Override
    public boolean isDouble() {
        return child instanceof Double;
    }

    @Override
    public boolean isInteger() {
        return child instanceof Integer;
    }

    @Override
    public boolean isString() {
        return child instanceof String;
    }    
    
    @Override
    public boolean isBoolean() {
        return child instanceof Boolean;
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
