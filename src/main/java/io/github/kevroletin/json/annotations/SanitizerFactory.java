package io.github.kevroletin.json.annotations;

import io.github.kevroletin.json.ValueSanitizer;

@FunctionalInterface
public interface SanitizerFactory<T extends ValueSanitizer> {
    T create();
}
