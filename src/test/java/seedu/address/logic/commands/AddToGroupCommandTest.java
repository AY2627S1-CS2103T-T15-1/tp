package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
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
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code AddToGroupCommand}.
 */
public class AddToGroupCommandTest {

    private static final Tag MORNING = new Tag("morning");
    private static final Tag REHAB = new Tag("rehab");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_clientType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new AddToGroupCommand(PersonSelector.byId(ALICE.getId()),
                Tag.GYM));
    }

    @Test
    public void execute_existingGroup_addsClientAndKeepsOtherTags() {
        model.addGroup(MORNING);
        Person gymAlice = new PersonBuilder(ALICE).withTags("gym").build();
        model.setPerson(ALICE, gymAlice);
        Person morningAlice = new PersonBuilder(ALICE).withTags("gym", "morning").build();
        AddToGroupCommand command = new AddToGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING);

        String expectedMessage = String.format(AddToGroupCommand.MESSAGE_ADDED, "morning",
                Messages.format(morningAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(gymAlice, morningAlice);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_groupNotCreated_throwsCommandException() {
        AddToGroupCommand command = new AddToGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING);
        assertCommandFailure(command, model, String.format(AddToGroupCommand.MESSAGE_GROUP_NOT_FOUND, "morning"));
        assertEquals("The group morning does not exist. Create it first with: group --name morning",
                String.format(AddToGroupCommand.MESSAGE_GROUP_NOT_FOUND, "morning"));
    }

    @Test
    public void execute_clientAlreadyInGroup_noChange() {
        Person morningAlice = new PersonBuilder(ALICE).withTags("morning").build();
        model.setPerson(ALICE, morningAlice);
        AddToGroupCommand command = new AddToGroupCommand(PersonSelector.byName("alice"), MORNING);

        String expectedMessage = String.format(AddToGroupCommand.MESSAGE_ALREADY_IN_GROUP, "morning",
                Messages.format(morningAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameMatchingSeveralClients_listsMatchesWithoutChanges() {
        model.addGroup(MORNING);
        PersonSelector selector = PersonSelector.byName("Meier");
        AddToGroupCommand command = new AddToGroupCommand(selector, MORNING);

        // the example is a complete tag command, including the group
        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL),
                "tag --id 2 --group morning");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        model.addGroup(MORNING);
        AddToGroupCommand command = new AddToGroupCommand(PersonSelector.byId(new PersonId(999)), MORNING);
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        AddToGroupCommand command = new AddToGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING);

        // same values -> returns true
        assertTrue(command.equals(new AddToGroupCommand(PersonSelector.byId(ALICE.getId()), new Tag("morning"))));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new UntagCommand(PersonSelector.byId(ALICE.getId()), MORNING)));

        // different client -> returns false
        assertFalse(command.equals(new AddToGroupCommand(PersonSelector.byId(BENSON.getId()), MORNING)));

        // different group -> returns false
        assertFalse(command.equals(new AddToGroupCommand(PersonSelector.byId(ALICE.getId()), REHAB)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        AddToGroupCommand command = new AddToGroupCommand(selector, MORNING);
        String expected = AddToGroupCommand.class.getCanonicalName() + "{selector=" + selector + ", group="
                + MORNING + "}";
        assertEquals(expected, command.toString());
    }
}
