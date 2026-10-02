package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/** An optional remark attached to a person. */
public class Remark {

    public final String value;

    /** Creates a remark from text, including an empty string for no remark. */
    public Remark(String value) {
        requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Remark remark && value.equals(remark.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
