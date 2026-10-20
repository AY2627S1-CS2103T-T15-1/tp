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
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
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
 * {@code ViewCommand}.
 */
public class ViewCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validId_success() {
        ViewCommand viewCommand = new ViewCommand(PersonSelector.byId(CARL.getId()));

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_SUCCESS, Messages.format(CARL));

        // viewing a person does not change the model
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage, CARL), expectedModel);
    }

    @Test
    public void execute_idOfPersonHiddenByFilter_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        ViewCommand viewCommand = new ViewCommand(PersonSelector.byId(CARL.getId()));

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_SUCCESS, Messages.format(CARL));

        // viewing a person keeps the filtered list as it is
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage, CARL), expectedModel);
    }

    @Test
    public void execute_nameMatchingOnePerson_success() {
        // matching ignores case and only needs part of the name
        ViewCommand viewCommand = new ViewCommand(PersonSelector.byName("ALICE p"));

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_SUCCESS, Messages.format(ALICE));
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage, ALICE), expectedModel);
    }

    @Test
    public void execute_nameMatchingSeveralPersons_listsMatches() {
        PersonSelector selector = PersonSelector.byName("meier");
        ViewCommand viewCommand = new ViewCommand(selector);

        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL),
                ViewCommand.COMMAND_WORD);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        // no client is viewed, so the result does not carry a person
        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage), expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        ViewCommand viewCommand = new ViewCommand(PersonSelector.byId(new PersonId(999)));
        assertCommandFailure(viewCommand, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void execute_nameMatchingNoPerson_throwsCommandException() {
        ViewCommand viewCommand = new ViewCommand(PersonSelector.byName("Zack"));
        assertCommandFailure(viewCommand, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_NAME, "Zack"));
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(PersonSelector.byId(ALICE.getId()));
        ViewCommand viewSecondCommand = new ViewCommand(PersonSelector.byId(BENSON.getId()));

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        ViewCommand viewFirstCommandCopy = new ViewCommand(PersonSelector.byId(ALICE.getId()));
        assertTrue(viewFirstCommand.equals(viewFirstCommandCopy));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byName("Alice");
        ViewCommand viewCommand = new ViewCommand(selector);
        String expected = ViewCommand.class.getCanonicalName() + "{selector=" + selector + "}";
        assertEquals(expected, viewCommand.toString());
    }
}
