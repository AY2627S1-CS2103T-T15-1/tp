package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.model.LongFormText;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.session.Session;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    private List<Session> sessions = new ArrayList<>();
    private PersonId id;
    private Name name;
    private Phone phone;
    private LongFormText note;
    private LongFormText workoutPlan = new LongFormText("");
    private boolean isActive;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        id = new PersonId(DEFAULT_ID);
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        note = new LongFormText("");
        isActive = true;
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        sessions = new ArrayList<>(personToCopy.getSessions());
        id = personToCopy.getId();
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        note = personToCopy.getNote();
        workoutPlan = personToCopy.getWorkoutPlan();
        isActive = personToCopy.isActive();
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code PersonId} of the {@code Person} that we are building.
     */
    public PersonBuilder withId(int id) {
        this.id = new PersonId(id);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the long-form note of the person being built.
     */
    public PersonBuilder withNote(String note) {
        this.note = new LongFormText(note);
        return this;
    }

    /**
     * Sets whether the {@code Person} that we are building is active.
     */
    public PersonBuilder withActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    /**
     * Sets the tags of the {@code Person} that we are building to tags with the given {@code tagNames}.
     */
    public PersonBuilder withTags(String... tagNames) {
        tags = new HashSet<>();
        for (String tagName : tagNames) {
            tags.add(new Tag(tagName));
        }
        return this;
    }

    /**
     * Sets the workout plan of the person being built.
     */
    public PersonBuilder withWorkoutPlan(String workoutPlan) {
        this.workoutPlan = new LongFormText(workoutPlan);
        return this;
    }

    /**
     * Sets the sessions of the person being built.
     */
    public PersonBuilder withSessions(Session... sessions) {
        this.sessions = new ArrayList<>(Arrays.asList(sessions));
        return this;
    }

    public Person build() {
        return new Person(id, name, phone, note, isActive, tags, workoutPlan, sessions);
    }

}
