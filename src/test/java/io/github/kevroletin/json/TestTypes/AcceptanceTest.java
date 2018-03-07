package io.github.kevroletin.json.TestTypes;

import io.github.kevroletin.TelephoneNumber;
import io.github.kevroletin.json.annotations.Sanitizer;
import java.util.Objects;

public class AcceptanceTest {
    int foo;

    String bar;

    @Sanitizer(cls = TelephoneNumber.TelephoneAsStringSanitizerFactory.class)
    String baz;

    private AcceptanceTest() {}

    public AcceptanceTest(int foo, String bar, String baz) {
        this.foo = foo;
        this.bar = bar;
        this.baz = baz;
    }

    @Override
    public String toString() {
        return "AcceptanceTest{" + "foo=" + foo + ", bar=" + bar + ", baz=" + baz + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.foo;
        hash = 97 * hash + Objects.hashCode(this.bar);
        hash = 97 * hash + Objects.hashCode(this.baz);
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
        final AcceptanceTest other = (AcceptanceTest) obj;
        if (this.foo != other.foo) {
            return false;
        }
        if (!Objects.equals(this.bar, other.bar)) {
            return false;
        }
        if (!Objects.equals(this.baz, other.baz)) {
            return false;
        }
        return true;
    }

}