package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.session.Session;
import seedu.address.testutil.PersonBuilder;

public class EditSessionCommandTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);
    private static final Session ORIGINAL_SESSION = new Session(DATE, "Original note");
    private static final String UPDATED_NOTE = "Improved squat depth";

    private Model model;
    private Person aliceWithSession;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = getTypicalAddressBook();
        aliceWithSession = new PersonBuilder(ALICE).withSessions(ORIGINAL_SESSION).build();
        addressBook.setPerson(ALICE, aliceWithSession);
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_validId_replacesNote() {
        EditSessionCommand command = new EditSessionCommand(
                PersonSelector.byId(aliceWithSession.getId()), DATE, UPDATED_NOTE);
        Person updatedAlice = new PersonBuilder(aliceWithSession)
                .withSessions(new Session(DATE, UPDATED_NOTE)).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(aliceWithSession, updatedAlice);

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_SUCCESS, aliceWithSession.getName(), DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyNote_clearsNote() {
        EditSessionCommand command = new EditSessionCommand(
                PersonSelector.byId(aliceWithSession.getId()), DATE, "");
        Person updatedAlice = new PersonBuilder(aliceWithSession)
                .withSessions(new Session(DATE, "")).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(aliceWithSession, updatedAlice);

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_SUCCESS, aliceWithSession.getName(), DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleSessions_preservesOtherSessions() {
        Session earlier = new Session(DATE.minusDays(1), "Earlier note");
        Session later = new Session(DATE.plusDays(1), "Later note");
        Person original = aliceWithSession.withSessions(List.of(earlier, ORIGINAL_SESSION, later));
        model.setPerson(aliceWithSession, original);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original,
                original.withSessions(List.of(earlier, new Session(DATE, UPDATED_NOTE), later)));

        EditSessionCommand command = new EditSessionCommand(
                PersonSelector.byId(original.getId()), DATE, UPDATED_NOTE);
        assertCommandSuccess(command, model,
                String.format(EditSessionCommand.MESSAGE_SUCCESS, original.getName(), DATE), expectedModel);
    }

    @Test
    public void execute_nameMatchingOnePerson_replacesNote() {
        EditSessionCommand command = new EditSessionCommand(
                PersonSelector.byName("ALICE p"), DATE, UPDATED_NOTE);
        Person updatedAlice = new PersonBuilder(aliceWithSession)
                .withSessions(new Session(DATE, UPDATED_NOTE)).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(aliceWithSession, updatedAlice);

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_SUCCESS, aliceWithSession.getName(), DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sessionDateNotFound_failure() {
        LocalDate missingDate = DATE.plusDays(1);
        EditSessionCommand command = new EditSessionCommand(
                PersonSelector.byId(aliceWithSession.getId()), missingDate, UPDATED_NOTE);

        String expectedMessage = String.format(
                EditSessionCommand.MESSAGE_SESSION_NOT_FOUND, aliceWithSession.getName(), missingDate);

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_nameMatchingSeveralPersons_listsMatchesWithoutEditing() {
        PersonSelector selector = PersonSelector.byName("Meier");
        EditSessionCommand command = new EditSessionCommand(selector, DATE, UPDATED_NOTE);
        String expectedMessage = selector.getMultipleMatchesMessage(
                List.of(BENSON, DANIEL), "session --id " + BENSON.getId()
                        + " --edit --date " + DATE + " --note \"" + UPDATED_NOTE + "\"");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_failure() {
        EditSessionCommand command = new EditSessionCommand(
                PersonSelector.byId(new PersonId(999)), DATE, UPDATED_NOTE);

        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        EditSessionCommand standardCommand = new EditSessionCommand(
                PersonSelector.byId(ALICE.getId()), DATE, UPDATED_NOTE);

        assertTrue(standardCommand.equals(standardCommand));
        assertTrue(standardCommand.equals(
                new EditSessionCommand(PersonSelector.byId(ALICE.getId()), DATE, UPDATED_NOTE)));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(5));
        assertFalse(standardCommand.equals(
                new EditSessionCommand(PersonSelector.byId(BENSON.getId()), DATE, UPDATED_NOTE)));
        assertFalse(standardCommand.equals(
                new EditSessionCommand(PersonSelector.byId(ALICE.getId()), DATE.plusDays(1), UPDATED_NOTE)));
        assertFalse(standardCommand.equals(
                new EditSessionCommand(PersonSelector.byId(ALICE.getId()), DATE, "Different note")));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        EditSessionCommand command = new EditSessionCommand(selector, DATE, UPDATED_NOTE);
        String expected = EditSessionCommand.class.getCanonicalName() + "{selector=" + selector
                + ", date=" + DATE + ", note=" + UPDATED_NOTE + "}";

        assertEquals(expected, command.toString());
    }
}
