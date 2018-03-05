package io.github.kevroletin;

import io.github.kevroletin.json.exceptions.JsonException;
import io.github.kevroletin.json.annotations.Adapter;

class User1 {
    @Adapter(cls = TelephoneNumber.TelephoneNumberType.class)
    public TelephoneNumber typesafeNumber;

    @Adapter(cls = TelephoneNumber.TelephoneAsStringType.class)
    public String strNumber;

    public User1() {}

    @Override
    public String toString() {
        return "User1{" + "typesafeNumber=" + typesafeNumber + ", strNumber=" + strNumber + '}';
    }
}

class User2 {
    public TelephoneNumber typesafeNumber;

    public String strNumber;

    public User2() {}

    @Override
    public String toString() {
        return "User1{" + "typesafeNumber=" + typesafeNumber + ", strNumber=" + strNumber + '}';
    }
}

public class Main {
    static public void main(String[] args) throws JsonException {
        System.out.println(new Json().fromJson(
            "{\"typesafeNumber\": \"71234567890\", \"strNumber\": \"81234567890\"}",
            User1.class
        ));

        assert(
            new Json().fromJsonNoThrow(
                "{\"typesafeNumber\": \"bad\", \"strNumber\": \"81234567890\"}",
                User1.class
            ).hasErrors()
        );

        assert(
            new Json().fromJsonNoThrow(
                "{\"typesafeNumber\": \"71234567890\", \"strNumber\": \"bad\"}",
                User1.class
            ).hasErrors()
        );

        Json json = new JsonBuilder()
                    .typeAdapter(String.class, new TelephoneNumber.TelephoneAsStringSanitizer())
                    .typeAdapter(TelephoneNumber.class, new TelephoneNumber.TelephoneNumberAdapter())
                    .build();

        System.out.println(json.fromJson(
            "{\"typesafeNumber\": \"71234567890\", \"strNumber\": \"81234567890\"}",
            User2.class
        ));

        assert(
            json.fromJsonNoThrow(
                "{\"typesafeNumber\": \"bad\", \"strNumber\": \"81234567890\"}",
                User1.class
            ).hasErrors()
        );

        assert(
            json.fromJsonNoThrow(
                "{\"typesafeNumber\": \"71234567890\", \"strNumber\": \"bad\"}",
                User1.class
            ).hasErrors()
        );
    }
}
