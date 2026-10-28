package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TODAY;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.AddressBookBuilder;

public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void defaultFilePaths_useAbsoluteDevelopmentHome() throws Exception {
        Path home = Path.of("").toAbsolutePath().normalize();
        Field addressBookPath = MainApp.class.getDeclaredField("ADDRESS_BOOK_FILE_PATH");
        addressBookPath.setAccessible(true);
        assertEquals(home.resolve("data/cindy.json"), addressBookPath.get(null));

        Field preferencesPath = MainApp.class.getDeclaredField("USER_PREFS_FILE_PATH");
        preferencesPath.setAccessible(true);
        assertEquals(home.resolve("preferences.json"), preferencesPath.get(null));

        Field logPath = LogsCenter.class.getDeclaredField("LOG_FILE");
        logPath.setAccessible(true);
        assertEquals(home.resolve("cindy.log").toString().replace("%", "%%"), logPath.get(null));
    }

    @Test
    public void initModelManager_missingAddressBook_createsFileWithSampleData() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("data").resolve("cindy.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertTrue(Files.exists(addressBookFilePath));
        assertEquals(SampleDataUtil.getSampleAddressBook(), model.getAddressBook());
        ReadOnlyAddressBook savedAddressBook = storage.readAddressBook().get();
        assertEquals(model.getAddressBook(), savedAddressBook);
    }

    @Test
    public void initModelManager_freshSamples_supportTodayCommands() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("data/cindy.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(addressBookFilePath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        Model model = new MainApp().initModelManager(storage, new UserPrefs());
        LogicManager logic = new LogicManager(model, storage);

        logic.execute(ListCommand.COMMAND_WORD + " " + PREFIX_TODAY);

        assertEquals(List.of("Alex Yeoh"), model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName).toList());
        CommandResult appointments = logic.execute(AddAppointmentCommand.COMMAND_WORD + " "
                + PREFIX_DATE + " " + LocalDate.now());
        assertTrue(appointments.getFeedbackToUser().contains("Alex Yeoh"));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void initModelManager_existingAddressBook_usesSavedData() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("cindy.json");
        AddressBook savedAddressBook = new AddressBookBuilder().withPerson(ALICE).build();
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        addressBookStorage.saveAddressBook(savedAddressBook);
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertEquals(savedAddressBook, model.getAddressBook());
    }

    @Test
    public void initModelManager_initialSaveFails_startsWithSampleData() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("cindy.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Unable to save address book");
            }
        };
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertFalse(Files.exists(addressBookFilePath));
        assertEquals(SampleDataUtil.getSampleAddressBook(), model.getAddressBook());
    }

    @Test
    public void initModelManager_invalidAddressBook_doesNotOverwriteFile() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("cindy.json");
        String invalidData = "not valid json";
        Files.writeString(addressBookFilePath, invalidData);
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(addressBookFilePath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));

        assertThrows(DataLoadingException.class, () -> new MainApp().initModelManager(storage, new UserPrefs()));

        assertEquals(invalidData, Files.readString(addressBookFilePath));
    }

    @Test
    public void initModelManager_repairedAddressBook_loadsSavedClients() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("cindy.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        Files.writeString(addressBookFilePath, "{\"persons\": [");
        MainApp app = new MainApp();

        assertThrows(DataLoadingException.class, () -> app.initModelManager(storage, new UserPrefs()));
        AddressBook restoredData = new AddressBookBuilder().withPerson(ALICE).build();
        addressBookStorage.saveAddressBook(restoredData);

        assertEquals(restoredData, app.initModelManager(storage, new UserPrefs()).getAddressBook());
    }

    @Test
    public void stop_failedModelInitialization_preservesFiles() throws Exception {
        Path addressBookFilePath = temporaryFolder.resolve("cindy.json");
        Path preferencesFilePath = temporaryFolder.resolve("preferences.json");
        String invalidData = "{\"persons\": [";
        Files.writeString(addressBookFilePath, invalidData);
        MainApp app = new MainApp();
        app.storage = new StorageManager(new JsonAddressBookStorage(addressBookFilePath),
                new JsonUserPrefsStorage(preferencesFilePath));

        assertThrows(DataLoadingException.class, () -> app.initModelManager(app.storage, new UserPrefs()));
        assertDoesNotThrow(app::stop);

        assertEquals(invalidData, Files.readString(addressBookFilePath));
        assertFalse(Files.exists(preferencesFilePath));
    }
}
