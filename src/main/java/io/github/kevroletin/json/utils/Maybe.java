package io.github.kevroletin.json.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

// Similar to Optional but can contain null value
public class Maybe<T> {
    final T value;

    final boolean hasValue;

    private Maybe(T value, boolean hasValue) {
        this.value = value;
        this.hasValue = hasValue;
    }

    public static <T> Maybe<T> just(T value) {
        return new Maybe(value, true);
    }

    public static <T> Maybe<T> nothing() {
        return new Maybe(null, false);
    }

    public boolean isJust() {
        return hasValue;
    }

    public boolean isNothing() {
        return !hasValue;
    }

    public <N> Maybe<N> map(Function<? super T, ? extends N> funct) {
        if (hasValue) {
            return new Maybe(funct.apply(value), true);
        }
        return Maybe.nothing();
    }

    public T orElse(T fallback) {
        if (hasValue) {
            return value;
        }
        return fallback;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> ex) throws X {
        if (!hasValue) {
            throw ex.get();
        }
        return value;
    }

    public T get() {
        if (!hasValue) {
            throw new NoSuchElementException();
        }
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.value);
        hash = 41 * hash + (this.hasValue ? 1 : 0);
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
        final Maybe<?> other = (Maybe<?>) obj;
        if (this.hasValue != other.hasValue) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Maybe{" + "value=" + value + ", hasValue=" + hasValue + '}';
    }
}
