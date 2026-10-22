package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private static final Tag MORNING = new Tag("morning");
    private static final Tag REHAB = new Tag("rehab");

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        Person duplicateAlice = new PersonBuilder(ALICE).build();
        List<Person> newPersons = List.of(ALICE, duplicateAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameNameAndDifferentPhoneInAddressBook_returnsFalse() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void hasPersonWithPhone_nullPhone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPersonWithPhone(null));
    }

    @Test
    public void hasPersonWithPhone_phoneNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPersonWithPhone(ALICE.getPhone()));
    }

    @Test
    public void hasPersonWithPhone_phoneInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPersonWithPhone(ALICE.getPhone()));
    }

    @Test
    public void equals() {
        AddressBook withAlice = new AddressBook();
        withAlice.addPerson(ALICE);
        AddressBook withAliceCopy = new AddressBook(withAlice);

        // same values -> returns true
        assertTrue(withAlice.equals(withAliceCopy));
        assertEquals(withAlice.hashCode(), withAliceCopy.hashCode());

        // same object -> returns true
        assertTrue(withAlice.equals(withAlice));

        // null -> returns false
        assertFalse(withAlice.equals(null));

        // different types -> returns false
        assertFalse(withAlice.equals(5));

        // same persons, different next ID -> returns false
        withAliceCopy.setNextPersonId(new PersonId(50));
        assertFalse(withAlice.equals(withAliceCopy));
    }

    @Test
    public void getNextPersonId_emptyAddressBook_returnsFirstId() {
        assertEquals(new PersonId(1), addressBook.getNextPersonId());
    }

    @Test
    public void addPerson_validPerson_reservesItsId() {
        addressBook.addPerson(new PersonBuilder(ALICE).withId(5).build());
        assertEquals(new PersonId(6), addressBook.getNextPersonId());
    }

    @Test
    public void addPerson_personWithSmallerId_keepsNextId() {
        addressBook.addPerson(new PersonBuilder(ALICE).withId(5).build());
        addressBook.addPerson(new PersonBuilder().withId(2).withName("Bob Choo").build());
        assertEquals(new PersonId(6), addressBook.getNextPersonId());
    }

    @Test
    public void addPerson_idAlreadyInUse_throwsIllegalArgumentException() {
        addressBook.addPerson(ALICE);
        Person differentPersonWithSameId = new PersonBuilder().withId(ALICE.getId().value).withName("Bob Choo")
                .build();
        assertThrows(IllegalArgumentException.class, () -> addressBook.addPerson(differentPersonWithSameId));
    }

    @Test
    public void removePerson_lastAddedPerson_doesNotFreeItsId() {
        addressBook.addPerson(ALICE);
        addressBook.removePerson(ALICE);
        assertEquals(ALICE.getId().next(), addressBook.getNextPersonId());
    }

    @Test
    public void hasPersonWithId() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPersonWithId(ALICE.getId()));
        assertFalse(addressBook.hasPersonWithId(ALICE.getId().next()));
    }

    @Test
    public void setNextPersonId_notLargerThanExistingIds_throwsIllegalArgumentException() {
        addressBook.addPerson(ALICE);
        assertThrows(IllegalArgumentException.class, () -> addressBook.setNextPersonId(ALICE.getId()));
    }

    @Test
    public void resetData_addressBookWithLargerNextId_copiesNextId() {
        AddressBook source = new AddressBook();
        source.addPerson(ALICE);
        source.setNextPersonId(new PersonId(20));
        addressBook.resetData(source);
        assertEquals(new PersonId(20), addressBook.getNextPersonId());
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void hasGroup_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasGroup(null));
    }

    @Test
    public void addGroup_newGroup_hasGroup() {
        assertFalse(addressBook.hasGroup(MORNING));
        addressBook.addGroup(MORNING);
        assertTrue(addressBook.hasGroup(MORNING));
        assertEquals(List.of(MORNING), addressBook.getGroupList());
    }

    @Test
    public void addGroup_existingGroup_keepsOneCopy() {
        addressBook.addGroup(MORNING);
        addressBook.addGroup(MORNING);
        assertEquals(List.of(MORNING), addressBook.getGroupList());
    }

    @Test
    public void addGroup_clientType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> addressBook.addGroup(Tag.GYM));
    }

    @Test
    public void getGroupList_keepsCreationOrder() {
        addressBook.addGroup(REHAB);
        addressBook.addGroup(MORNING);
        assertEquals(List.of(REHAB, MORNING), addressBook.getGroupList());
    }

    @Test
    public void getGroupList_modifyList_throwsUnsupportedOperationException() {
        addressBook.addGroup(MORNING);
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getGroupList().remove(0));
    }

    @Test
    public void addPerson_personWithCustomTag_addsTagAsGroup() {
        addressBook.addPerson(new PersonBuilder(ALICE).withTags("gym", "morning").build());
        assertEquals(List.of(MORNING), addressBook.getGroupList());
    }

    @Test
    public void setPerson_personWithNewCustomTag_addsTagAsGroup() {
        addressBook.addPerson(ALICE);
        addressBook.setPerson(ALICE, new PersonBuilder(ALICE).withTags("rehab").build());
        assertTrue(addressBook.hasGroup(REHAB));
    }

    @Test
    public void resetData_withGroups_copiesGroups() {
        AddressBook newData = new AddressBook();
        newData.addGroup(MORNING);
        addressBook.addGroup(REHAB);
        addressBook.resetData(newData);
        assertEquals(List.of(MORNING), addressBook.getGroupList());
    }

    @Test
    public void setGroups_keepsCustomTagsOfPersons() {
        addressBook.addPerson(new PersonBuilder(ALICE).withTags("rehab").build());
        addressBook.setGroups(List.of(MORNING));
        assertEquals(List.of(MORNING, REHAB), addressBook.getGroupList());
    }

    @Test
    public void removeGroup_groupWithPersons_removesGroupFromRegistryAndPersons() {
        Person morningAlice = new PersonBuilder(ALICE).withTags("gym", "morning").build();
        addressBook.addPerson(morningAlice);
        addressBook.addGroup(REHAB);

        addressBook.removeGroup(MORNING);

        assertEquals(List.of(REHAB), addressBook.getGroupList());
        assertEquals(List.of(new PersonBuilder(ALICE).withTags("gym").build()), addressBook.getPersonList());
    }

    @Test
    public void removeGroup_missingGroup_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> addressBook.removeGroup(MORNING));
    }

    @Test
    public void equals_differentGroups_returnsFalse() {
        AddressBook withGroup = new AddressBook();
        withGroup.addGroup(MORNING);
        assertFalse(addressBook.equals(withGroup));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", nextPersonId=" + addressBook.getNextPersonId() + ", groups=[]}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public PersonId getNextPersonId() {
            return persons.stream().map(Person::getId).max(PersonId::compareTo)
                    .map(PersonId::next).orElse(new PersonId(1));
        }

        @Override
        public List<Tag> getGroupList() {
            return List.of();
        }
    }

}
