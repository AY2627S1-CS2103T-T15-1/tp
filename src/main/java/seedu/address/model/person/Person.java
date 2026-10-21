package seedu.address.model.person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.session.Session;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    public static final String MESSAGE_DUPLICATE_SESSION_DATE =
            "A client cannot have more than one session on the same date.";

    // Identity fields
    private final PersonId id;
    private final Name name;
    private final Phone phone;
    private final List<Session> sessions = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone) {
        this(id, name, phone, List.of());
    }

    /**
     * Every field must be present and not null. Session dates must be unique for this client.
     */
    public Person(PersonId id, Name name, Phone phone, List<Session> sessions) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(sessions);
        sessions.forEach(Objects::requireNonNull);
        if (hasDuplicateSessionDates(sessions)) {
            throw new IllegalArgumentException(MESSAGE_DUPLICATE_SESSION_DATE);
        }
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.sessions.addAll(sessions);
    }

    public PersonId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    /**
     * Returns an immutable view of this client's sessions.
     */
    public List<Session> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    /**
     * Returns a copy of this person with the given sessions.
     */
    public Person withSessions(List<Session> sessions) {
        return new Person(id, name, phone, sessions);
    }

    private static boolean hasDuplicateSessionDates(List<Session> sessions) {
        Set<LocalDate> dates = new HashSet<>();
        return sessions.stream().map(Session::getDate).anyMatch(date -> !dates.add(date));
    }

    /**
     * Returns true if both persons have the same name and phone number.
     * Names are compared case-insensitively and phone numbers are compared after
     * formatting separators have been removed by their respective value objects.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName())
                && otherPerson.getPhone().equals(getPhone());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return id.equals(otherPerson.id)
                && name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && sessions.equals(otherPerson.sessions);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, sessions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("sessions", sessions)
                .toString();
    }

}
