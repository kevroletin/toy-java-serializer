package io.github.kevroletin.json.AST;

public class NullNode implements INode {

    static final NullNode instance = new NullNode();

    public static NullNode getInstance() {
        return instance;
    }

    private NullNode() {}

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public Object getUnsafe() {
        return null;
    }

    @Override
    public void toJson(StringBuffer res) {
        res.append("null");
    }

    @Override
    public String toString() {
        return "NullNode{" + '}';
    }

}
