package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a general note about a client, such as their preferences or injuries.
 * An empty note means the client has no note.
 * Guarantees: immutable; is valid as declared in {@link #isValidNote(String)}
 */
public class Note {

    public static final int MAX_LENGTH = 500;
    public static final String MESSAGE_CONSTRAINTS = "Invalid note: enter at most " + MAX_LENGTH
            + " characters on a single line.";
    public static final Note EMPTY = new Note("");

    public final String value;

    /**
     * Constructs a {@code Note}.
     *
     * @param note A valid note. Leading and trailing spaces are removed.
     */
    public Note(String note) {
        requireNonNull(note);
        checkArgument(isValidNote(note), MESSAGE_CONSTRAINTS);
        value = note.strip();
    }

    /**
     * Returns true if {@code test} is a valid note, i.e. it has at most {@code MAX_LENGTH} characters
     * after removing leading and trailing spaces, and no line breaks.
     */
    public static boolean isValidNote(String test) {
        requireNonNull(test);
        return test.strip().length() <= MAX_LENGTH && test.lines().count() <= 1;
    }

    /**
     * Returns true if this note has no text.
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Note otherNote)) {
            return false;
        }

        return value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
