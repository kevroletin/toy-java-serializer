package io.github.kevroletin;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.Serializer;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.annotations.TypeAdapterFactory;
import io.github.kevroletin.json.utils.Maybe;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TelephoneNumber {

    public static class TelephoneNumberAdapter implements TypeAdapter<TelephoneNumber> {

        @Override
        public Maybe<TelephoneNumber> deserialize(Deserializer d, List<String> err, Location loc, INode ast, Class<TelephoneNumber> cls) {
            Maybe<String> res = d.deserialize(err, loc, ast, String.class);
            if (!res.isJust() || res.get() == null) {
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

    public static class TelephoneNumberAdapterFactory implements TypeAdapterFactory<TelephoneNumberAdapter> {

        @Override
        public TelephoneNumberAdapter create() {
            return new TelephoneNumberAdapter();
        }

    }

    public static class TelephoneAsStringSanitizer implements TypeAdapter<String> {

        @Override
        public Maybe<String> deserialize(
            Deserializer d, List<String> err, Location loc, INode ast, Class<String> cls) 
        {
            Maybe<String> str = d.withoutTypeAdapter(String.class).deserialize(err, loc, ast, String.class);
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

    public static class TelephoneAsStringAdapterFactory implements TypeAdapterFactory<TelephoneAsStringSanitizer> {

        @Override
        public TelephoneAsStringSanitizer create() {
            return new TelephoneAsStringSanitizer();
        }

    }

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

    public TelephoneNumber() {
        this.value = null;
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
