package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code GroupCommand}.
 */
public class GroupCommandTest {

    private static final Tag MORNING = new Tag("morning");
    private static final Tag REHAB = new Tag("rehab");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupCommand(null));
    }

    @Test
    public void constructor_clientType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new GroupCommand(Tag.PERSONAL));
    }

    @Test
    public void execute_newGroup_success() {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.addGroup(MORNING);

        assertCommandSuccess(new GroupCommand(MORNING), model,
                String.format(GroupCommand.MESSAGE_SUCCESS, "morning"), expectedModel);
        assertTrue(model.hasGroup(MORNING));
    }

    @Test
    public void execute_existingGroup_throwsCommandException() {
        model.addGroup(MORNING);

        assertCommandFailure(new GroupCommand(MORNING), model,
                String.format(GroupCommand.MESSAGE_DUPLICATE_GROUP, "morning"));
    }

    @Test
    public void equals() {
        GroupCommand morningCommand = new GroupCommand(MORNING);

        // same object -> returns true
        assertTrue(morningCommand.equals(morningCommand));

        // same values -> returns true
        assertTrue(morningCommand.equals(new GroupCommand(new Tag("morning"))));

        // different types -> returns false
        assertFalse(morningCommand.equals(1));

        // null -> returns false
        assertFalse(morningCommand.equals(null));

        // different group -> returns false
        assertFalse(morningCommand.equals(new GroupCommand(REHAB)));
    }

    @Test
    public void toStringMethod() {
        GroupCommand command = new GroupCommand(MORNING);
        String expected = GroupCommand.class.getCanonicalName() + "{group=" + MORNING + "}";
        assertEquals(expected, command.toString());
    }
}
