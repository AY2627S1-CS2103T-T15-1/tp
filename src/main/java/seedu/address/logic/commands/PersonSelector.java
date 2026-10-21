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
 * Describes how a command chooses clients: by the client's ID, by a part of the client's name,
 * or by a part of the client's phone number.
 */
public class PersonSelector {

    public static final String MESSAGE_NO_PERSON_WITH_ID = "No client has the ID %1$s.";
    public static final String MESSAGE_NO_PERSON_WITH_NAME = "No client has a name containing \"%1$s\".";
    public static final String MESSAGE_NO_PERSON_WITH_PHONE = "No client has a phone number containing \"%1$s\".";
    public static final String MESSAGE_MULTIPLE_MATCHES = "%1$d clients have a %2$s containing \"%3$s\". "
            + "They are listed below.\n"
            + "Use " + PREFIX_ID + " to choose one of them, e.g. %4$s";

    /** Characters that may separate the digits of a phone number, which are ignored when matching. */
    private static final String PHONE_SEPARATORS = "[\\s()-]";
    private static final String PHONE_KEYWORD_REGEX = "\\+?[0-9]+";

    private final PersonId id;
    private final String nameKeyword;
    private final String phoneKeyword;

    private PersonSelector(PersonId id, String nameKeyword, String phoneKeyword) {
        int detailCount = (id == null ? 0 : 1) + (nameKeyword == null ? 0 : 1) + (phoneKeyword == null ? 0 : 1);
        assert detailCount == 1 : "Clients are chosen by exactly one detail";
        this.id = id;
        this.nameKeyword = nameKeyword;
        this.phoneKeyword = phoneKeyword;
    }

    /**
     * Returns a selector that chooses the client with the given {@code id}.
     */
    public static PersonSelector byId(PersonId id) {
        requireNonNull(id);
        return new PersonSelector(id, null, null);
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
        return new PersonSelector(null, normalizedKeyword, null);
    }

    /**
     * Returns a selector that chooses the clients whose phone numbers contain {@code phoneKeyword}.
     * Spaces, parentheses and hyphens in {@code phoneKeyword} are ignored.
     * {@code phoneKeyword} must be valid, as declared in {@link #isValidPhoneKeyword(String)}.
     */
    public static PersonSelector byPhone(String phoneKeyword) {
        requireNonNull(phoneKeyword);
        if (!isValidPhoneKeyword(phoneKeyword)) {
            throw new IllegalArgumentException("The phone number to look for must contain only digits.");
        }
        return new PersonSelector(null, null, normalizePhoneKeyword(phoneKeyword));
    }

    /**
     * Returns true if {@code test} can be used to look for clients by phone number, i.e. it contains at least one
     * digit, and otherwise only spaces, parentheses, hyphens and an optional leading {@code +}.
     */
    public static boolean isValidPhoneKeyword(String test) {
        requireNonNull(test);
        return normalizePhoneKeyword(test).matches(PHONE_KEYWORD_REGEX);
    }

    private static String normalizePhoneKeyword(String phoneKeyword) {
        return phoneKeyword.trim().replaceAll(PHONE_SEPARATORS, "");
    }

    /**
     * Returns true if {@code person} is chosen by this selector.
     */
    public boolean matches(Person person) {
        requireNonNull(person);
        if (id != null) {
            return person.getId().equals(id);
        }
        if (nameKeyword != null) {
            return person.getName().fullName.toLowerCase(Locale.ROOT)
                    .contains(nameKeyword.toLowerCase(Locale.ROOT));
        }
        return person.getPhone().value.contains(phoneKeyword);
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
        if (id != null) {
            return String.format(MESSAGE_NO_PERSON_WITH_ID, id);
        }
        if (nameKeyword != null) {
            return String.format(MESSAGE_NO_PERSON_WITH_NAME, nameKeyword);
        }
        return String.format(MESSAGE_NO_PERSON_WITH_PHONE, phoneKeyword);
    }

    /**
     * Returns the message to show when this selector chooses more than one client.
     *
     * @param matches The clients chosen by this selector. Must contain at least two clients.
     * @param retryExample A complete command that would act on the first of {@code matches} instead,
     *     shown as an example of choosing a client by ID.
     */
    public String getMultipleMatchesMessage(List<Person> matches, String retryExample) {
        requireNonNull(matches);
        requireNonNull(retryExample);
        assert matches.size() > 1 : "Only a name or phone number can match more than one client";
        assert id == null : "An ID matches at most one client";
        boolean isByName = nameKeyword != null;
        return String.format(MESSAGE_MULTIPLE_MATCHES, matches.size(), isByName ? "name" : "phone number",
                isByName ? nameKeyword : phoneKeyword, retryExample);
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
                && Objects.equals(nameKeyword, otherSelector.nameKeyword)
                && Objects.equals(phoneKeyword, otherSelector.phoneKeyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameKeyword, phoneKeyword);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("nameKeyword", nameKeyword)
                .add("phoneKeyword", phoneKeyword)
                .toString();
    }
}
