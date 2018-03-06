package io.github.kevroletin.json.TestTypes;

import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.NullNode;
import io.github.kevroletin.json.testHelpers.ScalarNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
    
public class IntCons {
    public static INode astFromList(List<Integer> fromList) {
        List<Integer> list = new ArrayList(fromList);
        Collections.reverse(list);

        INode prev = NullNode.getInstance();
        for (Integer i: list) {
            Map<String, INode> m = new HashMap();
            m.put("next", prev);
            m.put("value", ScalarNode.create(i));
            prev = new ObjectNode(m);
        }

        return prev;
    }

    public IntCons next;

    public Integer value;

    public IntCons(Integer value, IntCons next) {
        this.next = next;
        this.value = value;
    }

    public IntCons() {}

    @Override
    public String toString() {
        return "Cons{" + "next=" + next + ", value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.next);
        hash = 59 * hash + Objects.hashCode(this.value);
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
        final IntCons other = (IntCons) obj;
        if (!Objects.equals(this.next, other.next)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}