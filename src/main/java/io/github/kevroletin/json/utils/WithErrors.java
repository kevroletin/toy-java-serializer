package io.github.kevroletin.json.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class WithErrors<T> {
    private final boolean hasValue;

    private final T value;

    private List<String> errors;

    private WithErrors(boolean hasValue, T value, List<String> errors) {
        this.hasValue = hasValue;
        this.value = value;
        this.errors = errors;
    }

    static public <T> WithErrors<T> of(T value) {
        return new WithErrors(true, value, new ArrayList());
    }

    static public <T> WithErrors<T> errors(List errors) {
        return new WithErrors(false, null, errors);
    }

    static public <T> WithErrors<T> errors(String... errors) {
        List<String> list = new ArrayList();
        Collections.addAll(list, errors);
        return new WithErrors(false, null, list);
    }

    public <M> void addAllErrors(WithErrors<M> other) {
        errors.addAll(other.errors);
    }

    public <M> void addAllErrors(List<String> newErrors) {
        this.errors.addAll(newErrors);
    }

    public <M> void addAllErrors(String... newErrors) {
        Collections.addAll(this.errors, newErrors);
    }

    public <M> void addError(String msg) {
        errors.add(msg);
    }

    public boolean hasValue() {
        return hasValue;
    }

    public T getValue() {
        if (!hasValue()) {
            throw new NoSuchElementException();
        }
        return value;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public <N> WithErrors<N> copyErrors(Class<N> cls) {
        return new WithErrors<N>(false, null, errors);
    }
}
