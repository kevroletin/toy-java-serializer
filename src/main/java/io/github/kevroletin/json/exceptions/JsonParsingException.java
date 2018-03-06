package io.github.kevroletin.json.exceptions;

public class JsonParsingException extends JsonException {

    public JsonParsingException() {
    }

    public JsonParsingException(String message) {
        super(message);
    }

    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonParsingException(Throwable cause) {
        super(cause);
    }

    public JsonParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
