package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a client's unique ID in the address book.
 * IDs are assigned in increasing order as clients are added, and are never reused.
 * Guarantees: immutable; is valid as declared in {@link #isValidId(int)}
 */
public class PersonId implements Comparable<PersonId> {

    public static final String MESSAGE_CONSTRAINTS = "Invalid ID: enter a positive whole number, e.g. 3.";

    /**
     * The largest ID a client can have. It is one less than the largest {@code int},
     * so that the ID after any client's ID can still be stored.
     */
    public static final int MAX_CLIENT_ID = Integer.MAX_VALUE - 1;
    public static final String MESSAGE_CLIENT_ID_CONSTRAINTS =
            "Invalid client ID: enter a whole number from 1 to " + MAX_CLIENT_ID + ".";

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
     * Returns true if {@code test} can be the ID of a client, i.e. it is from 1 to {@link #MAX_CLIENT_ID}.
     */
    public static boolean isValidClientId(int test) {
        return isValidId(test) && test <= MAX_CLIENT_ID;
    }

    /**
     * Returns true if this ID can be given to a client.
     * The only ID that cannot is the one after {@link #MAX_CLIENT_ID}, which means that all client IDs are used.
     */
    public boolean isClientId() {
        return isValidClientId(value);
    }

    /**
     * Returns the ID that comes right after this one.
     * This ID must be a client ID, as declared in {@link #isClientId()}.
     */
    public PersonId next() {
        assert isClientId() : "Only a client ID has an ID after it";
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
