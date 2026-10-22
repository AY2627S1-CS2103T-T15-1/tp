package seedu.address.model.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AppointmentTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 26);
    private static final LocalTime NINE = LocalTime.of(9, 0);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Appointment(null, NINE));
        assertThrows(NullPointerException.class, () -> new Appointment(MONDAY, null));
    }

    @Test
    public void weeklySeries_startDate_returnsFourWeeklyAppointments() {
        List<Appointment> series = Appointment.weeklySeries(MONDAY, NINE);
        assertEquals(List.of(new Appointment(MONDAY, NINE),
                new Appointment(LocalDate.of(2026, 11, 2), NINE),
                new Appointment(LocalDate.of(2026, 11, 9), NINE),
                new Appointment(LocalDate.of(2026, 11, 16), NINE)), series);
        assertEquals(Appointment.SERIES_LENGTH, series.size());
    }

    @Test
    public void weeklySeries_acrossYearEnd_rollsIntoNextYear() {
        List<Appointment> series = Appointment.weeklySeries(LocalDate.of(2026, 12, 28), NINE);
        assertEquals(LocalDate.of(2027, 1, 18), series.get(3).getDate());
    }

    @Test
    public void isValidDate() {
        assertTrue(Appointment.isValidDate("2026-10-22"));
        assertTrue(Appointment.isValidDate("2028-02-29"));
        assertFalse(Appointment.isValidDate("2026-02-30"));
        assertFalse(Appointment.isValidDate("2026-02-29"));
        assertFalse(Appointment.isValidDate("2026-13-01"));
        assertFalse(Appointment.isValidDate("22-10-2026"));
        assertFalse(Appointment.isValidDate("2026-1-5"));
        assertFalse(Appointment.isValidDate(""));
    }

    @Test
    public void isValidTime() {
        assertTrue(Appointment.isValidTime("09:00"));
        assertTrue(Appointment.isValidTime("00:00"));
        assertTrue(Appointment.isValidTime("23:59"));
        assertFalse(Appointment.isValidTime("9:00"));
        assertFalse(Appointment.isValidTime("24:00"));
        assertFalse(Appointment.isValidTime("09:60"));
        assertFalse(Appointment.isValidTime("0900"));
        assertFalse(Appointment.isValidTime(""));
    }

    @Test
    public void cancel_returnsCancelledCopy() {
        Appointment appointment = new Appointment(MONDAY, NINE);
        Appointment cancelled = appointment.cancel();
        assertTrue(cancelled.isCancelled());
        assertFalse(appointment.isCancelled());
        assertEquals(MONDAY, cancelled.getDate());
        assertEquals(NINE, cancelled.getTime());
    }

    @Test
    public void rescheduleTo_cancelled_clearsCancelled() {
        LocalDate tuesday = MONDAY.plusDays(1);
        LocalTime evening = LocalTime.of(18, 30);
        Appointment moved = new Appointment(MONDAY, NINE, true).rescheduleTo(tuesday, evening);
        assertEquals(new Appointment(tuesday, evening), moved);
    }

    @Test
    public void compareTo_ordersByDateThenTime() {
        Appointment mondayEarly = new Appointment(MONDAY, NINE);
        Appointment mondayLate = new Appointment(MONDAY, LocalTime.of(18, 0));
        Appointment tuesdayEarly = new Appointment(MONDAY.plusDays(1), LocalTime.of(7, 0));
        assertTrue(mondayEarly.compareTo(mondayLate) < 0);
        assertTrue(mondayLate.compareTo(tuesdayEarly) < 0);
        assertEquals(0, mondayEarly.compareTo(new Appointment(MONDAY, NINE)));
    }

    @Test
    public void isWithin_boundaries_inclusive() {
        Appointment appointment = new Appointment(MONDAY, NINE);
        assertTrue(appointment.isWithin(MONDAY, MONDAY));
        assertTrue(appointment.isWithin(MONDAY.minusDays(1), MONDAY));
        assertTrue(appointment.isWithin(MONDAY, MONDAY.plusDays(6)));
        assertFalse(appointment.isWithin(MONDAY.plusDays(1), MONDAY.plusDays(6)));
        assertFalse(appointment.isWithin(MONDAY.minusDays(7), MONDAY.minusDays(1)));
    }

    @Test
    public void formatDate_showsShortDayName() {
        assertEquals("Mon 2026-10-26", Appointment.formatDate(MONDAY));
        assertEquals("Thu 2026-10-22", Appointment.formatDate(LocalDate.of(2026, 10, 22)));
    }

    @Test
    public void getDisplayText_marksCancelled() {
        assertEquals("Mon 2026-10-26 09:00", new Appointment(MONDAY, NINE).getDisplayText());
        assertEquals("Mon 2026-10-26 09:00 (Cancelled)", new Appointment(MONDAY, NINE, true).getDisplayText());
        assertEquals("Mon 2026-10-26 09:00", new Appointment(MONDAY, NINE).toString());
    }

    @Test
    public void equals() {
        Appointment appointment = new Appointment(MONDAY, NINE);
        assertEquals(appointment, appointment);
        assertEquals(appointment, new Appointment(MONDAY, NINE, false));
        assertEquals(appointment.hashCode(), new Appointment(MONDAY, NINE).hashCode());
        assertNotEquals(appointment, null);
        assertNotEquals(appointment, "2026-10-26");
        assertNotEquals(appointment, appointment.cancel());
        assertNotEquals(appointment, new Appointment(MONDAY.plusDays(1), NINE));
        assertNotEquals(appointment, new Appointment(MONDAY, NINE.plusMinutes(1)));
    }
}
