package seedu.address.model.session;

import java.time.LocalDate;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a dated session note belonging to a client.
 * Guarantees: details are present and not null, immutable.
 */
public class Session {

    public static final String MESSAGE_DATE_CONSTRAINTS =
            "Session dates must use YYYY-MM-DD and be valid calendar dates.";

    private final LocalDate date;
    private final String note;

    /**
     * Creates a {@code Session} on {@code date} with {@code note}.
     * The note may be empty.
     */
    public Session(LocalDate date, String note) {
        this.date = Objects.requireNonNull(date);
        this.note = Objects.requireNonNull(note);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
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
