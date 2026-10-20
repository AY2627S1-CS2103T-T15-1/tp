package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a client's unique ID in the address book.
 * IDs are assigned in increasing order as clients are added, and are never reused.
 * Guarantees: immutable; is valid as declared in {@link #isValidId(int)}
 */
public class PersonId implements Comparable<PersonId> {

    public static final String MESSAGE_CONSTRAINTS = "Invalid ID: enter a positive whole number, e.g. 3.";

    public final int value;

    /**
     * Constructs a {@code PersonId}.
     *
     * @param id A valid ID.
     */
    public PersonId(int id) {
        checkArgument(isValidId(id), MESSAGE_CONSTRAINTS);
        value = id;
    }

    /**
     * Returns true if {@code test} is a valid ID.
     */
    public static boolean isValidId(int test) {
        return test > 0;
    }

    /**
     * Returns the ID that comes right after this one.
     */
    public PersonId next() {
        assert value < Integer.MAX_VALUE : "Person IDs should never run out";
        return new PersonId(value + 1);
    }

    @Override
    public int compareTo(PersonId other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonId otherId)) {
            return false;
        }

        return value == otherId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
