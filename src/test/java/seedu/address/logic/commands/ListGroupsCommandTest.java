package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ListGroupsCommand}.
 */
public class ListGroupsCommandTest {

    @Test
    public void execute_noGroups_showsNoGroupsMessage() {
        Model model = modelWithGroups(List.of());
        Model expectedModel = modelWithGroups(List.of());
        assertCommandSuccess(new ListGroupsCommand(), model, ListGroupsCommand.MESSAGE_NO_GROUPS, expectedModel);
    }

    @Test
    public void execute_emptyGroups_listsGroupsAlphabetically() {
        List<Tag> groups = List.of(new Tag("rehab"), new Tag("morning"), new Tag("strength"));
        Model model = modelWithGroups(groups);
        Model expectedModel = modelWithGroups(groups);
        assertCommandSuccess(new ListGroupsCommand(), model,
                String.format(ListGroupsCommand.MESSAGE_SUCCESS, 3, "morning, rehab, strength"), expectedModel);
    }

    @Test
    public void execute_oneGroup_listsGroup() {
        Model model = modelWithGroups(List.of(new Tag("morning")));
        Model expectedModel = modelWithGroups(List.of(new Tag("morning")));
        assertCommandSuccess(new ListGroupsCommand(), model,
                String.format(ListGroupsCommand.MESSAGE_SUCCESS, 1, "morning"), expectedModel);
    }

    @Test
    public void equals() {
        ListGroupsCommand command = new ListGroupsCommand();
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ListGroupsCommand()));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ExitCommand()));
    }

    private static Model modelWithGroups(List<Tag> groups) {
        AddressBook addressBook = new AddressBook(getTypicalAddressBook());
        addressBook.setGroups(groups);
        return new ModelManager(addressBook, new UserPrefs());
    }
}
