package io.github.kevroletin.json.AST;

import io.github.kevroletin.json.utils.PrintingUtils;
import java.util.Objects;

public class StringNode implements INode {
    final private String value;

    public StringNode(String value) {
        assert(value != null);
        this.value = value;
    }

    public String get() {
        return value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public Object getUnsafe() {
        return value;
    }

    @Override
    public void toJson(StringBuffer res) {
        res.append(PrintingUtils.escapeString(value));
    }

    @Override
    public String toString() {
        return "StringNode{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.value);
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
        final StringNode other = (StringNode) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}
