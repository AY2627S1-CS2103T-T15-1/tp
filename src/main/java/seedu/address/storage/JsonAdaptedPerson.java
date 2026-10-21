package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link Person}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final Integer id;
    private final String name;
    private final String phone;
    private final String workoutPlan;

    /**
     * Constructs an adapted person without a workout plan.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone) {
        this(id, name, phone, "");
    }

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * {@code id} can be null for data files saved before clients had IDs.
     * {@code workoutPlan} can be null for clients without a plan, including older data files.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") Integer id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("workoutPlan") String workoutPlan) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.workoutPlan = workoutPlan;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        id = source.getId().value;
        name = source.getName().fullName;
        phone = source.getPhone().value;
        workoutPlan = source.getWorkoutPlan();
    }

    /**
     * Returns true if this person was saved with an ID.
     */
    boolean hasId() {
        return id != null;
    }

    /**
     * Returns the saved ID of this person.
     * Should only be called if {@link #hasId()} is true.
     */
    int getId() {
        assert hasId();
        return id;
    }

    /**
     * Returns a copy of this person that has {@code newId} as its ID.
     */
    JsonAdaptedPerson withId(int newId) {
        return new JsonAdaptedPerson(newId, name, phone, workoutPlan);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "ID"));
        }
        if (!PersonId.isValidId(id)) {
            throw new IllegalValueException(PersonId.MESSAGE_CONSTRAINTS);
        }
        final PersonId modelId = new PersonId(id);

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        return new Person(modelId, modelName, modelPhone, workoutPlan);
    }

}
