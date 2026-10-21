package seedu.address.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.session.Session;

/**
 * Jackson-friendly version of {@link Person}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final Integer id;
    private final String name;
    private final String phone;
    private final List<JsonAdaptedSession> sessions = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * {@code id} and {@code sessions} can be null for older data files.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") Integer id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone,
            @JsonProperty("sessions") List<JsonAdaptedSession> sessions) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        if (sessions != null) {
            this.sessions.addAll(sessions);
        }
    }

    /**
     * Constructs an adapted person without saved sessions.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone) {
        this(id, name, phone, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        id = source.getId().value;
        name = source.getName().fullName;
        phone = source.getPhone().value;
        source.getSessions().stream().map(JsonAdaptedSession::new).forEach(sessions::add);
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
        return new JsonAdaptedPerson(newId, name, phone, sessions);
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

        final List<Session> modelSessions = new ArrayList<>();
        final Set<LocalDate> sessionDates = new HashSet<>();
        for (JsonAdaptedSession session : sessions) {
            if (session == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Session.class.getSimpleName()));
            }
            Session modelSession = session.toModelType();
            if (!sessionDates.add(modelSession.getDate())) {
                throw new IllegalValueException(Person.MESSAGE_DUPLICATE_SESSION_DATE);
            }
            modelSessions.add(modelSession);
        }

        return new Person(modelId, modelName, modelPhone, modelSessions);
    }

}
