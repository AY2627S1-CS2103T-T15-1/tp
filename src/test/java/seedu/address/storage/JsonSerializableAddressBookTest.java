package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path DUPLICATE_ID_FILE = TEST_DATA_FOLDER.resolve("duplicateIdAddressBook.json");
    private static final Path LEGACY_PERSONS_FILE =
            TEST_DATA_FOLDER.resolve("legacyPersonsWithoutIdsAddressBook.json");
    private static final Path GROUPS_FILE = TEST_DATA_FOLDER.resolve("groupsAddressBook.json");
    private static final Path LEGACY_TAGS_FILE =
            TEST_DATA_FOLDER.resolve("legacyTagsWithoutGroupsAddressBook.json");
    private static final Path DUPLICATE_GROUP_FILE = TEST_DATA_FOLDER.resolve("duplicateGroupAddressBook.json");
    private static final Path CLIENT_TYPE_GROUP_FILE = TEST_DATA_FOLDER.resolve("clientTypeGroupAddressBook.json");
    private static final Path INVALID_GROUP_FILE = TEST_DATA_FOLDER.resolve("invalidGroupAddressBook.json");
    private static final Path NULL_GROUP_FILE = TEST_DATA_FOLDER.resolve("nullGroupAddressBook.json");

    private static final Tag MORNING = new Tag("morning");
    private static final Tag REHAB = new Tag("rehab");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_sameNameAndDifferentPhone_success() throws Exception {
        List<JsonAdaptedPerson> persons = List.of(
                new JsonAdaptedPerson(1, "Alice Pauline", "94351253"),
                new JsonAdaptedPerson(2, "Alice Pauline", "81234567"));
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(persons, 3);

        assertEquals(2, data.toModelType().getPersonList().size());
    }

    @Test
    public void toModelType_largestClientIdSaved_loadsWithNoIdsLeft() throws Exception {
        List<JsonAdaptedPerson> persons = List.of(
                new JsonAdaptedPerson(PersonId.MAX_CLIENT_ID, "Alice Pauline", "94351253"));
        AddressBook addressBook = new JsonSerializableAddressBook(persons, Integer.MAX_VALUE).toModelType();

        assertEquals(new PersonId(Integer.MAX_VALUE), addressBook.getNextPersonId());
    }

    @Test
    public void toModelType_idAfterLargestClientIdSaved_throwsIllegalValueException() {
        List<JsonAdaptedPerson> persons = List.of(
                new JsonAdaptedPerson(Integer.MAX_VALUE, "Alice Pauline", "94351253"));
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(persons, null);

        assertThrows(IllegalValueException.class, PersonId.MESSAGE_CLIENT_ID_CONSTRAINTS, data::toModelType);
    }

    @Test
    public void toModelType_legacyFileWithoutIds_assignsIdsInFileOrder() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(LEGACY_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        assertEquals(TypicalPersons.getTypicalAddressBook(), dataFromFile.toModelType());
    }

    @Test
    public void toModelType_duplicateIds_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_ID_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_ID,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_someIdsMissing_assignsIdsAfterLargestSavedId() throws Exception {
        List<JsonAdaptedPerson> persons = List.of(
                new JsonAdaptedPerson(4, "Alice Pauline", "94351253"),
                new JsonAdaptedPerson(null, "Benson Meier", "98765432"));
        AddressBook addressBook = new JsonSerializableAddressBook(persons, null).toModelType();

        assertEquals(new PersonId(5), addressBook.getPersonList().get(1).getId());
        assertEquals(new PersonId(6), addressBook.getNextPersonId());
    }

    @Test
    public void toModelType_savedNextIdLargerThanAllIds_keepsSavedNextId() throws Exception {
        List<JsonAdaptedPerson> persons = List.of(new JsonAdaptedPerson(1, "Alice Pauline", "94351253"));
        AddressBook addressBook = new JsonSerializableAddressBook(persons, 10).toModelType();

        assertEquals(new PersonId(10), addressBook.getNextPersonId());
    }

    @Test
    public void toModelType_savedNextIdNotLargerThanAllIds_usesIdAfterLargestId() throws Exception {
        List<JsonAdaptedPerson> persons = List.of(new JsonAdaptedPerson(7, "Alice Pauline", "94351253"));
        AddressBook addressBook = new JsonSerializableAddressBook(persons, 3).toModelType();

        assertEquals(new PersonId(8), addressBook.getNextPersonId());
    }

    @Test
    public void toModelType_invalidNextId_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), 0);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_NEXT_ID,
                data::toModelType);
    }

    @Test
    public void constructor_fromAddressBook_savesNextId() throws Exception {
        AddressBook original = TypicalPersons.getTypicalAddressBook();
        original.removePerson(TypicalPersons.GEORGE);
        AddressBook roundTripped = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(original, roundTripped);
        assertEquals(TypicalPersons.GEORGE.getId().next(), roundTripped.getNextPersonId());
    }

    @Test
    public void toModelType_groupsFile_keepsGroupsInSavedOrder() throws Exception {
        AddressBook addressBook = JsonUtil.readJsonFile(GROUPS_FILE, JsonSerializableAddressBook.class).get()
                .toModelType();
        assertEquals(List.of(MORNING, REHAB), addressBook.getGroupList());
    }

    @Test
    public void toModelType_legacyFileWithoutGroups_addsCustomTagsAsGroups() throws Exception {
        AddressBook addressBook = JsonUtil.readJsonFile(LEGACY_TAGS_FILE, JsonSerializableAddressBook.class).get()
                .toModelType();
        assertEquals(List.of(MORNING), addressBook.getGroupList());
    }

    @Test
    public void toModelType_duplicateGroups_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_GROUP_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_GROUP,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_clientTypeGroup_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(CLIENT_TYPE_GROUP_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_CLIENT_TYPE_GROUP,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidGroup_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_GROUP_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_nullGroup_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(NULL_GROUP_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_MISSING_GROUP,
                dataFromFile::toModelType);
    }

    @Test
    public void constructor_fromAddressBookWithGroups_savesGroups() throws Exception {
        AddressBook original = TypicalPersons.getTypicalAddressBook();
        original.addGroup(REHAB);
        original.addGroup(MORNING);
        AddressBook roundTripped = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(original, roundTripped);
        assertEquals(List.of(REHAB, MORNING), roundTripped.getGroupList());
    }
}
