package io.github.kevroletin.json.AST;

import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import java.util.Objects;

public class ArrayNode implements INode {
    ArrayList<INode> childs;

    public ArrayNode(@NotNull ArrayList<INode> childs) {
        assert(childs != null);
        this.childs = childs;
    }
    
    @Override
    public boolean isArray() { 
        return true; 
    }
    
    @Override
    public Object unsafeGet() {
        return childs;
    }

    @Override
    public String toString() {
        return "ArrayNode{" + "childs=" + childs + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.childs);
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
        final ArrayNode other = (ArrayNode) obj;
        if (!Objects.equals(this.childs, other.childs)) {
            return false;
        }
        return true;
    }
}
