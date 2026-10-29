package seedu.address.model.appointment;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents one appointment with a client, at a date and time. A cancelled appointment is kept on record.
 * Guarantees: immutable; date and time are not null.
 */
public final class Appointment implements Comparable<Appointment> {

    /** Number of weekly appointments created for a client in advance, and the most a client can have. */
    public static final int SERIES_LENGTH = 4;

    public static final String MESSAGE_DATE_CONSTRAINTS = "Dates should be in the format YYYY-MM-DD, e.g. 2026-10-22.";
    public static final String MESSAGE_TIME_CONSTRAINTS = "Times should be in the 24-hour format HH:MM, e.g. 09:30.";
    public static final String MESSAGE_NOT_REAL_DATE = "%1$s is not a real date.";
    public static final String MESSAGE_NOT_REAL_TIME = "%1$s is not a real time. Use 00:00 to 23:59.";

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final Comparator<Appointment> ORDER = Comparator.comparing(Appointment::getDate)
            .thenComparing(Appointment::getTime);

    private final LocalDate date;
    private final LocalTime time;
    private final boolean isCancelled;

    /**
     * Creates an appointment that is not cancelled.
     */
    public Appointment(LocalDate date, LocalTime time) {
        this(date, time, false);
    }

    /**
     * Creates an appointment, which is cancelled if {@code isCancelled} is true.
     */
    public Appointment(LocalDate date, LocalTime time, boolean isCancelled) {
        this.date = requireNonNull(date);
        this.time = requireNonNull(time);
        this.isCancelled = isCancelled;
    }

    /**
     * Returns {@value #SERIES_LENGTH} appointments, one week apart, starting on {@code firstDate} at {@code time}.
     */
    public static List<Appointment> weeklySeries(LocalDate firstDate, LocalTime time) {
        requireNonNull(firstDate);
        requireNonNull(time);
        List<Appointment> series = new ArrayList<>();
        for (int week = 0; week < SERIES_LENGTH; week++) {
            series.add(new Appointment(firstDate.plusWeeks(week), time));
        }
        return List.copyOf(series);
    }

    /**
     * Returns true if {@code test} is a real calendar date in the format YYYY-MM-DD.
     */
    public static boolean isValidDate(String test) {
        requireNonNull(test);
        try {
            LocalDate.parse(test, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if {@code test} is a time in the 24-hour format HH:MM.
     */
    public static boolean isValidTime(String test) {
        requireNonNull(test);
        try {
            LocalTime.parse(test, TIME_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns {@code date} as shown to the user, e.g. {@code Thu 2026-10-22}.
     */
    public static String formatDate(LocalDate date) {
        requireNonNull(date);
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + date.format(DATE_FORMAT);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Returns a cancelled copy of this appointment.
     */
    public Appointment cancel() {
        return new Appointment(date, time, true);
    }

    /**
     * Returns a copy of this appointment moved to {@code newDate} at {@code newTime}, which is not cancelled.
     */
    public Appointment rescheduleTo(LocalDate newDate, LocalTime newTime) {
        return new Appointment(newDate, newTime);
    }

    /**
     * Returns true if this appointment is on a day from {@code start} to {@code end}, both inclusive.
     */
    public boolean isWithin(LocalDate start, LocalDate end) {
        requireNonNull(start);
        requireNonNull(end);
        assert !start.isAfter(end) : "The start of a period must not be after its end";
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Returns the date, the time, and whether this appointment is cancelled, as shown to the user,
     * e.g. {@code Mon 2026-10-26 09:00 (Cancelled)}.
     */
    public String getDisplayText() {
        return formatDate(date) + " " + time.format(TIME_FORMAT) + (isCancelled ? " (Cancelled)" : "");
    }

    /**
     * Orders appointments by date, then by time.
     */
    @Override
    public int compareTo(Appointment other) {
        return ORDER.compare(this, other);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Appointment otherAppointment)) {
            return false;
        }
        return date.equals(otherAppointment.date)
                && time.equals(otherAppointment.time)
                && isCancelled == otherAppointment.isCancelled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, isCancelled);
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}
