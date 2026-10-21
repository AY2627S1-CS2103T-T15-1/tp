package seedu.address.model.person;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final PersonId id;
    private final Name name;
    private final Phone phone;

    // Data fields
    private final Note note;
    private final boolean isActive;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates an active person with no note and no tags.
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone) {
        this(id, name, phone, Note.EMPTY, true, Set.of());
    }

    /**
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone, Note note, boolean isActive, Set<Tag> tags) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(note);
        Objects.requireNonNull(tags);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.isActive = isActive;
        this.tags.addAll(tags);
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

    public Note getNote() {
        return note;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns a copy of this person with {@code note} as their note.
     */
    public Person withNote(Note note) {
        return new Person(id, name, phone, note, isActive, tags);
    }

    /**
     * Returns a copy of this person who is active if {@code isActive} is true, and inactive otherwise.
     */
    public Person withActive(boolean isActive) {
        return new Person(id, name, phone, note, isActive, tags);
    }

    /**
     * Returns a copy of this person with {@code tags} as their tags.
     */
    public Person withTags(Set<Tag> tags) {
        return new Person(id, name, phone, note, isActive, tags);
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
                && note.equals(otherPerson.note)
                && isActive == otherPerson.isActive
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, note, isActive, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("note", note)
                .add("isActive", isActive)
                .add("tags", tags)
                .toString();
    }

}
