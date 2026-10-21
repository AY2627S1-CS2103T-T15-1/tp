package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_ID = "Persons list contains more than one person with the same ID.";
    public static final String MESSAGE_INVALID_NEXT_ID = "The next person ID is not a positive whole number.";
    public static final String MESSAGE_MISSING_PERSON = "Persons list contains an empty entry.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final Integer nextPersonId;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and next person ID.
     * {@code nextPersonId} can be null for data files saved before clients had IDs.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("nextPersonId") Integer nextPersonId) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        this.nextPersonId = nextPersonId;
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        nextPersonId = source.getNextPersonId().value;
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     * Persons saved without an ID are given new IDs that follow the largest saved ID.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        if (persons.contains(null)) {
            throw new IllegalValueException(MESSAGE_MISSING_PERSON);
        }

        AddressBook addressBook = new AddressBook();
        int nextFreeId = getLargestSavedId() + 1;
        Set<Integer> usedIds = new HashSet<>();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            JsonAdaptedPerson personWithId = jsonAdaptedPerson.hasId()
                    ? jsonAdaptedPerson
                    : jsonAdaptedPerson.withId(nextFreeId++);
            Person person = personWithId.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            if (!usedIds.add(person.getId().value)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_ID);
            }
            addressBook.addPerson(person);
        }

        if (nextPersonId != null) {
            if (!PersonId.isValidId(nextPersonId)) {
                throw new IllegalValueException(MESSAGE_INVALID_NEXT_ID);
            }
            PersonId savedNextId = new PersonId(nextPersonId);
            if (savedNextId.compareTo(addressBook.getNextPersonId()) > 0) {
                // keeps IDs of deleted persons from being reused
                addressBook.setNextPersonId(savedNextId);
            }
        }
        return addressBook;
    }

    /**
     * Returns the largest ID among the persons saved with an ID, or 0 if there are none.
     */
    private int getLargestSavedId() {
        return persons.stream()
                .filter(JsonAdaptedPerson::hasId)
                .mapToInt(JsonAdaptedPerson::getId)
                .max()
                .orElse(0);
    }

}
