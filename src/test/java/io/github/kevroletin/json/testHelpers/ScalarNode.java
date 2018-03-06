package io.github.kevroletin.json.testHelpers;

import io.github.kevroletin.json.AST.BooleanNode;
import io.github.kevroletin.json.AST.DoubleNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.IntegerNode;
import io.github.kevroletin.json.AST.StringNode;
import io.github.kevroletin.json.AST.NullNode;

public class ScalarNode {

    public static INode create(double value) {
        return new DoubleNode(String.valueOf(value));
    }

    public static INode create(int value) {
        return new IntegerNode(String.valueOf(value));
    }

    public static INode create(boolean value) {
        return new BooleanNode(value);
    }

    public static INode create(String value) {
        if (value == null) {
            return NullNode.getInstance();
        }
        return new StringNode(value);
    }
}
