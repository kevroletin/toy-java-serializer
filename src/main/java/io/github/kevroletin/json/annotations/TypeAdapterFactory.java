package io.github.kevroletin.json.annotations;

import io.github.kevroletin.json.TypeAdapter;

@FunctionalInterface
public interface TypeAdapterFactory<T extends TypeAdapter> {
    T create();
}
