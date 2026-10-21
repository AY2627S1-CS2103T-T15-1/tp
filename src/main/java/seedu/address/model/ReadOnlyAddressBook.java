package seedu.address.model;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns the ID to give the next person added to the address book.
     * It is greater than the ID of every person ever added, so IDs are never reused.
     */
    PersonId getNextPersonId();

    /**
     * Returns an unmodifiable list of the custom groups, in the order they were created.
     * It contains no duplicates and no client types.
     */
    List<Tag> getGroupList();

}
