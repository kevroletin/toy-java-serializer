package io.github.kevroletin.json.utils;

public class PrintingUtils {
    public static void printOffset(int offset, StringBuffer buf) {
        for (int i = 0; i < offset; ++ i) {
            buf.append("  ");
        }
    }

    public static String escapeString(String str) {
        StringBuilder res = new StringBuilder();
        res.append('"');
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '\b': res.append("\\b"); break;
                case '\f': res.append("\\f"); break;
                case '\n': res.append("\\n"); break;
                case '\r': res.append("\\r"); break;
                case '\t': res.append("\\t"); break;
                case '"': res.append("\\\""); break;
                default: res.append(c);
            }
        }
        res.append('"');
        return res.toString();
    }
}
