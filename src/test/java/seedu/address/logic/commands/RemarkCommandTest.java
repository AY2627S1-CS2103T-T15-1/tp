package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_ARGUMENTS;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code RemarkCommand}.
 */
public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_throwsArgumentsException() {
        RemarkCommand command = new RemarkCommand(Index.fromOneBased(1), "Likes to swim.");
        String expectedMessage = String.format(MESSAGE_ARGUMENTS, 1, "Likes to swim.");
        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void equals() {
        RemarkCommand firstCommand = new RemarkCommand(Index.fromOneBased(1), "Likes to swim.");
        RemarkCommand firstCommandCopy = new RemarkCommand(Index.fromOneBased(1), "Likes to swim.");
        RemarkCommand differentIndexCommand = new RemarkCommand(Index.fromOneBased(2), "Likes to swim.");
        RemarkCommand differentRemarkCommand = new RemarkCommand(Index.fromOneBased(1), "Likes to run.");

        assertEquals(firstCommand, firstCommand);
        assertEquals(firstCommand, firstCommandCopy);
        assertNotEquals(firstCommand, differentIndexCommand);
        assertNotEquals(firstCommand, differentRemarkCommand);
        assertNotEquals(firstCommand, null);
        assertNotEquals(firstCommand, new Object());
    }
}
