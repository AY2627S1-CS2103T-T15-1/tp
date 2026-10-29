package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Checks that reading instructions does not depend on write access to client data.
 */
public class HelpClearReadOnlyIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(AMY);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("cindy.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Write access unavailable");
            }
        };
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_helpWhenSavingFails_returnsShowHelpResult() throws Exception {
        assertTrue(logic.execute("help").isShowHelp());
    }

    @Test
    public void execute_unconfirmedClearWhenSavingFails_returnsPromptWithoutChangingData() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());

        assertEquals(ClearCommand.MESSAGE_CONFIRMATION, logic.execute("clear").getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_confirmedClearWhenSavingFails_stillReportsSaveFailure() {
        assertThrows(CommandException.class, () -> logic.execute("clear --confirm"));
    }

    @Test
    public void execute_confirmedClearWhenSavingSucceeds_persistsClearedData() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("cindy.json"));
        LogicManager writableLogic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        assertEquals(ClearCommand.MESSAGE_SUCCESS, writableLogic.execute("clear --confirm").getFeedbackToUser());
        assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
    }
}
