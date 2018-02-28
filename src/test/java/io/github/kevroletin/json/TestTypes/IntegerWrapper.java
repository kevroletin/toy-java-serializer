package io.github.kevroletin.json.TestTypes;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IntegerWrapper {
    public Integer value;

    public static INode astNode(Integer value) {
        Map<String, INode> m = new HashMap();
        m.put("value", new ScalarNode(value));
        return new ObjectNode(m);
    }

    public IntegerWrapper(Integer value) {
        this.value = value;
    }
    
    public IntegerWrapper() {}

    @Override
    public String toString() {
        return "IntegerWrapper{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.value);
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
        final IntegerWrapper other = (IntegerWrapper) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}
