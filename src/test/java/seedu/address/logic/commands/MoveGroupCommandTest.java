package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

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
 * Contains integration tests (interaction with the Model) and unit tests for {@code MoveGroupCommand}.
 */
public class MoveGroupCommandTest {

    private static final Tag MORNING = new Tag("morning");
    private static final Tag EVENING = new Tag("evening");
    private static final Tag REHAB = new Tag("rehab");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_sameGroups_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new MoveGroupCommand(PersonSelector.byId(ALICE.getId()),
                MORNING, MORNING));
    }

    @Test
    public void constructor_clientType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new MoveGroupCommand(PersonSelector.byId(ALICE.getId()),
                Tag.GYM, MORNING));
    }

    @Test
    public void execute_clientInGroup_movedAndOtherTagsKept() {
        Person morningAlice = new PersonBuilder(ALICE).withTags("gym", "morning").build();
        model.setPerson(ALICE, morningAlice);
        model.addGroup(EVENING);
        Person eveningAlice = new PersonBuilder(ALICE).withTags("gym", "evening").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(morningAlice, eveningAlice);

        assertCommandSuccess(new MoveGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING, EVENING), model,
                String.format(MoveGroupCommand.MESSAGE_MOVED, "morning", "evening", Messages.format(eveningAlice)),
                expectedModel);
        // the old group is kept
        assertTrue(model.hasGroup(MORNING));
    }

    @Test
    public void execute_afterFindingGroup_showsAllClients() {
        Person morningAlice = new PersonBuilder(ALICE).withTags("morning").build();
        model.setPerson(ALICE, morningAlice);
        model.addGroup(EVENING);
        model.updateFilteredPersonList(person -> person.getTags().contains(MORNING));

        assertDoesNotThrow(() -> new MoveGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING, EVENING)
                .execute(model));

        assertEquals(getTypicalPersons().size(), model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clientAlreadyInTargetGroup_onlyLeavesOldGroup() {
        Person taggedAlice = new PersonBuilder(ALICE).withTags("morning", "evening").build();
        model.setPerson(ALICE, taggedAlice);
        Person eveningAlice = new PersonBuilder(ALICE).withTags("evening").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(taggedAlice, eveningAlice);

        assertCommandSuccess(new MoveGroupCommand(PersonSelector.byName("alice"), MORNING, EVENING), model,
                String.format(MoveGroupCommand.MESSAGE_MOVED, "morning", "evening", Messages.format(eveningAlice)),
                expectedModel);
    }

    @Test
    public void execute_clientNotInFromGroup_throwsCommandException() {
        model.addGroup(EVENING);
        assertCommandFailure(new MoveGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING, EVENING), model,
                String.format(MoveGroupCommand.MESSAGE_NOT_IN_GROUP, "morning", Messages.format(ALICE)));
    }

    @Test
    public void execute_toGroupNotCreated_throwsCommandException() {
        model.setPerson(ALICE, new PersonBuilder(ALICE).withTags("morning").build());
        assertCommandFailure(new MoveGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING, EVENING), model,
                String.format(AddToGroupCommand.MESSAGE_GROUP_NOT_FOUND, "evening"));
    }

    @Test
    public void execute_nameMatchingSeveralClients_listsMatchesWithoutChanges() {
        PersonSelector selector = PersonSelector.byName("Meier");
        MoveGroupCommand command = new MoveGroupCommand(selector, MORNING, EVENING);

        // the example is a complete group --move command
        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL),
                "group --move --id 2 --from morning --to evening");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        assertCommandFailure(new MoveGroupCommand(PersonSelector.byId(new PersonId(999)), MORNING, EVENING), model,
                String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        MoveGroupCommand command = new MoveGroupCommand(selector, MORNING, EVENING);

        // same values -> returns true
        assertTrue(command.equals(new MoveGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING, EVENING)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new AddToGroupCommand(selector, EVENING)));

        // different client -> returns false
        assertFalse(command.equals(new MoveGroupCommand(PersonSelector.byId(BENSON.getId()), MORNING, EVENING)));

        // different from group -> returns false
        assertFalse(command.equals(new MoveGroupCommand(selector, REHAB, EVENING)));

        // different to group -> returns false
        assertFalse(command.equals(new MoveGroupCommand(selector, MORNING, REHAB)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        MoveGroupCommand command = new MoveGroupCommand(selector, MORNING, EVENING);
        String expected = MoveGroupCommand.class.getCanonicalName() + "{selector=" + selector + ", from="
                + MORNING + ", to=" + EVENING + "}";
        assertEquals(expected, command.toString());
    }
}
