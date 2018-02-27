package io.github.kevroletin.json.AST;

/** INode - intermediate representation for parsed Json
 * 
 * We represent all scalar types, booleans and null as a single PrimitiveNode
 * for simplicity. This is trade off between type safety of amount of code. 
 * Anyway we plan to use a lot of reflection and AST nodes are not part of user 
 * interface.
 */
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
}
