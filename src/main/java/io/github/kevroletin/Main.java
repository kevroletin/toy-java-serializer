package io.github.kevroletin;

import io.github.kevroletin.json.AST.*;
import io.github.kevroletin.json.Serializer;
import io.github.kevroletin.json.TypeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class InnerTestClass {
    Integer z;
}

class TestClass {
    Integer x;
    Integer y;

    InnerTestClass t;
    
    List list;
}

public class Main {
    static public void main(String[] args) throws IllegalArgumentException, IllegalAccessException {

        HashMap<String, INode> m = new HashMap<>();
        m.put("string", new ScalarNode("hello"));
        m.put("int", new ScalarNode(1));
        m.put("double", new ScalarNode(1.0));

        ObjectNode res = new ObjectNode(m);

        TestClass x = new TestClass();
        x.x = 10;
        x.y = 20;
        x.t = new InnerTestClass();
        x.t.z = 30;

        INode ans = Serializer.serializeInner(x);

        int[] arr = {1, 2, 3};
        ArrayList<Integer> arrList = new ArrayList<>();
        for (int t: arr) {
            arrList.add(t);
        }
	
		x.list = arrList;

        System.out.println( Serializer.serializeInner(x) );
    }
}
