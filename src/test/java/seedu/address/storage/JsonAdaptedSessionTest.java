package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedSession.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.LongFormText;
import seedu.address.model.session.Session;

public class JsonAdaptedSessionTest {

    @Test
    public void toModelType_storedNotes_preservesContentsExactly() throws Exception {
        for (String note : new String[]{"", "  Squats\nRest\n\n  ", "Legacy literal \\n and \\t"}) {
            Session expected = new Session(LocalDate.of(2026, 10, 21), new LongFormText(note));
            assertEquals(expected, new JsonAdaptedSession("2026-10-21", note).toModelType());
            assertEquals(expected, new JsonAdaptedSession(expected).toModelType());
        }
    }

    @Test
    public void toModelType_validSession_returnsSession() throws Exception {
        Session session = new Session(LocalDate.of(2026, 10, 21), new LongFormText("Worked on squat form"));
        assertEquals(session, new JsonAdaptedSession(session).toModelType());
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession("2026-02-30", "Note");
        assertThrows(IllegalValueException.class, Session.MESSAGE_DATE_CONSTRAINTS, session::toModelType);
        JsonAdaptedSession extendedYear = new JsonAdaptedSession("+10000-10-21", "Note");
        assertThrows(IllegalValueException.class, Session.MESSAGE_DATE_CONSTRAINTS, extendedYear::toModelType);
        JsonAdaptedSession negativeYear = new JsonAdaptedSession("-0001-10-21", "Note");
        assertThrows(IllegalValueException.class, Session.MESSAGE_DATE_CONSTRAINTS, negativeYear::toModelType);
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(null, "Note");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "date");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_nullNote_defaultsToEmptyNote() throws Exception {
        JsonAdaptedSession session = new JsonAdaptedSession("2026-10-21", null);
        assertEquals(new Session(LocalDate.of(2026, 10, 21), new LongFormText("")), session.toModelType());
    }
}
