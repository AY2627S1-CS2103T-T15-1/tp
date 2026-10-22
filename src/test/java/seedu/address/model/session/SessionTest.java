package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class SessionTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Session(null, "Note"));
    }

    @Test
    public void constructor_nullNote_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Session(DATE, null));
    }

    @Test
    public void equals() {
        Session session = new Session(DATE, "Worked on squat form");

        assertTrue(session.equals(new Session(DATE, "Worked on squat form")));
        assertEquals(session.hashCode(), new Session(DATE, "Worked on squat form").hashCode());
        assertTrue(session.equals(session));
        assertFalse(session.equals(null));
        assertFalse(session.equals(5));
        assertFalse(session.equals(new Session(DATE.plusDays(1), "Worked on squat form")));
        assertFalse(session.equals(new Session(DATE, "Different note")));
    }

    @Test
    public void toStringMethod() {
        Session session = new Session(DATE, "Worked on squat form");
        String expected = Session.class.getCanonicalName()
                + "{date=2026-10-21, note=Worked on squat form}";
        assertEquals(expected, session.toString());
    }
}
