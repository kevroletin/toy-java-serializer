package io.github.kevroletin;

import io.github.kevroletin.json.annotations.FieldValidator;
import io.github.kevroletin.json.annotations.TypeValidator;
import io.github.kevroletin.json.annotations.ValidationFunction;
import io.github.kevroletin.json.exceptions.ValidationException;
import java.util.Objects;
import io.github.kevroletin.json.exceptions.JsonException;

class TelephoneNumberStringValidator implements ValidationFunction {
    public static boolean validateString(String value) {
        if (value.length() == 11) {
            if (!(value.charAt(0) != '7' || value.charAt(0) != '8')) {
                return false;
            } 
            for (int i = 1; i < value.length(); ++i) {
                if (!Character.isDigit(value.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        if (value.length() == 12) {
            if (value.charAt(0) != '+') {
                return false;
            }
            if (!(value.charAt(1) != '7' || value.charAt(1) != '8')) {
                return false;
            } 
            for (int i = 2; i < value.length(); ++i) {
                if (!Character.isDigit(value.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public Boolean validate(Object data) throws ValidationException {
        if (!(data instanceof String)) { return false; }
        return validateString((String) data);
    }
}


class TelephoneNumberValidator implements ValidationFunction {
    @Override
    public Boolean validate(Object data) throws ValidationException {
        if (!(data instanceof TelephoneNumber)) { return false; }
        String value = ((TelephoneNumber)data).value;
        return TelephoneNumberStringValidator.validateString(value);
    }
}

@TypeValidator(cls = TelephoneNumberValidator.class)
class TelephoneNumber {
    String value;

    public TelephoneNumber(String value) {
        this.value = value;
    }

    public TelephoneNumber() {}
    
    @Override
    public String toString() {
        return "TelephoneNumber{" + "value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.value);
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

class User1 {
    public TelephoneNumber number;

    public User1(TelephoneNumber number) {
        this.number = number;
    }

    User1() {}

    @Override
    public String toString() {
        return "User1{" + "number=" + number + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final User1 other = (User1) obj;
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        return true;
    }
}

class User2 {
    @FieldValidator(cls = TelephoneNumberStringValidator.class)
    public String number;

    public User2(String number) {
        this.number = number;
    }

    User2() {}

    @Override
    public String toString() {
        return "User2{" + "number=" + number + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.number);
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
        final User2 other = (User2) obj;
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        return true;
    }
}

public class Main {
    static void goodCases() throws JsonException {
        User1 u1 = new User1(new TelephoneNumber("+70123456789"));
        String str1 = "{\n" +
                      "  \"number\": {\n" +
                      "    \"value\": \"+70123456789\"\n" +
                      "  }\n" +
                      "}";
        assert(Json.toPrettyJson(u1).equals(str1));
        assert(Json.toPrettyJson(Json.fromJson(str1, User1.class)).equals(str1));

        User2 u2 = new User2("+70123456789");
        String str2 = "{\n" +
                      "  \"number\": \"+70123456789\"\n" +
                      "}";
        assert(Json.toPrettyJson(u2).equals(str2));
        assert(Json.toPrettyJson(Json.fromJson(str2, User2.class)).equals(str2));
    }

    static void badCase1() throws JsonException {
        boolean ok = false;
        try {
            User1 u1 = new User1(new TelephoneNumber("23456789"));
            String str1 =
                "{\n" +
                "  \"number\": {\n" +
                "    \"value\": \"23456789\"\n" +
                "  }\n" +
                "}";
            Json.fromJson(str1, User1.class);
        } 
        catch (ValidationException e) {
            ok = e.getMessage().contains("Validator io.github.kevroletin.TelephoneNumberValidator rejected value");
        }
        assert(ok);
    }

    static void badCase2() throws JsonException {
        boolean ok = false;
        try {
            User2 u2 = new User2("+70123456789");
            String str2 =
                "{\n" +
                "  \"number\": \"23456789\"\n" +
                "}";
            Json.fromJson(str2, User2.class);
        } 
        catch (ValidationException e) {
            ok = e.getMessage().contains("Validator io.github.kevroletin.TelephoneNumberStringValidator rejected value"
);
        }
        assert(ok);
    }

    static public void testValidator() throws ValidationException {
        assert( new TelephoneNumberValidator().validate(new TelephoneNumber("+70123456789")) );
        assert( new TelephoneNumberValidator().validate(new TelephoneNumber("+80123456789")) );
        assert( new TelephoneNumberValidator().validate(new TelephoneNumber("70123456789")) );
        assert( new TelephoneNumberValidator().validate(new TelephoneNumber("80123456789")) );
        assert( !(new TelephoneNumberValidator().validate(new TelephoneNumber("0123456789"))) );
        assert( !(new TelephoneNumberValidator().validate(new TelephoneNumber(""))) );
        assert( !(new TelephoneNumberValidator().validate(new TelephoneNumber("abc"))) );
    }

    static public void main(String[] args) throws JsonException {
        testValidator();
        goodCases();
        badCase1();
        badCase2();
    }
}
