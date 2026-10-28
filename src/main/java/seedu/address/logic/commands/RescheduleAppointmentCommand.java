package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RESCHEDULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Moves one appointment of a client to a new date and time. A cancelled appointment becomes active again.
 */
public class RescheduleAppointmentCommand extends AppointmentIndexCommand {

    public static final String MESSAGE_USAGE = AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_RESCHEDULE
            + ": Moves one of a client's appointments to a new date and time.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then " + PREFIX_INDEX + " INDEX "
            + PREFIX_DATE + " DATE " + PREFIX_TIME + " TIME\n"
            + "Example: " + AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_RESCHEDULE + " " + PREFIX_ID + " 2 "
            + PREFIX_INDEX + " 1 " + PREFIX_DATE + " 2026-12-08 " + PREFIX_TIME + " 18:00";

    public static final String MESSAGE_SUCCESS = "Rescheduled appointment %1$d for client: %2$s\nNew time: %3$s";
    public static final String MESSAGE_CLASH = "This client already has an appointment on %1$s.";

    private static final Logger logger = LogsCenter.getLogger(RescheduleAppointmentCommand.class);

    private final LocalDate date;
    private final LocalTime time;

    /**
     * Creates a command that moves appointment {@code index} of the client chosen by {@code selector}
     * to {@code date} at {@code time}. No argument may be null.
     */
    public RescheduleAppointmentCommand(PersonSelector selector, Index index, LocalDate date, LocalTime time) {
        this(selector, index, date, time, Clock.systemDefaultZone());
    }

    /**
     * Creates a command like {@link #RescheduleAppointmentCommand(PersonSelector, Index, LocalDate, LocalTime)},
     * using the supplied clock's local date at execution time as today.
     */
    public RescheduleAppointmentCommand(PersonSelector selector, Index index, LocalDate date, LocalTime time,
            Clock clock) {
        super(selector, index, clock);
        this.date = requireNonNull(date);
        this.time = requireNonNull(time);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        getTargetAppointment(target); // checks that the index is valid for this client
        AddAppointmentCommand.checkSchedulableDate(date, getToday());
        Appointment moved = new Appointment(date, time);
        if (clashesWithOtherAppointment(target.getAppointments(), moved)) {
            throw new CommandException(String.format(MESSAGE_CLASH, moved.getDisplayText()));
        }
        Person updated = withTargetAppointmentReplaced(target, moved);
        model.setPerson(target, updated);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        logger.info("Rescheduled appointment " + index.getOneBased() + " of client " + updated.getId()
                + " to " + moved.getDisplayText());
        String message = String.format(MESSAGE_SUCCESS, index.getOneBased(), Messages.format(updated),
                moved.getDisplayText());
        return new CommandResult(message, updated);
    }

    /**
     * Returns true if an appointment other than the one being moved, cancelled or not, is at the time of
     * {@code moved}.
     */
    private boolean clashesWithOtherAppointment(List<Appointment> appointments, Appointment moved) {
        return IntStream.range(0, appointments.size())
                .filter(i -> i != index.getZeroBased())
                .mapToObj(appointments::get)
                .anyMatch(other -> other.getDate().equals(moved.getDate()) && other.getTime().equals(moved.getTime()));
    }

    @Override
    protected String getCommandWord() {
        return AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_RESCHEDULE;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return getIndexArgument()
                + " " + PREFIX_DATE + " " + date.format(Appointment.DATE_FORMAT)
                + " " + PREFIX_TIME + " " + time.format(Appointment.TIME_FORMAT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RescheduleAppointmentCommand otherCommand)) {
            return false;
        }
        return selector.equals(otherCommand.selector)
                && index.equals(otherCommand.index)
                && date.equals(otherCommand.date)
                && time.equals(otherCommand.time);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("index", index)
                .add("date", date)
                .add("time", time)
                .toString();
    }
}
