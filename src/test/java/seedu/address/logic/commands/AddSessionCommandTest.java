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

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.SessionCommandParser;
import seedu.address.model.AddressBook;
import seedu.address.model.LongFormText;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.session.Session;
import seedu.address.testutil.PersonBuilder;

public class AddSessionCommandTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);
    private static final String NOTE = "Worked on squat form";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_multilineNote_retryCommandPreservesNote() throws Exception {
        LongFormText note = new LongFormText("  Squats\nRest\n\nRemember --edit  ");
        AddSessionCommand command = new AddSessionCommand(PersonSelector.byName("Meier"), DATE, note);
        String retry = "session --id " + BENSON.getId() + command.getArgumentsAfterSelector();

        assertTrue(command.execute(model).getFeedbackToUser().contains(retry));
        var parsed = new SessionCommandParser().parse(retry.substring("session".length()));
        assertEquals(new AddSessionCommand(PersonSelector.byId(BENSON.getId()), DATE, note), parsed);
        parsed.execute(model);
        assertEquals(note, model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getId().equals(BENSON.getId())).findFirst().orElseThrow()
                .getSessions().get(0).getNote());
    }

    @Test
    public void execute_validId_success() {
        AddSessionCommand command = new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE,
                new LongFormText(NOTE));
        Person updatedAlice = new PersonBuilder(ALICE).withSessions(new Session(DATE, new LongFormText(NOTE))).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, updatedAlice);

        String expectedMessage = String.format(AddSessionCommand.MESSAGE_SUCCESS, ALICE.getName(), DATE);

        assertCommandSuccess(command, model, new CommandResult(expectedMessage, updatedAlice), expectedModel);
    }

    @Test
    public void execute_emptyNote_success() {
        AddSessionCommand command = new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE,
                new LongFormText(""));
        Person updatedAlice = new PersonBuilder(ALICE).withSessions(new Session(DATE, new LongFormText(""))).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, updatedAlice);

        String expectedMessage = String.format(AddSessionCommand.MESSAGE_SUCCESS, ALICE.getName(), DATE);

        assertCommandSuccess(command, model, new CommandResult(expectedMessage, updatedAlice), expectedModel);
    }

    @Test
    public void execute_nameMatchingOnePerson_success() {
        AddSessionCommand command = new AddSessionCommand(PersonSelector.byName("ALICE p"), DATE,
                new LongFormText(NOTE));
        Person updatedAlice = new PersonBuilder(ALICE).withSessions(new Session(DATE, new LongFormText(NOTE))).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, updatedAlice);

        String expectedMessage = String.format(AddSessionCommand.MESSAGE_SUCCESS, ALICE.getName(), DATE);

        assertCommandSuccess(command, model, new CommandResult(expectedMessage, updatedAlice), expectedModel);
    }

    @Test
    public void execute_duplicateDate_failure() {
        Person aliceWithSession = new PersonBuilder(ALICE).withSessions(new Session(DATE,
                new LongFormText("Existing note"))).build();
        AddressBook addressBook = new AddressBook(model.getAddressBook());
        addressBook.setPerson(ALICE, aliceWithSession);
        Model modelWithSession = new ModelManager(addressBook, new UserPrefs());
        AddSessionCommand command = new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE,
                new LongFormText(NOTE));

        String expectedMessage = String.format(
                AddSessionCommand.MESSAGE_DUPLICATE_SESSION, ALICE.getName(), DATE);

        assertCommandFailure(command, modelWithSession, expectedMessage);
    }

    @Test
    public void execute_nameMatchingSeveralPersons_listsMatchesWithoutAdding() {
        PersonSelector selector = PersonSelector.byName("Meier");
        AddSessionCommand command = new AddSessionCommand(selector, DATE, new LongFormText(NOTE));
        String expectedMessage = selector.getMultipleMatchesMessage(
                List.of(BENSON, DANIEL), "session --id " + BENSON.getId()
                        + " --add --date " + DATE + " --note \"" + NOTE + "\"");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_failure() {
        AddSessionCommand command = new AddSessionCommand(
                PersonSelector.byId(new PersonId(999)), DATE, new LongFormText(NOTE));

        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        AddSessionCommand standardCommand = new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE,
                new LongFormText(NOTE));

        assertTrue(standardCommand.equals(standardCommand));
        assertTrue(standardCommand.equals(
                new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE, new LongFormText(NOTE))));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(5));
        assertFalse(standardCommand.equals(
                new AddSessionCommand(PersonSelector.byId(BENSON.getId()), DATE, new LongFormText(NOTE))));
        assertFalse(standardCommand.equals(
                new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE.plusDays(1), new LongFormText(NOTE))));
        assertFalse(standardCommand.equals(
                new AddSessionCommand(PersonSelector.byId(ALICE.getId()), DATE, new LongFormText("Different note"))));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        AddSessionCommand command = new AddSessionCommand(selector, DATE, new LongFormText(NOTE));
        String expected = AddSessionCommand.class.getCanonicalName() + "{selector=" + selector
                + ", date=" + DATE + ", note=" + NOTE + "}";

        assertEquals(expected, command.toString());
    }
}
