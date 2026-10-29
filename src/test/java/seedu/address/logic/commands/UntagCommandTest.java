package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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
 * Contains integration tests (interaction with the Model) and unit tests for {@code UntagCommand}.
 */
public class UntagCommandTest {

    private static final Tag MORNING = new Tag("morning");
    private static final Tag REHAB = new Tag("rehab");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_clientInGroup_removesGroupAndKeepsOtherTags() {
        Person taggedAlice = new PersonBuilder(ALICE).withTags("gym", "morning").build();
        model.setPerson(ALICE, taggedAlice);
        Person gymAlice = new PersonBuilder(ALICE).withTags("gym").build();
        UntagCommand command = new UntagCommand(PersonSelector.byName("alice"), MORNING);

        String expectedMessage = String.format(UntagCommand.MESSAGE_REMOVED, "morning", Messages.format(gymAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(taggedAlice, gymAlice);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        // the group itself is kept
        assertTrue(model.hasGroup(MORNING));
    }

    @Test
    public void execute_afterFindingGroup_showsAllClients() {
        Person morningAlice = new PersonBuilder(ALICE).withTags("morning").build();
        model.setPerson(ALICE, morningAlice);
        model.updateFilteredPersonList(person -> person.getTags().contains(MORNING));

        assertDoesNotThrow(() -> new UntagCommand(PersonSelector.byId(ALICE.getId()), MORNING).execute(model));

        assertEquals(getTypicalPersons().size(), model.getFilteredPersonList().size());
    }

    @Test
    public void execute_clientType_removesLabel() {
        Person gymAlice = new PersonBuilder(ALICE).withTags("gym").build();
        model.setPerson(ALICE, gymAlice);
        UntagCommand command = new UntagCommand(PersonSelector.byId(ALICE.getId()), Tag.GYM);

        String expectedMessage = String.format(UntagCommand.MESSAGE_REMOVED, "gym", Messages.format(ALICE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(gymAlice, ALICE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_tagWithDifferentCase_removesTag() {
        Person upperCaseAlice = new PersonBuilder(ALICE).withTags("Morning").build();
        model.setPerson(ALICE, upperCaseAlice);
        UntagCommand command = new UntagCommand(PersonSelector.byId(ALICE.getId()), MORNING);

        String expectedMessage = String.format(UntagCommand.MESSAGE_REMOVED, "morning", Messages.format(ALICE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(upperCaseAlice, ALICE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientWithoutTag_throwsCommandException() {
        UntagCommand command = new UntagCommand(PersonSelector.byId(ALICE.getId()), MORNING);
        assertCommandFailure(command, model, String.format(UntagCommand.MESSAGE_NOT_TAGGED, "morning",
                Messages.format(ALICE)));
    }

    @Test
    public void execute_nameMatchingSeveralClients_listsMatchesWithoutChanges() {
        PersonSelector selector = PersonSelector.byName("Meier");
        UntagCommand command = new UntagCommand(selector, MORNING);

        // the example is a complete tag command, including the tag to remove
        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL),
                "tag --id 2 --untag morning");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        UntagCommand command = new UntagCommand(PersonSelector.byId(new PersonId(999)), MORNING);
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        UntagCommand command = new UntagCommand(PersonSelector.byId(ALICE.getId()), MORNING);

        // same values -> returns true
        assertTrue(command.equals(new UntagCommand(PersonSelector.byId(ALICE.getId()), new Tag("morning"))));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new AddToGroupCommand(PersonSelector.byId(ALICE.getId()), MORNING)));

        // different client -> returns false
        assertFalse(command.equals(new UntagCommand(PersonSelector.byId(BENSON.getId()), MORNING)));

        // different tag -> returns false
        assertFalse(command.equals(new UntagCommand(PersonSelector.byId(ALICE.getId()), REHAB)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        UntagCommand command = new UntagCommand(selector, MORNING);
        String expected = UntagCommand.class.getCanonicalName() + "{selector=" + selector + ", tag="
                + MORNING + "}";
        assertEquals(expected, command.toString());
    }
}
