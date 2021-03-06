package io.github.kevroletin.json;

import io.github.kevroletin.json.utils.Maybe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Result - container which can hold both value and list of errors at the same time.
 * 
 * Presence of errors doesn't prevent one from getting the value (if the value is present). In that
 * sense it's *not* like Either class which can hold either value either error.
 */
public class Result<T> {
    private final boolean hasValue;

    private final T result;

    private final List<String> errors;

    public Result(boolean hasValue, T result, List<String> errors) {
        this.hasValue = hasValue;
        this.result = result;
        this.errors = errors;
    }

    public Result(Maybe<T> result, List<String> errors) {
        this.hasValue = result.isJust();
        this.result = result.orElse(null);
        this.errors = errors;
    }

    public static <T> Result<T> error(String... errors) {
        List<String> list = new ArrayList();
        Collections.addAll(list, errors);
        return new Result(false, null, list);
    }

    public boolean hasValue() {
        return hasValue;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public T get() {
        if (!hasValue) {
            throw new NoSuchElementException();
        }
        return result;
    }

    public T orElse(T fallback) {
        if (!hasValue) {
            return fallback;
        }
        return result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public <N> Result<N> copyErrors(Class<N> cls) {
        return new Result(false, null, errors);
    }

    @Override
    public String toString() {
        return "Result{" + "hasValue=" + hasValue + ", result=" + result + ", errors=" + errors + '}';
    }

    public Maybe<T> toMaybe() {
        if (hasValue) {
            return Maybe.just(result);
        }
        return Maybe.nothing();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.hasValue ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.result);
        hash = 43 * hash + Objects.hashCode(this.errors);
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
        final Result<?> other = (Result<?>) obj;
        if (this.hasValue != other.hasValue) {
            return false;
        }
        if (!Objects.equals(this.result, other.result)) {
            return false;
        }
        if (!Objects.equals(this.errors, other.errors)) {
            return false;
        }
        return true;
    }
}
