package io.github.kevroletin.json.TestTypes;

import java.util.Objects;
    
public class Cons<T> {
    public Cons next;

    public T value;

    public Cons(T value, Cons<T> next) {
        this.next = next;
        this.value = value;
    }

    public Cons() {}

    @Override
    public String toString() {
        return "Cons{" + "next=" + next + ", value=" + value + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.next);
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
        final Cons<?> other = (Cons<?>) obj;
        if (!Objects.equals(this.next, other.next)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}