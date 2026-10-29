package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name implements Comparable<Name> {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid name: enter 1–100 characters using letters, spaces, apostrophes, hyphens or periods.";

    public static final String VALIDATION_REGEX = "[\\p{L} .'\u2019-]+";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalize(name);
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        if (test.codePoints().anyMatch(Character::isISOControl)) {
            return false;
        }
        String normalizedName = normalize(test);
        return normalizedName.length() <= 100
                && normalizedName.matches(VALIDATION_REGEX)
                && normalizedName.codePoints().anyMatch(Character::isLetter);
    }

    private static String normalize(String name) {
        return name.trim().replaceAll(" +", " ");
    }

    /**
     * Returns true if this name contains {@code keyword}, ignoring upper and lower case in the same way as
     * {@link #equals(Object)}, so that e.g. {@code İpek} contains {@code ipek}.
     */
    public boolean containsIgnoreCase(String keyword) {
        requireNonNull(keyword);
        return foldCase(fullName).contains(foldCase(keyword));
    }

    private static String foldCase(String name) {
        StringBuilder result = new StringBuilder();
        name.codePoints()
                .map(codePoint -> Character.toLowerCase(Character.toUpperCase(codePoint)))
                .forEach(result::appendCodePoint);
        return result.toString();
    }

    /**
     * Compares names alphabetically, ignoring case, consistent with {@link #equals(Object)}.
     */
    @Override
    public int compareTo(Name other) {
        requireNonNull(other);
        return foldCase(fullName).compareTo(foldCase(other.fullName));
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return foldCase(fullName).equals(foldCase(otherName.fullName));
    }

    @Override
    public int hashCode() {
        return foldCase(fullName).hashCode();
    }

}
