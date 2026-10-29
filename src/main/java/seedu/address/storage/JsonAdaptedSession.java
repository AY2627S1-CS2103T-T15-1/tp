package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.LongFormText;
import seedu.address.model.session.Session;

/**
 * Jackson-friendly version of {@link Session}.
 */
class JsonAdaptedSession {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Session's %s field is missing!";

    private final String date;
    private final String note;

    @JsonCreator
    public JsonAdaptedSession(@JsonProperty("date") String date, @JsonProperty("note") String note) {
        this.date = date;
        this.note = note;
    }

    public JsonAdaptedSession(Session source) {
        date = source.getDate().toString();
        note = source.getNote().value;
    }

    /**
     * Converts this Jackson-friendly session into the model's {@code Session} object.
     */
    public Session toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }

        final LocalDate modelDate;
        try {
            modelDate = Session.parseDate(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(Session.MESSAGE_DATE_CONSTRAINTS);
        }

        return new Session(modelDate, new LongFormText(note == null ? "" : note));
    }
}
