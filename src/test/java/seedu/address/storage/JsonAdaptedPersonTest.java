package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.session.Session;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final int INVALID_ID = 0;
    private static final int VALID_ID = BENSON.getId().value;
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, INVALID_NAME, VALID_PHONE);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, null, VALID_PHONE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, INVALID_PHONE);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidId_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_ID, VALID_NAME, VALID_PHONE);
        assertThrows(IllegalValueException.class, PersonId.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_NAME, VALID_PHONE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "ID");
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void withId_personWithoutId_returnsPersonWithGivenId() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_NAME, VALID_PHONE);
        assertEquals(new PersonBuilder(BENSON).withId(5).build(), person.withId(5).toModelType());
    }

    @Test
    public void toModelType_missingSessions_defaultsToEmptyList() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, null);
        assertEquals(new PersonBuilder(BENSON).build(), person.toModelType());
    }

    @Test
    public void toModelType_duplicateSessionDates_throwsIllegalValueException() {
        List<JsonAdaptedSession> sessions = List.of(
                new JsonAdaptedSession("2026-10-21", "First"),
                new JsonAdaptedSession("2026-10-21", "Second"));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, sessions);

        assertThrows(IllegalValueException.class, seedu.address.model.person.Person.MESSAGE_DUPLICATE_SESSION_DATE,
                person::toModelType);
    }

    @Test
    public void toModelType_nullSession_throwsIllegalValueException() {
        List<JsonAdaptedSession> sessions = new ArrayList<>();
        sessions.add(null);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, sessions);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Session.class.getSimpleName());

        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void withId_personWithSessions_preservesSessions() throws Exception {
        Session session = new Session(java.time.LocalDate.of(2026, 10, 21), "Squats");
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                null, VALID_NAME, VALID_PHONE, List.of(new JsonAdaptedSession(session)));

        assertEquals(new PersonBuilder(BENSON).withId(5).withSessions(session).build(),
                person.withId(5).toModelType());
    }
}
