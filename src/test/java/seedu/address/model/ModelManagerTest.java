package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPersonWithPhone_nullPhone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPersonWithPhone(null));
    }

    @Test
    public void hasPersonWithPhone_phoneNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPersonWithPhone(ALICE.getPhone()));
    }

    @Test
    public void hasPersonWithPhone_phoneInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPersonWithPhone(ALICE.getPhone()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void getFilteredPersonList_personsAddedOutOfOrder_sortedByName() {
        modelManager.addPerson(BENSON);
        modelManager.addPerson(ALICE);
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_withComparator_sortsUntilNextUpdateWithoutComparator() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);

        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS,
                Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        assertEquals(List.of(BENSON, ALICE), modelManager.getFilteredPersonList());

        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_nullComparator_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, null));
    }

    @Test
    public void hasGroup_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasGroup(null));
    }

    @Test
    public void addGroup_newGroup_hasGroup() {
        Tag morning = new Tag("morning");
        assertFalse(modelManager.hasGroup(morning));
        modelManager.addGroup(morning);
        assertTrue(modelManager.hasGroup(morning));
    }

    @Test
    public void deleteGroup_existingGroup_groupRemoved() {
        Tag morning = new Tag("morning");
        modelManager.addGroup(morning);
        modelManager.deleteGroup(morning);
        assertFalse(modelManager.hasGroup(morning));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        modelManager.updateFilteredPersonList(PersonSelector.byName(ALICE.getName().fullName)::matches);
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
