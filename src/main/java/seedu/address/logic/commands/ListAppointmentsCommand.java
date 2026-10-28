package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Lists the appointments of all clients on one day, or in one Monday to Sunday week,
 * and shows only the clients who have one of those appointments.
 */
public class ListAppointmentsCommand extends Command {

    public static final String MESSAGE_USAGE = "appointment " + PREFIX_DATE + " DATE or appointment " + PREFIX_WEEK
            + " DATE: Lists appointments on a day, or in the Monday to Sunday week containing DATE.\n"
            + "Example: appointment " + PREFIX_WEEK + " 2026-10-22";

    public static final String MESSAGE_DAY_HEADER = "Appointments on %1$s:";
    public static final String MESSAGE_WEEK_HEADER = "Appointments from %1$s to %2$s:";
    public static final String MESSAGE_NO_DAY = "No appointments on %1$s.";
    public static final String MESSAGE_NO_WEEK = "No appointments from %1$s to %2$s.";
    public static final String MESSAGE_WEEK_NOT_SUPPORTED =
            "The week of %1$s goes past the dates Cindy supports, 0000-01-01 to 9999-12-31.";

    private static final Logger logger = LogsCenter.getLogger(ListAppointmentsCommand.class);

    private final LocalDate start;
    private final LocalDate end;
    private final boolean isWeek;

    private ListAppointmentsCommand(LocalDate start, LocalDate end, boolean isWeek) {
        assert !start.isAfter(end) : "The start of a period must not be after its end";
        this.start = start;
        this.end = end;
        this.isWeek = isWeek;
    }

    /**
     * Returns a command that lists the appointments on {@code day}.
     */
    public static ListAppointmentsCommand forDay(LocalDate day) {
        requireNonNull(day);
        return new ListAppointmentsCommand(day, day, false);
    }

    /**
     * Returns a command that lists the appointments in the Monday to Sunday week containing {@code anyDayInWeek}.
     */
    public static ListAppointmentsCommand forWeek(LocalDate anyDayInWeek) {
        requireNonNull(anyDayInWeek);
        LocalDate monday = getMonday(anyDayInWeek);
        return new ListAppointmentsCommand(monday, monday.plusDays(6), true);
    }

    /**
     * Returns true if every day of the Monday to Sunday week containing {@code anyDayInWeek} has a year
     * from 0000 to 9999, so that the week can be shown in the format YYYY-MM-DD.
     */
    public static boolean isSupportedWeek(LocalDate anyDayInWeek) {
        requireNonNull(anyDayInWeek);
        LocalDate monday = getMonday(anyDayInWeek);
        return monday.getYear() >= 0 && monday.plusDays(6).getYear() <= 9999;
    }

    private static LocalDate getMonday(LocalDate anyDayInWeek) {
        return anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Entry> entries = model.getAddressBook().getPersonList().stream()
                .flatMap(person -> person.getAppointments().stream()
                        .filter(appointment -> appointment.isWithin(start, end))
                        .map(appointment -> new Entry(appointment, person)))
                .sorted(Comparator.comparing(Entry::appointment)
                        .thenComparingInt(entry -> entry.person().getId().value))
                .toList();
        model.updateFilteredPersonList(this::hasAppointmentInPeriod);
        logger.info("Listed " + entries.size() + " appointment(s) from " + start + " to " + end);

        String startText = Appointment.formatDate(start);
        String endText = Appointment.formatDate(end);
        if (entries.isEmpty()) {
            return new CommandResult(isWeek
                    ? String.format(MESSAGE_NO_WEEK, startText, endText)
                    : String.format(MESSAGE_NO_DAY, startText));
        }
        String header = isWeek
                ? String.format(MESSAGE_WEEK_HEADER, startText, endText)
                : String.format(MESSAGE_DAY_HEADER, startText);
        String lines = entries.stream().map(this::formatEntry).collect(Collectors.joining("\n"));
        return new CommandResult(header + "\n" + lines);
    }

    private boolean hasAppointmentInPeriod(Person person) {
        return person.getAppointments().stream().anyMatch(appointment -> appointment.isWithin(start, end));
    }

    /**
     * Returns one line of the list, e.g. {@code 09:00 Alex Yeoh (ID: 1)} for a day, with the date in front for a week.
     */
    private String formatEntry(Entry entry) {
        Appointment appointment = entry.appointment();
        Person person = entry.person();
        String time = appointment.getTime().format(Appointment.TIME_FORMAT);
        String when = isWeek ? Appointment.formatDate(appointment.getDate()) + " " + time : time;
        return when + " " + person.getName().fullName + " (ID: " + person.getId() + ")"
                + (appointment.isCancelled() ? " (Cancelled)" : "");
    }

    /** One appointment in the list, with the client it belongs to. */
    private record Entry(Appointment appointment, Person person) {}

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListAppointmentsCommand otherCommand)) {
            return false;
        }
        return start.equals(otherCommand.start)
                && end.equals(otherCommand.end)
                && isWeek == otherCommand.isWeek;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("start", start)
                .add("end", end)
                .add("isWeek", isWeek)
                .toString();
    }
}
