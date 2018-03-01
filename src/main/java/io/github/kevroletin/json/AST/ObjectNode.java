package io.github.kevroletin.json.AST;

import io.github.kevroletin.json.utils.PrintingUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjectNode implements INode {
    Map<String, INode> childs;

    public ObjectNode(Map<String, INode> childs) {
        assert(childs != null);
        this.childs = childs;
    }

    public Map<String, INode> get() {
        return childs;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public Object getUnsafe() {
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

    @Override
    public void toJson(StringBuffer res) {
        List<String> keys = new ArrayList();
        keys.addAll(childs.keySet());
        Collections.sort(keys);

        res.append("{");
        for (int i = 0; i < childs.size(); ++i) {
            if (i != 0) {
                res.append(",");
            }
            String k = keys.get(i);
            res.append(PrintingUtils.escapeString(k));
            res.append(":");
            childs.get(k).toJson(res);
        }
        res.append("}");
    }

    @Override
    public void toPrettyJson(int offset, StringBuffer res) {
        if (childs.isEmpty()) {
            res.append("{}");
            return;
        }

        List<String> keys = new ArrayList();
        keys.addAll(childs.keySet());
        Collections.sort(keys);

        res.append("{\n");
        for (int i = 0; i < childs.size(); ++i) {
            if (i != 0) {
                res.append(",\n");
            }
            String k = keys.get(i);
            PrintingUtils.printOffset(offset + 1, res);
            res.append(PrintingUtils.escapeString(k));
            res.append(": ");
            childs.get(k).toPrettyJson(offset + 1, res);
        }
        res.append("\n");
        PrintingUtils.printOffset(offset, res);
        res.append("}");
    }
}
