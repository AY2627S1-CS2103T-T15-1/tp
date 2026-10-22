package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TODAY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.time.LocalDate;

import seedu.address.model.Model;

/**
 * Lists all clients, or only clients with a non-cancelled appointment today.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all clients, or only clients with "
            + "a non-cancelled appointment today.\n"
            + "Parameters: [" + PREFIX_TODAY + "]\n"
            + "Examples: " + COMMAND_WORD + ", " + COMMAND_WORD + " " + PREFIX_TODAY;

    public static final String MESSAGE_SUCCESS_SINGULAR = "Listed 1 client";
    public static final String MESSAGE_SUCCESS_PLURAL = "Listed %1$d clients";
    public static final String MESSAGE_NO_CLIENTS = "No clients yet. Use `add` to add your first client.";
    public static final String MESSAGE_NO_CLIENTS_TODAY = "No clients have appointments today.";

    private final boolean todayOnly;
    private final Clock clock;

    /**
     * Creates a command that lists all clients.
     */
    public ListCommand() {
        this(false, Clock.systemDefaultZone());
    }

    private ListCommand(boolean todayOnly, Clock clock) {
        this.todayOnly = todayOnly;
        this.clock = requireNonNull(clock);
    }

    /**
     * Creates a command that lists clients with a non-cancelled appointment today,
     * using the supplied clock's local date at execution time.
     */
    public static ListCommand forToday(Clock clock) {
        return new ListCommand(true, clock);
    }

    /**
     * Replaces the current filter and reports how many clients are listed.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (todayOnly) {
            // Keep one date for the entire filter, even if execution crosses midnight.
            LocalDate today = LocalDate.now(clock);
            model.updateFilteredPersonList(person -> person.getAppointments().stream()
                    .anyMatch(appointment -> !appointment.isCancelled() && appointment.getDate().equals(today)));
        } else {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }
        int clientCount = model.getFilteredPersonList().size();
        return new CommandResult(getMessageForClientCount(clientCount));
    }

    /**
     * Returns the feedback message for the given number of listed clients.
     */
    private String getMessageForClientCount(int clientCount) {
        if (clientCount == 0) {
            return todayOnly ? MESSAGE_NO_CLIENTS_TODAY : MESSAGE_NO_CLIENTS;
        }
        String message = clientCount == 1 ? MESSAGE_SUCCESS_SINGULAR
                : String.format(MESSAGE_SUCCESS_PLURAL, clientCount);
        return todayOnly ? message + " with appointments today" : message;
    }
}
