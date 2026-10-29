package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks that failed saves cannot leave client changes pending for a later command to persist.
 */
public class SaveFailureIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private FailingStorage addressBookStorage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        addressBookStorage = new FailingStorage(temporaryFolder.resolve("cindy.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_failedClear_restoresDataBeforeLaterSave() throws Exception {
        assertFailedClearPreservesData(new IOException("Disk unavailable"));
    }

    @Test
    public void execute_failedClearWithAccessDenied_restoresDataBeforeLaterSave() throws Exception {
        assertFailedClearPreservesData(new AccessDeniedException("cindy.json"));
    }

    private void assertFailedClearPreservesData(IOException failure) throws Exception {
        Person amy = new PersonBuilder(AMY).withId(7).withNote("Original note")
                .withWorkoutPlan("Original plan").withTags("morning").build();
        model.addPerson(amy);
        model.addGroup(new Tag("empty-group"));
        AddressBook original = new AddressBook(model.getAddressBook());
        original.setNextPersonId(new PersonId(10));
        model.setAddressBook(original);
        model.updateFilteredPersonList(person -> person.getId().value == 7,
                Comparator.comparing(Person::getId).reversed());
        addressBookStorage.saveAddressBook(original);
        addressBookStorage.failure = failure;

        assertThrows(CommandException.class, () -> logic.execute("clear --confirm"));

        assertEquals(original, model.getAddressBook());
        assertEquals(List.of(amy), model.getFilteredPersonList());
        assertEquals(new PersonId(10), model.getNextPersonId());
        assertEquals(original, addressBookStorage.readAddressBook().orElseThrow());

        addressBookStorage.failure = null;
        logic.execute("help");
        assertEquals(original, addressBookStorage.readAddressBook().orElseThrow());
        logic.execute("group --name recovery");
        AddressBook expected = new AddressBook(original);
        expected.addGroup(new Tag("recovery"));
        assertEquals(expected, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedClearWithNoClients_preservesGroupsAndNextId() throws Exception {
        AddressBook original = new AddressBook();
        original.addGroup(new Tag("empty-group"));
        original.setNextPersonId(new PersonId(23));
        model.setAddressBook(original);
        addressBookStorage.failure = new IOException("Disk unavailable");

        assertThrows(CommandException.class, () -> logic.execute("clear --confirm"));

        assertEquals(original, model.getAddressBook());
        assertEquals(new PersonId(23), model.getNextPersonId());
    }

    @Test
    public void execute_failedPlan_restoresPreviousPlan() throws Exception {
        Person amy = new PersonBuilder(AMY).withId(1).withWorkoutPlan("Original plan").build();
        model.addPerson(amy);
        addressBookStorage.saveAddressBook(model.getAddressBook());
        addressBookStorage.failure = new IOException("Disk unavailable");

        assertThrows(CommandException.class, () -> logic.execute("plan --id 1 --plan \"Replacement\""));

        assertEquals(List.of(amy), model.getAddressBook().getPersonList());
        assertEquals(List.of(amy), addressBookStorage.readAddressBook().orElseThrow().getPersonList());
    }

    private static class FailingStorage extends JsonAddressBookStorage {
        private IOException failure;

        FailingStorage(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            if (failure != null) {
                throw failure;
            }
            super.saveAddressBook(addressBook);
        }
    }
}
