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
import seedu.address.model.LongFormText;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.session.Session;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String TOO_MANY_APPOINTMENTS_MESSAGE = "A client can have at most "
            + Appointment.SERIES_LENGTH + " appointments.";

    private final List<JsonAdaptedSession> sessions = new ArrayList<>();
    private final Integer id;
    private final String name;
    private final String phone;
    private final String note;
    private final String workoutPlan;
    private final Boolean active;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedAppointment> appointments = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * {@code id}, {@code note}, {@code active}, {@code tags}, {@code workoutPlan}, {@code sessions} and
     * {@code appointments} can be null for data files saved before clients had these details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") Integer id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("note") String note,
            @JsonProperty("active") Boolean active, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("workoutPlan") String workoutPlan,
            @JsonProperty("sessions") List<JsonAdaptedSession> sessions,
            @JsonProperty("appointments") List<JsonAdaptedAppointment> appointments) {
        if (sessions != null) {
            this.sessions.addAll(sessions);
        }
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.workoutPlan = workoutPlan;
        this.active = active;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (appointments != null) {
            this.appointments.addAll(appointments);
        }
    }

    /**
     * Constructs an adapted person with sessions and no saved appointments.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone, String note, Boolean active,
            List<JsonAdaptedTag> tags, String workoutPlan, List<JsonAdaptedSession> sessions) {
        this(id, name, phone, note, active, tags, workoutPlan, sessions, null);
    }

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given ID, name and phone number,
     * and no other details.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone) {
        this(id, name, phone, null, null, null);
    }

    /**
     * Constructs an adapted person whose workout plan was not saved.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone, String note, Boolean active,
            List<JsonAdaptedTag> tags) {
        this(id, name, phone, note, active, tags, null, null);
    }

    /**
     * Constructs an adapted person with a workout plan and default values for other optional details.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone, String workoutPlan) {
        this(id, name, phone, null, null, null, workoutPlan, null);
    }

    /**
     * Constructs an adapted person with no saved sessions.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone, String note, Boolean active,
            List<JsonAdaptedTag> tags, String workoutPlan) {
        this(id, name, phone, note, active, tags, workoutPlan, null);
    }

    /**
     * Constructs an adapted person with sessions and default values for other optional details.
     */
    public JsonAdaptedPerson(Integer id, String name, String phone, List<JsonAdaptedSession> sessions) {
        this(id, name, phone, null, null, null, null, sessions);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        source.getSessions().stream().map(JsonAdaptedSession::new).forEach(sessions::add);
        id = source.getId().value;
        name = source.getName().fullName;
        phone = source.getPhone().value;
        note = source.getNote().value;
        workoutPlan = source.getWorkoutPlan().value;
        active = source.isActive();
        source.getTags().stream().map(JsonAdaptedTag::new).forEach(tags::add);
        source.getAppointments().stream().map(JsonAdaptedAppointment::new).forEach(appointments::add);
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
        return new JsonAdaptedPerson(newId, name, phone, note, active, tags, workoutPlan, sessions, appointments);
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
        if (!PersonId.isValidClientId(id)) {
            throw new IllegalValueException(PersonId.MESSAGE_CLIENT_ID_CONSTRAINTS);
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

        // Missing or null text fields in legacy files represent absent text.
        final LongFormText modelNote = new LongFormText(note == null ? "" : note);
        final LongFormText modelWorkoutPlan = new LongFormText(workoutPlan == null ? "" : workoutPlan);

        final boolean modelIsActive = active == null || active;

        final Set<Tag> modelTags = new HashSet<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Tag.class.getSimpleName()));
            }
            modelTags.add(tag.toModelType());
        }

        if (appointments.size() > Appointment.SERIES_LENGTH) {
            throw new IllegalValueException(TOO_MANY_APPOINTMENTS_MESSAGE);
        }
        final List<Appointment> modelAppointments = new ArrayList<>();
        for (JsonAdaptedAppointment appointment : appointments) {
            if (appointment == null) {
                throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "Appointment"));
            }
            modelAppointments.add(appointment.toModelType());
        }

        return new Person(modelId, modelName, modelPhone, modelNote, modelIsActive, modelTags,
                modelWorkoutPlan, modelSessions, modelAppointments);
    }

}
