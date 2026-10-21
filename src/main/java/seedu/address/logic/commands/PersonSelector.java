package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Describes how a command chooses the client it acts on: either by the client's ID,
 * or by a part of the client's name.
 */
public class PersonSelector {

    public static final String MESSAGE_NO_PERSON_WITH_ID = "No client has the ID %1$s.";
    public static final String MESSAGE_NO_PERSON_WITH_NAME = "No client has a name containing \"%1$s\".";
    public static final String MESSAGE_MULTIPLE_MATCHES = "%1$d clients have a name containing \"%2$s\". "
            + "They are listed below.\n"
            + "Run %3$s again with " + PREFIX_ID + " %4$s instead of --name, keeping all other parameters.";

    private final PersonId id;
    private final String nameKeyword;

    private PersonSelector(PersonId id, String nameKeyword) {
        assert (id == null) != (nameKeyword == null) : "A client is chosen either by ID or by name";
        this.id = id;
        this.nameKeyword = nameKeyword;
    }

    /**
     * Returns a selector that chooses the client with the given {@code id}.
     */
    public static PersonSelector byId(PersonId id) {
        requireNonNull(id);
        return new PersonSelector(id, null);
    }

    /**
     * Returns a selector that chooses the clients whose names contain {@code nameKeyword},
     * ignoring upper and lower case.
     * {@code nameKeyword} must not be blank.
     */
    public static PersonSelector byName(String nameKeyword) {
        requireNonNull(nameKeyword);
        String normalizedKeyword = nameKeyword.trim().replaceAll("\\s+", " ");
        if (normalizedKeyword.isEmpty()) {
            throw new IllegalArgumentException("The name to look for must not be blank.");
        }
        return new PersonSelector(null, normalizedKeyword);
    }

    /**
     * Returns true if {@code person} is chosen by this selector.
     */
    public boolean matches(Person person) {
        requireNonNull(person);
        if (id != null) {
            return person.getId().equals(id);
        }
        return person.getName().fullName.toLowerCase(Locale.ROOT)
                .contains(nameKeyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the persons in {@code persons} chosen by this selector, in order of their IDs.
     */
    public List<Person> findMatches(List<Person> persons) {
        requireNonNull(persons);
        return persons.stream()
                .filter(this::matches)
                .sorted(Comparator.comparing(Person::getId))
                .toList();
    }

    /**
     * Returns the message to show when no client is chosen by this selector.
     */
    public String getNoMatchMessage() {
        return id != null
                ? String.format(MESSAGE_NO_PERSON_WITH_ID, id)
                : String.format(MESSAGE_NO_PERSON_WITH_NAME, nameKeyword);
    }

    /**
     * Returns the message to show when this selector chooses more than one client.
     *
     * @param matches The clients chosen by this selector. Must contain at least two clients.
     * @param commandWord The command word of the command that used this selector, for the example in the message.
     */
    public String getMultipleMatchesMessage(List<Person> matches, String commandWord) {
        requireNonNull(matches);
        requireNonNull(commandWord);
        assert matches.size() > 1 : "Only a name can match more than one client";
        assert id == null : "An ID matches at most one client";
        return String.format(MESSAGE_MULTIPLE_MATCHES, matches.size(), nameKeyword, commandWord,
                matches.get(0).getId());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonSelector otherSelector)) {
            return false;
        }

        return Objects.equals(id, otherSelector.id)
                && Objects.equals(nameKeyword, otherSelector.nameKeyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameKeyword);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("nameKeyword", nameKeyword)
                .toString();
    }
}
