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

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code NoteCommand}.
 */
public class NoteCommandTest {

    private static final Note KNEE_NOTE = new Note("Knee injury, avoid lunges");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_newNote_success() {
        Person editedPerson = new PersonBuilder(ALICE).withNote(KNEE_NOTE.value).build();
        NoteCommand noteCommand = new NoteCommand(PersonSelector.byId(ALICE.getId()), KNEE_NOTE);

        String expectedMessage = String.format(NoteCommand.MESSAGE_NOTE_UPDATED, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        // the client's page is shown so that the new note can be seen
        assertCommandSuccess(noteCommand, model, new CommandResult(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void execute_replaceExistingNote_success() {
        Person personWithNote = new PersonBuilder(ALICE).withNote("Old note").build();
        model.setPerson(ALICE, personWithNote);
        Person editedPerson = new PersonBuilder(ALICE).withNote(KNEE_NOTE.value).build();
        NoteCommand noteCommand = new NoteCommand(PersonSelector.byName("alice"), KNEE_NOTE);

        String expectedMessage = String.format(NoteCommand.MESSAGE_NOTE_UPDATED, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithNote, editedPerson);

        assertCommandSuccess(noteCommand, model, new CommandResult(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void execute_emptyNote_removesNote() {
        Person personWithNote = new PersonBuilder(ALICE).withNote("Old note").build();
        model.setPerson(ALICE, personWithNote);
        NoteCommand noteCommand = new NoteCommand(PersonSelector.byId(ALICE.getId()), Note.EMPTY);

        String expectedMessage = String.format(NoteCommand.MESSAGE_NOTE_REMOVED, Messages.format(ALICE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithNote, ALICE);

        assertCommandSuccess(noteCommand, model, new CommandResult(expectedMessage, ALICE), expectedModel);
    }

    @Test
    public void execute_nameMatchingSeveralClients_listsMatchesWithoutChangingNotes() {
        PersonSelector selector = PersonSelector.byName("Meier");
        NoteCommand noteCommand = new NoteCommand(selector, KNEE_NOTE);

        // the example is a complete note command, including the note
        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL),
                "note --id 2 --note \"" + KNEE_NOTE.value + "\"");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        NoteCommand noteCommand = new NoteCommand(PersonSelector.byId(new PersonId(999)), KNEE_NOTE);
        assertCommandFailure(noteCommand, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void execute_noteOfOneClient_keepsOtherDetails() throws Exception {
        Person taggedInactivePerson = new PersonBuilder(ALICE).withActive(false).withTags("gym").build();
        model.setPerson(ALICE, taggedInactivePerson);

        new NoteCommand(PersonSelector.byId(ALICE.getId()), KNEE_NOTE).execute(model);

        Person editedPerson = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getId().equals(ALICE.getId())).findFirst().get();
        assertEquals(taggedInactivePerson.withNote(KNEE_NOTE), editedPerson);
    }

    @Test
    public void equals() {
        NoteCommand noteCommand = new NoteCommand(PersonSelector.byId(ALICE.getId()), KNEE_NOTE);

        // same values -> returns true
        assertTrue(noteCommand.equals(new NoteCommand(PersonSelector.byId(ALICE.getId()), KNEE_NOTE)));

        // same object -> returns true
        assertTrue(noteCommand.equals(noteCommand));

        // null -> returns false
        assertFalse(noteCommand.equals(null));

        // different types -> returns false
        assertFalse(noteCommand.equals(new ClearCommand()));

        // different client -> returns false
        assertFalse(noteCommand.equals(new NoteCommand(PersonSelector.byId(BENSON.getId()), KNEE_NOTE)));

        // different note -> returns false
        assertFalse(noteCommand.equals(new NoteCommand(PersonSelector.byId(ALICE.getId()), Note.EMPTY)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        NoteCommand noteCommand = new NoteCommand(selector, KNEE_NOTE);
        String expected = NoteCommand.class.getCanonicalName() + "{selector=" + selector + ", note=" + KNEE_NOTE
                + "}";
        assertEquals(expected, noteCommand.toString());
    }
}
