package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteGroupCommand}.
 */
public class DeleteGroupCommandTest {

    private static final Tag MORNING = new Tag("morning");
    private static final Tag REHAB = new Tag("rehab");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_clientType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new DeleteGroupCommand(Tag.GYM));
    }

    @Test
    public void execute_groupWithClients_removedFromGroupsAndClients() {
        Person morningAlice = new PersonBuilder(ALICE).withTags("gym", "morning").build();
        Person morningBenson = new PersonBuilder(BENSON).withTags("morning", "rehab").build();
        model.setPerson(ALICE, morningAlice);
        model.setPerson(BENSON, morningBenson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(morningAlice, new PersonBuilder(ALICE).withTags("gym").build());
        expectedModel.setPerson(morningBenson, new PersonBuilder(BENSON).withTags("rehab").build());
        AddressBook expectedAddressBook = new AddressBook(expectedModel.getAddressBook());
        expectedAddressBook.setGroups(List.of(REHAB));
        expectedModel.setAddressBook(expectedAddressBook);

        assertCommandSuccess(new DeleteGroupCommand(MORNING), model,
                String.format(DeleteGroupCommand.MESSAGE_DELETED, "morning", 2), expectedModel);
        assertFalse(model.hasGroup(MORNING));
        assertTrue(model.hasGroup(REHAB));
    }

    @Test
    public void execute_emptyGroup_removed() {
        model.addGroup(MORNING);
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        assertCommandSuccess(new DeleteGroupCommand(MORNING), model,
                String.format(DeleteGroupCommand.MESSAGE_DELETED, "morning", 0), expectedModel);
    }

    @Test
    public void execute_missingGroup_throwsCommandException() {
        assertCommandFailure(new DeleteGroupCommand(MORNING), model,
                String.format(DeleteGroupCommand.MESSAGE_GROUP_NOT_FOUND, "morning"));
    }

    @Test
    public void equals() {
        DeleteGroupCommand command = new DeleteGroupCommand(MORNING);

        // same object -> returns true
        assertTrue(command.equals(command));

        // same values -> returns true
        assertTrue(command.equals(new DeleteGroupCommand(new Tag("morning"))));

        // different types -> returns false
        assertFalse(command.equals(new GroupCommand(MORNING)));

        // null -> returns false
        assertFalse(command.equals(null));

        // different group -> returns false
        assertFalse(command.equals(new DeleteGroupCommand(REHAB)));
    }

    @Test
    public void toStringMethod() {
        String expected = DeleteGroupCommand.class.getCanonicalName() + "{group=" + MORNING + "}";
        assertEquals(expected, new DeleteGroupCommand(MORNING).toString());
    }
}
