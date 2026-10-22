package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Represents a command that acts on one appointment of a client, chosen by its number in the client's list.
 */
public abstract class AppointmentIndexCommand extends PersonTargetedCommand {

    public static final String MESSAGE_NO_APPOINTMENTS =
            "This client has no appointments. Schedule them with appointment --add.";
    public static final String MESSAGE_INDEX_TOO_LARGE =
            "This client has %1$d appointment(s). Choose an index from 1 to %1$d.";

    protected final Index index;

    /**
     * Creates a command that acts on appointment {@code index} of the client chosen by {@code selector}.
     */
    protected AppointmentIndexCommand(PersonSelector selector, Index index) {
        super(selector);
        this.index = requireNonNull(index);
    }

    /**
     * Returns the appointment of {@code target} chosen by the index.
     *
     * @throws CommandException if the client has no appointments, or fewer than the index.
     */
    protected Appointment getTargetAppointment(Person target) throws CommandException {
        List<Appointment> appointments = target.getAppointments();
        if (appointments.isEmpty()) {
            throw new CommandException(MESSAGE_NO_APPOINTMENTS);
        }
        if (index.getZeroBased() >= appointments.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_TOO_LARGE, appointments.size()));
        }
        return appointments.get(index.getZeroBased());
    }

    /**
     * Returns a copy of {@code target} whose appointment chosen by the index is replaced by {@code replacement}.
     * The index must be valid for {@code target}.
     */
    protected Person withTargetAppointmentReplaced(Person target, Appointment replacement) {
        requireNonNull(replacement);
        List<Appointment> appointments = new ArrayList<>(target.getAppointments());
        assert index.getZeroBased() < appointments.size() : "The index must be checked before replacing";
        appointments.set(index.getZeroBased(), replacement);
        return target.withAppointments(appointments);
    }

    /**
     * Returns the index argument as the user would type it, starting with a space.
     */
    protected String getIndexArgument() {
        return " " + PREFIX_INDEX + " " + index.getOneBased();
    }
}
