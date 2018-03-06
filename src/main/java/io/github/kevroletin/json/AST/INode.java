package io.github.kevroletin.json.AST;

public interface INode {
    default boolean isArray() { return false; }

    default boolean isObject() { return false; }

    default boolean isPrimitive() { return false; }

    default boolean isBoolean() { return false; }

    default boolean isInteger() { return false; }

    default boolean isDouble() { return false; }

    default boolean isString() { return false; }

    default boolean isNull() { return false; }

    Object getUnsafe();

    default String toJson() {
        StringBuffer buff = new StringBuffer();
        toJson(buff);
        return buff.toString();
    }

    void toJson(StringBuffer res);

    default String toPrettyJson() {
        StringBuffer buff = new StringBuffer();
        toPrettyJson(0, buff);
        return buff.toString();
    }

    default void toPrettyJson(int offset, StringBuffer res) {
        toJson(res);
    }
}
