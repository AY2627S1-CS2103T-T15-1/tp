package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private static final PersonId FIRST_PERSON_ID = new PersonId(1);

    private final UniquePersonList persons = new UniquePersonList();
    private PersonId nextPersonId = FIRST_PERSON_ID;

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        nextPersonId = FIRST_PERSON_ID;
        persons.forEach(this::reserveIdOf);
    }

    /**
     * Sets the ID to give the next person added to the address book.
     * {@code nextPersonId} must be greater than the ID of every person in the address book.
     */
    public void setNextPersonId(PersonId nextPersonId) {
        requireNonNull(nextPersonId);
        if (persons.asUnmodifiableObservableList().stream()
                .anyMatch(person -> person.getId().compareTo(nextPersonId) >= 0)) {
            throw new IllegalArgumentException("The next person ID must be greater than every existing person ID.");
        }
        this.nextPersonId = nextPersonId;
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setNextPersonId(newData.getNextPersonId());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Returns true if a person with the given {@code id} exists in the address book.
     */
    public boolean hasPersonWithId(PersonId id) {
        requireNonNull(id);
        return persons.asUnmodifiableObservableList().stream()
                .anyMatch(person -> person.getId().equals(id));
    }

    /**
     * Returns true if a person with {@code phone} exists in the address book.
     */
    public boolean hasPersonWithPhone(Phone phone) {
        requireNonNull(phone);
        return persons.asUnmodifiableObservableList().stream()
                .anyMatch(person -> person.getPhone().equals(phone));
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book, and no other person may have the same ID.
     */
    public void addPerson(Person p) {
        requireNonNull(p);
        if (hasPersonWithId(p.getId())) {
            throw new IllegalArgumentException("Another person already has the ID " + p.getId() + ".");
        }
        persons.add(p);
        reserveIdOf(p);
    }

    /**
     * Ensures that the ID of {@code person} will not be given to any person added later.
     */
    private void reserveIdOf(Person person) {
        if (person.getId().compareTo(nextPersonId) >= 0) {
            nextPersonId = person.getId().next();
        }
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        assert target.getId().equals(editedPerson.getId()) : "Editing a person should not change their ID";

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("nextPersonId", nextPersonId)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public PersonId getNextPersonId() {
        return nextPersonId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && nextPersonId.equals(otherAddressBook.nextPersonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, nextPersonId);
    }
}
