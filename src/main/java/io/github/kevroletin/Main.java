package io.github.kevroletin;

import io.github.kevroletin.json.AST.*;
import java.util.HashMap;

public class Main {
    static public void main(String[] args) {
        
        HashMap<String, INode> m = new HashMap();
        m.put("string", new ScalarNode("hello"));
        m.put("int", new ScalarNode(1));
        m.put("double", new ScalarNode(1.0));
        
        ObjectNode res = new ObjectNode(m);
        
        System.out.println(res);
    }
}
