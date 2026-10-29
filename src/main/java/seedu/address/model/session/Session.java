package seedu.address.model.session;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.LongFormText;

/**
 * Represents a dated session note belonging to a client.
 * Guarantees: details are present and not null, immutable.
 */
public class Session {

    public static final String MESSAGE_DATE_CONSTRAINTS =
            "Session dates must use YYYY-MM-DD and be valid calendar dates.";

    private final LocalDate date;
    private final LongFormText note;

    /**
     * Creates a {@code Session} on {@code date} with {@code note}.
     * The note may be empty.
     */
    public Session(LocalDate date, LongFormText note) {
        this.date = Objects.requireNonNull(date);
        this.note = Objects.requireNonNull(note);
    }

    public LocalDate getDate() {
        return date;
    }

    public LongFormText getNote() {
        return note;
    }

    /**
     * Parses a valid calendar date written as exactly YYYY-MM-DD.
     *
     * @throws DateTimeParseException if the format or calendar date is invalid.
     */
    public static LocalDate parseDate(String value) {
        Objects.requireNonNull(value);
        if (!value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new DateTimeParseException(MESSAGE_DATE_CONSTRAINTS, value, 0);
        }
        return LocalDate.parse(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Session otherSession)) {
            return false;
        }

        return date.equals(otherSession.date)
                && note.equals(otherSession.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date)
                .add("note", note)
                .toString();
    }
}
