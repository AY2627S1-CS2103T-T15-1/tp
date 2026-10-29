package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Set;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    /** The longest tag name allowed, checked before matching so long input cannot overflow the regex. */
    public static final int MAX_LENGTH = 50;
    public static final String MESSAGE_CONSTRAINTS = "Tag names should be words made of letters and digits, "
            + "separated by single spaces or hyphens, and at most " + MAX_LENGTH + " characters long";
    public static final String VALIDATION_REGEX = "\\p{Alnum}+([ -]\\p{Alnum}+)*";

    /** Tag for clients the trainer trains at a gym. */
    public static final Tag GYM = new Tag("gym");
    /** Tag for clients the trainer trains personally. */
    public static final Tag PERSONAL = new Tag("personal");
    /** The client types. A client has at most one of these tags. */
    public static final Set<Tag> CLIENT_TYPES = Set.of(GYM, PERSONAL);

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = tagName;
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return tagName.equals(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
