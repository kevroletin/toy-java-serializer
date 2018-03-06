package io.github.kevroletin.json;

import io.github.kevroletin.json.utils.Maybe;
import java.util.List;

@FunctionalInterface
public interface ValueSanitizer<T> {
    Maybe<T> sanitize(List<String> err, Location loc, T obj);
}
