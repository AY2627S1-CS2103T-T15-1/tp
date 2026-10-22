package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.appointment.Appointment;

public class JsonAdaptedAppointmentTest {

    private static final Appointment CANCELLED = new Appointment(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0), true);

    @Test
    public void toModelType_fromModel_returnsSameAppointment() throws Exception {
        assertEquals(CANCELLED, new JsonAdaptedAppointment(CANCELLED).toModelType());
    }

    @Test
    public void toModelType_savedJson_returnsAppointment() throws Exception {
        String json = "{\"date\": \"2026-10-26\", \"time\": \"09:00\", \"cancelled\": true}";
        assertEquals(CANCELLED, JsonUtil.fromJsonString(json, JsonAdaptedAppointment.class).toModelType());
    }

    @Test
    public void toModelType_missingCancelled_notCancelled() throws Exception {
        JsonAdaptedAppointment adapted = new JsonAdaptedAppointment("2026-10-26", "09:00", null);
        assertEquals(new Appointment(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0)), adapted.toModelType());
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedAppointment adapted = new JsonAdaptedAppointment("2026-02-30", "09:00", false);
        assertThrows(IllegalValueException.class, Appointment.MESSAGE_DATE_CONSTRAINTS, adapted::toModelType);
    }

    @Test
    public void toModelType_invalidTime_throwsIllegalValueException() {
        JsonAdaptedAppointment adapted = new JsonAdaptedAppointment("2026-10-26", "9am", false);
        assertThrows(IllegalValueException.class, Appointment.MESSAGE_TIME_CONSTRAINTS, adapted::toModelType);
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedAppointment adapted = new JsonAdaptedAppointment(null, "09:00", false);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "appointment date"),
                adapted::toModelType);
    }

    @Test
    public void toModelType_nullTime_throwsIllegalValueException() {
        JsonAdaptedAppointment adapted = new JsonAdaptedAppointment("2026-10-26", null, false);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "appointment time"),
                adapted::toModelType);
    }
}
