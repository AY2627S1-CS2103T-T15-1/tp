package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CANCEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Cancels one appointment of a client, keeping it on record marked as cancelled.
 */
public class CancelAppointmentCommand extends AppointmentIndexCommand {

    public static final String MESSAGE_USAGE = AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_CANCEL
            + ": Cancels one of a client's appointments and keeps it on record.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then " + PREFIX_INDEX
            + " INDEX (as numbered on the client's page)\n"
            + "Example: " + AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_CANCEL + " " + PREFIX_ID + " 2 "
            + PREFIX_INDEX + " 1";

    public static final String MESSAGE_SUCCESS = "Cancelled appointment %1$d (%2$s) for client: %3$s";
    public static final String MESSAGE_ALREADY_CANCELLED = "Appointment %1$d is already cancelled.";
    public static final String MESSAGE_ALREADY_PASSED =
            "Appointment %1$d (%2$s) has already passed, so it cannot be cancelled.";

    private static final Logger logger = LogsCenter.getLogger(CancelAppointmentCommand.class);

    /**
     * Creates a command that cancels appointment {@code index} of the client chosen by {@code selector}.
     * Both arguments must not be null.
     */
    public CancelAppointmentCommand(PersonSelector selector, Index index) {
        this(selector, index, Clock.systemDefaultZone());
    }

    /**
     * Creates a command like {@link #CancelAppointmentCommand(PersonSelector, Index)},
     * using the supplied clock's local date at execution time as today.
     */
    public CancelAppointmentCommand(PersonSelector selector, Index index, Clock clock) {
        super(selector, index, clock);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        Appointment appointment = getTargetAppointment(target);
        if (appointment.isCancelled()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_CANCELLED, index.getOneBased()));
        }
        if (appointment.getDate().isBefore(getToday())) {
            throw new CommandException(String.format(MESSAGE_ALREADY_PASSED, index.getOneBased(),
                    appointment.getDisplayText()));
        }
        Person updated = withTargetAppointmentReplaced(target, appointment.cancel());
        model.setPerson(target, updated);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        logger.info("Cancelled appointment " + index.getOneBased() + " of client " + updated.getId());
        String message = String.format(MESSAGE_SUCCESS, index.getOneBased(), appointment.getDisplayText(),
                Messages.format(updated));
        return new CommandResult(message, updated);
    }

    @Override
    protected String getCommandWord() {
        return AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_CANCEL;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return getIndexArgument();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CancelAppointmentCommand otherCommand)) {
            return false;
        }
        return selector.equals(otherCommand.selector)
                && index.equals(otherCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("index", index)
                .toString();
    }
}
