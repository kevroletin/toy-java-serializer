package io.github.kevroletin;

import io.github.kevroletin.json.Result;
import io.github.kevroletin.json.annotations.Sanitizer;
import io.github.kevroletin.json.exceptions.JsonException;
import io.github.kevroletin.json.utils.TypeToken;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class User1 {

    public TelephoneNumber typesafeNumber;

    @Sanitizer(cls = TelephoneNumber.TelephoneAsStringSanitizerFactory.class)
    public String strNumber;

    public Map<TelephoneNumber, Boolean> blacklist;

    public User1() {}

    @Override
    public String toString() {
        return "User1{" + "typesafeNumber=" + typesafeNumber + ", strNumber=" + strNumber + ", blacklist=" + blacklist + '}';
    }

}

public class Main {

    static public void main(String[] args) throws JsonException {
        Json json = new JsonBuilder()
                    .typeAdapter(TelephoneNumber.class, new TelephoneNumber.TelephoneNumberAdapter())
                    .build();

        System.out.println(json.fromJson(
            "{\"typesafeNumber\": \"71234567890\", \"strNumber\": \"81234567890\", \"blacklist\": null}",
            User1.class
        ));

        Type genericType = new TypeToken<Map<TelephoneNumber, Boolean>>(){}.getType();
        System.out.println(json.fromJson(
            "{\"71234567890\": true, \"00000000000\": false}",
            genericType
        ));

        System.out.println(json.fromJson(
              "{ \"typesafeNumber\": \"71234567890\""
            + ", \"strNumber\": \"81234567890\""
            + ", \"blacklist\": {\"71234567890\": true, \"00000000000\": false}"
            + "}",
            User1.class
        ));

        assert(
            json.fromJsonNoThrow(
                "{\"typesafeNumber\": \"bad\", \"strNumber\": \"81234567890\", \"blacklist\": null}",
                User1.class
            ).hasErrors()
        );

        assert(
            json.fromJsonNoThrow(
                "{\"typesafeNumber\": \"71234567890\", \"strNumber\": \"bad\", \"blacklist\": null}",
                User1.class
            ).hasErrors()
        );

        Result res = json.fromJsonNoThrow(
            "[{\"badKey\": true}, {\"anotherBadKey\": false}]",
            new TypeToken<List<Map<TelephoneNumber, Boolean>>>(){}.getType()
        );
        assert(
            res.getErrors().equals(Arrays.asList(
                "[0]{badKey} Invalid telephone number format",
                "[1]{anotherBadKey} Invalid telephone number format"
            ))
        );
    }
}
