package seedu.address.model;

/**
 * Immutable long-form text shared by optional descriptions and notes.
 * Preserves text exactly, except that null is normalized to an empty string.
 */
public final class LongFormText {

    public final String value;

    /**
     * Creates text from an already parsed or stored value, without interpreting escape sequences.
     */
    public LongFormText(String text) {
        value = text == null ? "" : text;
    }

    /**
     * Returns true if the text is empty or contains only whitespace.
     */
    public boolean isBlank() {
        return value.isBlank();
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
        return other instanceof LongFormText otherText && value.equals(otherText.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
