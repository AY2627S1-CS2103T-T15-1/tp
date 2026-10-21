package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.session.Session;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    private static final Session SESSION = new Session(LocalDate.of(2026, 10, 21), "Squats");

    @Test
    public void constructor_sessionsChangedAfterConstruction_preservesOriginalSessions() {
        List<Session> sessions = new ArrayList<>(List.of(SESSION));
        Person person = new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone(), sessions);

        sessions.clear();

        assertEquals(List.of(SESSION), person.getSessions());
        assertThrows(UnsupportedOperationException.class, () -> person.getSessions().add(SESSION));
    }

    @Test
    public void constructor_duplicateSessionDates_throwsIllegalArgumentException() {
        Session sameDate = new Session(SESSION.getDate(), "Different note");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone(), List.of(SESSION, sameDate)));

        assertEquals(Person.MESSAGE_DUPLICATE_SESSION_DATE, exception.getMessage());
    }

    @Test
    public void withSessions_returnsPersonWithSessions() {
        assertEquals(new PersonBuilder(ALICE).withSessions(SESSION).build(), ALICE.withSessions(List.of(SESSION)));
        assertTrue(ALICE.getSessions().isEmpty());
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // same name and phone, different ID -> returns true
        assertTrue(ALICE.isSamePerson(new PersonBuilder(ALICE).withId(99).build()));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, different phone -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // phone has formatting separators, all other attributes same -> returns true
        editedBob = new PersonBuilder(BOB).withPhone("2222 2222").build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true after trimming
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different ID -> returns false
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withId(99).build()));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different sessions -> returns false
        editedAlice = new PersonBuilder(ALICE).withSessions(SESSION).build();
        assertFalse(ALICE.equals(editedAlice));

    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{id=" + ALICE.getId() + ", name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone() + ", sessions=[]}";
        assertEquals(expected, ALICE.toString());
    }
}
