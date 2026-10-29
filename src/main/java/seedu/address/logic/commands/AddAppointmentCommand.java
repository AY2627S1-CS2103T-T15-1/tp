package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Schedules weekly appointments for a client chosen by ID or by name, replacing their current appointments.
 */
public class AddAppointmentCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "appointment";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + PREFIX_ADD
            + ": Schedules 4 weekly appointments for a client, replacing their current appointments.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then " + PREFIX_DATE + " DATE "
            + PREFIX_TIME + " TIME\n"
            + "DATE is YYYY-MM-DD and TIME is HH:MM (24-hour).\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ADD + " " + PREFIX_ID + " 2 " + PREFIX_DATE + " 2026-12-07 "
            + PREFIX_TIME + " 09:00";

    public static final String MESSAGE_SUCCESS = "Scheduled 4 weekly appointments for client: %1$s\n"
            + "First appointment: %2$s";
    public static final String MESSAGE_DATE_IN_PAST = "%1$s is in the past. Choose a date from today (%2$s) onwards.";
    public static final String MESSAGE_DATE_TOO_FAR = "%1$s is more than a year ahead. Choose a date up to %2$s.";

    private static final Logger logger = LogsCenter.getLogger(AddAppointmentCommand.class);

    private final LocalDate firstDate;
    private final LocalTime time;
    private final Clock clock;

    /**
     * Creates a command that gives the chosen client weekly appointments at {@code time},
     * starting on {@code firstDate}. All arguments must not be null.
     */
    public AddAppointmentCommand(PersonSelector selector, LocalDate firstDate, LocalTime time) {
        this(selector, firstDate, time, Clock.systemDefaultZone());
    }

    /**
     * Creates a command like {@link #AddAppointmentCommand(PersonSelector, LocalDate, LocalTime)},
     * using the supplied clock's local date at execution time as today.
     */
    public AddAppointmentCommand(PersonSelector selector, LocalDate firstDate, LocalTime time, Clock clock) {
        super(selector);
        this.firstDate = requireNonNull(firstDate);
        this.time = requireNonNull(time);
        this.clock = requireNonNull(clock);
    }

    /**
     * Checks that an appointment can be scheduled on {@code date}, given that it is {@code today}.
     *
     * @throws CommandException if {@code date} is before {@code today}, or more than a year after it.
     */
    static void checkSchedulableDate(LocalDate date, LocalDate today) throws CommandException {
        if (date.isBefore(today)) {
            throw new CommandException(String.format(MESSAGE_DATE_IN_PAST,
                    date.format(Appointment.DATE_FORMAT), today.format(Appointment.DATE_FORMAT)));
        }
        LocalDate lastDate = today.plusYears(1);
        if (date.isAfter(lastDate)) {
            throw new CommandException(String.format(MESSAGE_DATE_TOO_FAR,
                    date.format(Appointment.DATE_FORMAT), lastDate.format(Appointment.DATE_FORMAT)));
        }
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        checkSchedulableDate(firstDate, LocalDate.now(clock));
        List<Appointment> series = Appointment.weeklySeries(firstDate, time);
        assert series.size() == Appointment.SERIES_LENGTH : "A weekly series has a fixed number of appointments";
        Person updated = target.withAppointments(series);
        model.setPerson(target, updated);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        logger.info("Scheduled weekly appointments for client " + updated.getId() + " from " + firstDate);
        String message = String.format(MESSAGE_SUCCESS, Messages.format(updated), series.get(0).getDisplayText());
        return new CommandResult(message, updated);
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD + " " + PREFIX_ADD;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_DATE + " " + firstDate.format(Appointment.DATE_FORMAT)
                + " " + PREFIX_TIME + " " + time.format(Appointment.TIME_FORMAT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddAppointmentCommand otherCommand)) {
            return false;
        }
        return selector.equals(otherCommand.selector)
                && firstDate.equals(otherCommand.firstDate)
                && time.equals(otherCommand.time);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("firstDate", firstDate)
                .add("time", time)
                .toString();
    }
}
