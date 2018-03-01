package io.github.kevroletin.json;

import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.AST.ArrayNode;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.AST.ObjectNode;
import io.github.kevroletin.json.AST.ScalarNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonParser {

    static class StringInputSeq {
        public class StateGuard {
            final int oldState;

            StateGuard() {
                oldState = StringInputSeq.this.getState();
            };

            public void restore() {
                StringInputSeq.this.restoreState(oldState);
            }
        }

        final String data;
        int pos;

        public StringInputSeq(String data) {
            this.data = data;
            this.pos = 0;
        }

        public boolean isEmpty() {
            return pos >= data.length();
        }
        
        public char pop() throws JsonParsingException {
            char res = get();
            ++pos;
            return res;
        }

        public char get() throws JsonParsingException {
            if (isEmpty()) {
                throw new JsonParsingException("Unexcepted end of input");
            }
            return data.charAt(pos);
        }

        public void unpop(char c) {
            assert(pos > 0 && data.charAt(pos) == c);
            --pos;
        }

        @Override
        public String toString() {
            return "StringInputSource{" + "data=" + data + ", pos=" + pos + '}';
        }

        private int getState() {
            return pos;
        }

        private void restoreState(int pos) {
            assert(pos >= 0);
            this.pos = pos;
        }

        public StateGuard savePoint() {
            return new StateGuard();
        }
    }

    @FunctionalInterface
    public interface Parser<T> {
        T apply() throws JsonParsingException;
    }

    StringInputSeq in;

    public JsonParser(String str) {
        this.in = new StringInputSeq(str);
    }

    private void skipSpaces() throws JsonParsingException {
        while (!in.isEmpty() && Character.isSpace(in.get())) {
            in.pop();
        }
    }

    private void expect(char expect) throws JsonParsingException {
        char got = in.pop();
        if (got != expect) {
            throw new JsonParsingException(
                String.format("Expected %c but got %c", expect, got));
        }
    }

    private void expect(String expect) throws JsonParsingException {
        for (char c: expect.toCharArray()) {
            expect(c);
        }
    }

    private char convertIfSpecialChar(char x) {
        if (x == 'b') return '\b';
        if (x == 'f') return '\f';
        if (x == 'n') return '\n';
        if (x == 'r') return '\r';
        if (x == 't') return '\t';
        return x;
    }

    private String eatString() throws JsonParsingException {
        expect('"');
        
        StringBuilder res = new StringBuilder();
        boolean prevSlash = false;

        while (true) {
            char p = in.pop();
            if (p == '"' && !prevSlash) {
                break;
            }
            boolean hideChar = (p == '\\' && !prevSlash);
            if (!hideChar) {
                if (prevSlash) {
                    res.append(convertIfSpecialChar(p));
                } else {
                    res.append(p);
                }
            }
            if (p == '\\') {
                prevSlash = !prevSlash; // double slash doesn't escape next character
            } else {
                prevSlash = false;
            }
        }

        return res.toString(); 
    }
    
    /** 
     * - no support for escaped unicode characters
     * - error message could be improved
     */
    private ScalarNode parseString() throws JsonParsingException {
        return new ScalarNode(eatString());
    }    

    private String eatInt() throws JsonParsingException {
        boolean negative = false;
        if (in.get() == '-') {
            negative = true;
            in.pop();
        }

        String res = eatDigits();
        if (res.length() == 0) {
            throw new JsonParsingException("Expectin digits or - sign");
        }
        if (res.length() > 1 && res.charAt(0) == '0') {
            throw new JsonParsingException(
                String.format("Leading zeros in multi digit number %s", res));
        }

        if (negative) {
            return "-" + res;
        } else {
            return res;
        }
    }

    private ScalarNode parseInt() throws JsonParsingException {
        return new ScalarNode( Integer.parseInt(eatInt()) );
    }

    private <T> Optional<T> tryParse(Parser<T> f) {
        StringInputSeq.StateGuard sp = in.savePoint();
        try {
            return Optional.of(f.apply());
        } catch (JsonParsingException e) {
            sp.restore();
        }
        return Optional.empty();
    }

    private String eatDigits() throws JsonParsingException { 
        StringBuilder res = new StringBuilder();
        while (!in.isEmpty() && Character.isDigit(in.get())) {
            res.append(in.pop());
        }
        return res.toString();
    }
    
    private ScalarNode parseNumber() throws JsonParsingException {
        String intPart = eatInt();
        Optional<String> fracPart = tryParse(this::eatFrac);
        Optional<String> expPart = tryParse(this::eatExp);

        if (!fracPart.isPresent() && !expPart.isPresent()) {
            return new ScalarNode(Integer.parseInt(intPart));
        } else {
            String str = intPart + fracPart.orElse("") + expPart.orElse("");
            return new ScalarNode(Double.parseDouble(str));
        }
    }

    private String eatFrac() throws JsonParsingException {
        expect('.');
        String right = eatDigits();
        return '.' + right;
    }

    private String eatExp() throws JsonParsingException {
        char e = in.pop();
        if (e != 'e' && e != 'E') {
            throw new JsonParsingException(
                String.format("Expected e or E but got %c", e));
        }
        String sign;
        if (in.get() == '-' || in.get() == '+') {
            sign = String.valueOf(in.pop());
        } else {
            sign = "";
        }

        String digits = eatDigits();
        return String.valueOf(e) + sign + digits;
    }

    private ScalarNode parseBoolean() throws JsonParsingException {
        Optional<Boolean> res = tryParse(() -> { expect("true"); return true; });
        if (!res.isPresent()) {
            res = tryParse(() -> { expect("false"); return false; });
        }
        return new ScalarNode(
            res.orElseThrow(() -> new JsonParsingException("Failed to parse atom"))
        );
    }

    private ScalarNode parseScalar() throws JsonParsingException {
        Optional<ScalarNode> res = tryParse(this::parseString);
        if (!res.isPresent()) {
            res = tryParse(this::parseNumber); 
        }
        if (!res.isPresent()) {
            res = tryParse(this::parseBoolean); 
        }
        if (!res.isPresent()) {
            res = tryParse(() -> { expect("null"); return new ScalarNode(null); });
        }
        return res.orElseThrow(() -> new JsonParsingException("Failed to parse scalar"));
    }

    private ArrayNode parseArray() throws JsonParsingException {
        expect('[');
        skipSpaces();
        List<INode> elems = new ArrayList();

        boolean ok = (in.get() != ']');
        while (ok) {
            INode elem = parseInternal();
            elems.add(elem);

            skipSpaces();
            if (in.get() == ','){
                in.pop();
            } else {
                ok = false;
            }
            skipSpaces();
        }
        expect(']');
        return new ArrayNode(elems);
    }

    private ObjectNode parseObject() throws JsonParsingException {
        expect('{');
        skipSpaces();
        Map<String, INode> fields = new HashMap();
        
        boolean ok = (in.get() != '}');
        while (ok) {
            String key = eatString();
            skipSpaces();
            expect(':');
            skipSpaces();
            INode val = parseInternal();
            fields.put(key, val);

            skipSpaces();
            if (in.get() == ','){
                in.pop();
                skipSpaces();
            } else {
                ok = false;
            }
        }
        expect('}');
        return new ObjectNode(fields);
    }

    private INode parseInternal() throws JsonParsingException {
        skipSpaces();
        Optional<INode> res = tryParse(this::parseObject); 
        if (!res.isPresent()) {
            res = tryParse(this::parseArray); 
        }
        if (!res.isPresent()) {
            res = tryParse(this::parseScalar); 
        }
        return res.orElseThrow(() -> new JsonParsingException("Failed to parse json"));
    }

    static public INode parse(String string) throws JsonParsingException {
        JsonParser parser = new JsonParser(string);
        return parser.parseInternal();
    }
}
