package seedu.address.model.person;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.LongFormText;
import seedu.address.model.appointment.Appointment;
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
    private final LongFormText note;
    private final LongFormText workoutPlan;
    private final boolean isActive;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Appointment> appointments;

    /**
     * Creates an active person with no note and no tags.
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone) {
        this(id, name, phone, new LongFormText(""), true, Set.of());
    }

    /**
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone, LongFormText note, boolean isActive, Set<Tag> tags) {
        this(id, name, phone, note, isActive, tags, new LongFormText(""));
    }

    /**
     * Creates an active person with a workout plan, no note and no tags.
     */
    public Person(PersonId id, Name name, Phone phone, LongFormText workoutPlan) {
        this(id, name, phone, new LongFormText(""), true, Set.of(), workoutPlan);
    }

    /**
     * Creates a person with no appointments. Every argument must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone, LongFormText note, boolean isActive, Set<Tag> tags,
            LongFormText workoutPlan) {
        this(id, name, phone, note, isActive, tags, workoutPlan, List.of());
    }

    /**
     * Creates a person with all details. Every argument must be present and not null.
     * {@code appointments} may be in any order, and must hold at most {@value Appointment#SERIES_LENGTH}
     * appointments.
     */
    public Person(PersonId id, Name name, Phone phone, LongFormText note, boolean isActive, Set<Tag> tags,
            LongFormText workoutPlan, List<Appointment> appointments) {
        Objects.requireNonNull(appointments);
        assert appointments.size() <= Appointment.SERIES_LENGTH
                : "A client has at most " + Appointment.SERIES_LENGTH + " appointments";
        this.appointments = appointments.stream().sorted().toList();
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(note);
        Objects.requireNonNull(tags);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.workoutPlan = Objects.requireNonNull(workoutPlan);
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

    public LongFormText getNote() {
        return note;
    }

    public LongFormText getWorkoutPlan() {
        return workoutPlan;
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
    public Person withNote(LongFormText note) {
        return new Person(id, name, phone, note, isActive, tags, workoutPlan, appointments);
    }

    /**
     * Returns a copy of this person who is active if {@code isActive} is true, and inactive otherwise.
     */
    public Person withActive(boolean isActive) {
        return new Person(id, name, phone, note, isActive, tags, workoutPlan, appointments);
    }

    /**
     * Returns a copy of this person with {@code tags} as their tags.
     */
    public Person withTags(Set<Tag> tags) {
        return new Person(id, name, phone, note, isActive, tags, workoutPlan, appointments);
    }

    /**
     * Returns a copy of this person with the given workout plan, preserving all other details.
     */
    public Person withWorkoutPlan(LongFormText workoutPlan) {
        return new Person(id, name, phone, note, isActive, tags, workoutPlan, appointments);
    }

    /**
     * Returns the client's appointments, in order of date and time. The list cannot be modified.
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Returns a copy of this person with {@code appointments} as their appointments, preserving all other details.
     */
    public Person withAppointments(List<Appointment> appointments) {
        return new Person(id, name, phone, note, isActive, tags, workoutPlan, appointments);
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
                && tags.equals(otherPerson.tags)
                && workoutPlan.equals(otherPerson.workoutPlan)
                && appointments.equals(otherPerson.appointments);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, note, isActive, tags, workoutPlan, appointments);
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
                .add("workoutPlan", workoutPlan)
                .add("appointments", appointments)
                .toString();
    }

}
