package seedu.address.storage;

import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.appointment.Appointment;

/**
 * Jackson-friendly version of {@link Appointment}.
 */
class JsonAdaptedAppointment {

    private final String date;
    private final String time;
    private final Boolean cancelled;

    /**
     * Constructs a {@code JsonAdaptedAppointment} with the given details.
     * {@code cancelled} can be null, which means the appointment is not cancelled.
     */
    @JsonCreator
    public JsonAdaptedAppointment(@JsonProperty("date") String date, @JsonProperty("time") String time,
            @JsonProperty("cancelled") Boolean cancelled) {
        this.date = date;
        this.time = time;
        this.cancelled = cancelled;
    }

    /**
     * Converts a given {@code Appointment} into this class for Jackson use.
     */
    public JsonAdaptedAppointment(Appointment source) {
        date = source.getDate().format(Appointment.DATE_FORMAT);
        time = source.getTime().format(Appointment.TIME_FORMAT);
        cancelled = source.isCancelled();
    }

    /**
     * Converts this Jackson-friendly adapted appointment into the model's {@code Appointment} object.
     *
     * @throws IllegalValueException if the date or time is missing or invalid.
     */
    public Appointment toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "appointment date"));
        }
        if (!Appointment.isValidDate(date)) {
            throw new IllegalValueException(Appointment.MESSAGE_DATE_CONSTRAINTS);
        }
        if (time == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "appointment time"));
        }
        if (!Appointment.isValidTime(time)) {
            throw new IllegalValueException(Appointment.MESSAGE_TIME_CONSTRAINTS);
        }
        return new Appointment(LocalDate.parse(date, Appointment.DATE_FORMAT),
                LocalTime.parse(time, Appointment.TIME_FORMAT), cancelled != null && cancelled);
    }
}
