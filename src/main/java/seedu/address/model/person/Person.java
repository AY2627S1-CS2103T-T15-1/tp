package seedu.address.model.person;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.LongFormText;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final PersonId id;
    private final Name name;
    private final Phone phone;

    // Optional data field; blank text means no workout plan.
    private final LongFormText workoutPlan;

    /**
     * Creates a person without a workout plan. Identity fields must not be null.
     */
    public Person(PersonId id, Name name, Phone phone) {
        this(id, name, phone, new LongFormText(""));
    }

    /**
     * Creates a person with an optional workout plan.
     * All arguments must not be null. Use blank text for an absent plan.
     */
    public Person(PersonId id, Name name, Phone phone, LongFormText workoutPlan) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(phone);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.workoutPlan = Objects.requireNonNull(workoutPlan);
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

    public LongFormText getWorkoutPlan() {
        return workoutPlan;
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
                && workoutPlan.equals(otherPerson.workoutPlan);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, workoutPlan);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("workoutPlan", workoutPlan)
                .toString();
    }

}
