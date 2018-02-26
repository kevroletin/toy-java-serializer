package io.github.kevroletin.json.AST;

import com.sun.istack.internal.NotNull;
import java.util.Map;
import java.util.Objects;

public class ObjectNode implements INode {
    Map<String, INode> childs;

    public ObjectNode(@NotNull Map<String, INode> childs) {
        assert(childs != null);
        this.childs = childs;
    }
    
    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public Object unsafeGet() {
        return childs;
    }

    @Override
    public String toString() {
        return "ObjectNode{" + "childs=" + childs + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.childs);
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
        final ObjectNode other = (ObjectNode) obj;
        if (!Objects.equals(this.childs, other.childs)) {
            return false;
        }
        return true;
    }
}
