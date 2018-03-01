package io.github.kevroletin.json.annotations;

import io.github.kevroletin.Json;
import io.github.kevroletin.json.exceptions.DeserializationException;
import io.github.kevroletin.json.exceptions.JsonParsingException;
import io.github.kevroletin.json.exceptions.ValidationException;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

class NonEmptyStringValidator implements ValidationFunction {
    @Override
    public Boolean validate(Object data) throws ValidationException {
        if (!(data instanceof StringWrapper)) {
            return false;
        }
        StringWrapper x = (StringWrapper)data;
        return x.value != null && x.value.length() > 0;
    }
}

@TypeValidator(
    cls = NonEmptyStringValidator.class
)
class StringWrapper {
    public String value;

    public StringWrapper(String value) {
        this.value = value;
    }

    public StringWrapper() {}

    @Override
    public String toString() {
        return "StringWrapper{" + "value=" + value + '}';
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
        final StringWrapper other = (StringWrapper) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}

public class TypeValidatorTest {

    @Test
    public void testTypeValidation() throws JsonParsingException, DeserializationException {
        assertEquals(
            new StringWrapper("hello"),
            Json.fromJson("{\"value\": \"hello\"}", StringWrapper.class)
        );
    }

    @Test(expected = ValidationException.class)
    public void testTypeValidationError() throws JsonParsingException, DeserializationException {
        Json.fromJson("{\"value\": \"\"}", StringWrapper.class);
    }

}
