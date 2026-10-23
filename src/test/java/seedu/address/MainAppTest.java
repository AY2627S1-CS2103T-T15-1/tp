package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;
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

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertEquals(invalidData, Files.readString(addressBookFilePath));
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }
}
