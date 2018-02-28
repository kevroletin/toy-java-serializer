package io.github.kevroletin.json.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArrayNode implements INode {
    List<INode> childs;

    public ArrayNode(List<INode> childs) {
        assert(childs != null);
        this.childs = childs;
    }

    public List<INode> get(){
        return childs;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public Object getUnsafe() {
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
