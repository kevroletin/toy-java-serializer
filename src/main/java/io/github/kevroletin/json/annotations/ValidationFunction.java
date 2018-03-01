package io.github.kevroletin.json.annotations;

import io.github.kevroletin.json.exceptions.ValidationException;

@FunctionalInterface
public interface ValidationFunction {
    Boolean validate(Object data) throws ValidationException;
}
