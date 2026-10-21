package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SetActiveCommand}.
 */
public class SetActiveCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_markActiveClientInactive_success() {
        Person inactiveAlice = ALICE.withActive(false);
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byId(ALICE.getId()), false);

        String expectedMessage = String.format(SetActiveCommand.MESSAGE_MARKED_INACTIVE,
                Messages.format(inactiveAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, inactiveAlice);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_markInactiveClientActive_success() {
        Person inactiveAlice = ALICE.withActive(false);
        model.setPerson(ALICE, inactiveAlice);
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byName("pauline"), true);

        String expectedMessage = String.format(SetActiveCommand.MESSAGE_MARKED_ACTIVE, Messages.format(ALICE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(inactiveAlice, ALICE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientChosenByPhone_success() {
        // only ALICE's phone number contains 4351
        Person inactiveAlice = ALICE.withActive(false);
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byPhone("4351"), false);

        String expectedMessage = String.format(SetActiveCommand.MESSAGE_MARKED_INACTIVE,
                Messages.format(inactiveAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, inactiveAlice);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_alreadyInactive_noChange() {
        Person inactiveAlice = ALICE.withActive(false);
        model.setPerson(ALICE, inactiveAlice);
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byId(ALICE.getId()), false);

        String expectedMessage = String.format(SetActiveCommand.MESSAGE_ALREADY_INACTIVE,
                Messages.format(inactiveAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_alreadyActive_noChange() {
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byId(ALICE.getId()), true);

        String expectedMessage = String.format(SetActiveCommand.MESSAGE_ALREADY_ACTIVE, Messages.format(ALICE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_phoneMatchingSeveralClients_listsMatchesWithoutChanges() {
        // ELLE, FIONA and GEORGE have phone numbers containing 9482
        PersonSelector selector = PersonSelector.byPhone("9482");
        SetActiveCommand command = new SetActiveCommand(selector, false);

        String expectedMessage = selector.getMultipleMatchesMessage(List.of(ELLE, FIONA, GEORGE), "inactive --id 5");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byId(new PersonId(999)), true);
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        SetActiveCommand command = new SetActiveCommand(PersonSelector.byId(ALICE.getId()), false);

        // same values -> returns true
        assertTrue(command.equals(new SetActiveCommand(PersonSelector.byId(ALICE.getId()), false)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand(false)));

        // different client -> returns false
        assertFalse(command.equals(new SetActiveCommand(PersonSelector.byId(BENSON.getId()), false)));

        // different status -> returns false
        assertFalse(command.equals(new SetActiveCommand(PersonSelector.byId(ALICE.getId()), true)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        SetActiveCommand command = new SetActiveCommand(selector, false);
        String expected = SetActiveCommand.class.getCanonicalName() + "{selector=" + selector
                + ", isActive=false}";
        assertEquals(expected, command.toString());
    }
}
