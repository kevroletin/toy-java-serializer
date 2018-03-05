package io.github.kevroletin.json;

import io.github.kevroletin.Json;
import io.github.kevroletin.JsonBuilder;
import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.utils.Maybe;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

public class TypeAdapterTest {

    static class TelephoneNumber {
        public final String value;

        public static Maybe<String> sanitize (String value) {
            String digits = 
                value.codePoints()
                     .filter(Character::isDigit)
                     .collect(StringBuilder::new,
                              StringBuilder::appendCodePoint,
                              StringBuilder::append )
                     .toString();
            if (digits.length() == 11) {
                return Maybe.just(digits);
            } else {
                return Maybe.nothing();
            }
        }

        public TelephoneNumber(String value) {
            this.value = sanitize(value).orElseThrow(() -> 
                new RuntimeException("Invalid telephone number format")
            );
        }

        @Override
        public String toString() {
            return "TelephoneNumber{" + "value=" + value + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + Objects.hashCode(this.value);
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
            final TelephoneNumber other = (TelephoneNumber) obj;
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

    }

    static class TelephoneNumberAdapter implements TypeAdapter<TelephoneNumber> {

        @Override
        public Maybe<TelephoneNumber> deserialize(Deserializer d, List<String> err, Location loc, INode ast, Class<?> cls) {
            Result<String> res = d.deserialize(loc, ast, String.class);
            if (res.hasErrors()) {
                err.addAll(res.getErrors());
                return Maybe.nothing();
            }
            
            try {
                return Maybe.just(new TelephoneNumber(res.get()));
            } catch (Exception ex) {
                d.pushError(err, loc, ex.getMessage());
                return Maybe.nothing();
            }
        }
    }

    static class UserWithNumber {
        TelephoneNumber number;

        public UserWithNumber(TelephoneNumber number) {
            this.number = number;
        }

        private UserWithNumber() {}

        @Override
        public String toString() {
            return "UserWithNumber{" + "number=" + number + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.number);
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
            final UserWithNumber other = (UserWithNumber) obj;
            if (!Objects.equals(this.number, other.number)) {
                return false;
            }
            return true;
        }

    }

    static class UserWithStringNumber {
        String number;

        public UserWithStringNumber(String number) {
            this.number = number;
        }

        public UserWithStringNumber() {}

        public String getNumber() {
            return number;
        }

        @Override
        public String toString() {
            return "UserWithStringNumber{" + "number=" + number + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.number);
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
            final UserWithStringNumber other = (UserWithStringNumber) obj;
            if (!Objects.equals(this.number, other.number)) {
                return false;
            }
            return true;
        }
    }

    @Test
    public void testValidator() {
        assertEquals( "70000000000", TelephoneNumber.sanitize("+70000000000").get() );
        assertEquals( "70000000000", TelephoneNumber.sanitize("+7(000)000-00-00").get() );
        assertEquals( "70000000000", TelephoneNumber.sanitize("7(000)000-00-00").get() );
        assertEquals( "00000000000", TelephoneNumber.sanitize("0(000)000-00-00").get() );
        assertTrue( TelephoneNumber.sanitize("7()000-00-00").isNothing() );
        assertTrue( TelephoneNumber.sanitize("7(abc)000-00-00").isNothing() );
        assertTrue( TelephoneNumber.sanitize("").isNothing() );
    }

    class StringToTelephoneSanitizer implements TypeAdapter<String> {

        @Override
        public Maybe<String> deserialize(
            Deserializer d, List<String> err, Location loc, INode ast, Class<?> cls) 
        {
            Maybe<String> str = new Deserializer().deserialize(err, loc, ast, String.class);
            if (str.isNothing()) {
                return Maybe.nothing();
            }

            Maybe<String> res = TelephoneNumber.sanitize(str.get());
            if (res.isNothing()) {
                d.pushError(err, loc, "Invalid telephone number format");
                return Maybe.nothing();
            }
            return res;
        }

    }

    @Test
    public void TestSanitizationOfString() throws JsonParsingException, DeserializationException {
        Json json = new JsonBuilder()
                        .typeAdapter(String.class, new StringToTelephoneSanitizer()).build();
        
        assertEquals(
            new UserWithStringNumber("71234567890"),
            json.fromJson("{\"number\": \"+71234567890\"}", UserWithStringNumber.class)
        );

        assertTrue(
            json.fromJsonNoThrow("{\"number\": \"wrong\"}", UserWithStringNumber.class).hasErrors()
        );
    }

    @Test
    public void TestDeserializationOfCustomType() throws JsonParsingException, DeserializationException {
        Json json = new JsonBuilder()
                        .typeAdapter(TelephoneNumber.class, new TelephoneNumberAdapter()).build();
        
        assertEquals(
            new UserWithNumber(new TelephoneNumber("71234567890")),
            json.fromJson("{\"number\": \"+71234567890\"}", UserWithNumber.class)
        );

        assertTrue(
            json.fromJsonNoThrow("{\"number\": \"wrong\"}", UserWithNumber.class).hasErrors()
        );

        assertEquals(
            Arrays.asList("{number} Expected java.lang.String but got java.lang.Integer"),
            json.fromJsonNoThrow("{\"number\": 123}", UserWithNumber.class).getErrors()
        );
    }
}
