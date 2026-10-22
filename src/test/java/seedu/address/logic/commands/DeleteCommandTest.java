package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonId;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validId_success() {
        DeleteCommand deleteCommand = new DeleteCommand(PersonSelector.byId(BENSON.getId()));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(BENSON));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_idOfPersonHiddenByFilter_success() {
        // the chosen client does not need to be in the displayed list
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteCommand deleteCommand = new DeleteCommand(PersonSelector.byId(GEORGE.getId()));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(GEORGE));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(GEORGE);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameMatchingOnePerson_success() {
        DeleteCommand deleteCommand = new DeleteCommand(PersonSelector.byName("pauline"));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(ALICE));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameMatchingSeveralPersons_listsMatchesWithoutDeleting() {
        PersonSelector selector = PersonSelector.byName("Meier");
        DeleteCommand deleteCommand = new DeleteCommand(selector);

        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL), "delete --id 2");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        PersonSelector selector = PersonSelector.byId(new PersonId(999));
        assertCommandFailure(new DeleteCommand(selector), model,
                String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void execute_nameMatchingNoPerson_throwsCommandException() {
        PersonSelector selector = PersonSelector.byName("Zack");
        assertCommandFailure(new DeleteCommand(selector), model,
                String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_NAME, "Zack"));
    }

    @Test
    public void execute_idOfDeletedPerson_throwsCommandException() {
        model.deletePerson(ALICE);
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        assertCommandFailure(new DeleteCommand(selector), model,
                String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, ALICE.getId()));
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(PersonSelector.byId(ALICE.getId()));
        DeleteCommand deleteSecondCommand = new DeleteCommand(PersonSelector.byId(BENSON.getId()));

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(PersonSelector.byId(ALICE.getId()));
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // same client chosen by name instead of ID -> returns false
        assertFalse(deleteFirstCommand.equals(new DeleteCommand(PersonSelector.byName("Alice"))));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(new PersonId(1));
        DeleteCommand deleteCommand = new DeleteCommand(selector);
        String expected = DeleteCommand.class.getCanonicalName() + "{selector=" + selector + "}";
        assertEquals(expected, deleteCommand.toString());
    }
}
