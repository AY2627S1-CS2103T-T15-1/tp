package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
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
            + "Example: " + COMMAND_WORD + " " + PREFIX_ADD + " " + PREFIX_ID + " 2 " + PREFIX_DATE + " 2026-10-26 "
            + PREFIX_TIME + " 09:00";

    public static final String MESSAGE_SUCCESS = "Scheduled 4 weekly appointments for client: %1$s\n"
            + "First appointment: %2$s";

    private static final Logger logger = LogsCenter.getLogger(AddAppointmentCommand.class);

    private final LocalDate firstDate;
    private final LocalTime time;

    /**
     * Creates a command that gives the chosen client weekly appointments at {@code time},
     * starting on {@code firstDate}. All arguments must not be null.
     */
    public AddAppointmentCommand(PersonSelector selector, LocalDate firstDate, LocalTime time) {
        super(selector);
        this.firstDate = requireNonNull(firstDate);
        this.time = requireNonNull(time);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
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
