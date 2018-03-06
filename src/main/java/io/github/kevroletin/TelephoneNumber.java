package io.github.kevroletin;

import io.github.kevroletin.json.AST.INode;
import io.github.kevroletin.json.Deserializer;
import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.TypeAdapter;
import io.github.kevroletin.json.ValueSanitizer;
import io.github.kevroletin.json.utils.Maybe;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import io.github.kevroletin.json.annotations.SanitizerFactory;
import java.util.ArrayList;

public class TelephoneNumber {

    public static class TelephoneNumberAdapter implements TypeAdapter<TelephoneNumber> {

        @Override
        public Maybe<TelephoneNumber> deserialize(Deserializer d, List<String> err, Location loc, INode ast, Type cls) {
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

    public static class TelephoneAsStringSanitizer implements ValueSanitizer<String> {

        @Override
        public Maybe<String> sanitize(List<String> err, Location loc, String str) {
            if (str == null) {
                err.add(loc.toStringWith("Telephone number can't be null"));
                return Maybe.nothing();
            }

            return TelephoneNumber.sanitize(err, loc, str);
        }

    }

    public static class TelephoneAsStringSanitizerFactory implements SanitizerFactory<TelephoneAsStringSanitizer> {

        @Override
        public TelephoneAsStringSanitizer create() {
            return new TelephoneAsStringSanitizer();
        }

    }

    public static Maybe<String> sanitize(List<String> err, Location loc, String value) {
        String digits =
            value.codePoints()
                    .filter(Character::isDigit)
                    .collect(StringBuilder::new,
                             StringBuilder::appendCodePoint,
                             StringBuilder::append)
                    .toString();
        if (digits.length() == 11) {
            return Maybe.just(digits);
        } else {
            err.add(loc.toStringWith("Invalid telephone number format"));
            return Maybe.nothing();
        }
    }

    public final String value;

    public TelephoneNumber(String value) {
        List<String> err = new ArrayList();
        this.value = sanitize(err, Location.empty(), value).orElseThrow(() ->
            new RuntimeException(String.join("; ", err))
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
