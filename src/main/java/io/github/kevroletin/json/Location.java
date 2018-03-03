package io.github.kevroletin.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Implemented as an immutable list
public class Location {
    public final Location prev;

    public final String value;

    public Location(String value, Location next) {
        this.prev = next;
        this.value = value;
    }

    static public Location empty() {
        return new Location(null, null);
    }

    public boolean isNull() {
        return value == null;
    }

    public Location addField(String name) {
        return new Location(String.format("{%s}", name), this);
    }

    public Location addIndex(int idx) {
        return new Location(String.format("[%d]", idx), this);
    }

    public Location addString(String value) {
        return new Location(value, this);
    }

    public String join(String sep) {
        List<String> list = new ArrayList();
        Location p = this;
        while (!p.isNull()) {
            list.add(p.value);
            p = p.prev;
        }
        Collections.reverse(list);
        return list.stream().collect( Collectors.joining(sep) );
    }

    @Override
    public String toString() {
        return join("");
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.prev);
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
        final Location other = (Location) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.prev, other.prev)) {
            return false;
        }
        return true;
    }
}
