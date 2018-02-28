package io.github.kevroletin.json.TestTypes;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StringWrapper {
    public String value;

    public static INode astNode(String value) {
        Map<String, INode> m = new HashMap();
        m.put("value", new ScalarNode(value));
        return new ObjectNode(m);
    }

    public StringWrapper(String value) {
        this.value = value;
    }
    
    public StringWrapper() {}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.value);
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
        final StringWrapper other = (StringWrapper) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StringWrapper{" + "value=" + value + '}';
    }
}
