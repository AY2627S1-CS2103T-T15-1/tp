package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TODAY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Lists all clients, or only clients with a non-cancelled appointment today, ordered by their earliest
 * appointment time today.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all clients, or only clients with "
            + "a non-cancelled appointment today, with their appointment times, earliest first.\n"
            + "Parameters: [" + PREFIX_TODAY + "]\n"
            + "Examples: " + COMMAND_WORD + ", " + COMMAND_WORD + " " + PREFIX_TODAY;

    public static final String MESSAGE_SUCCESS_SINGULAR = "Listed 1 client";
    public static final String MESSAGE_SUCCESS_PLURAL = "Listed %1$d clients";
    public static final String MESSAGE_NO_CLIENTS = "No clients yet. Use add to add your first client.";
    public static final String MESSAGE_NO_CLIENTS_TODAY = "No clients have appointments today.";
    public static final String MESSAGE_TODAY_SUFFIX = " with appointments today:\n";

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
            return executeToday(model);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        int clientCount = model.getFilteredPersonList().size();
        if (clientCount == 0) {
            return new CommandResult(MESSAGE_NO_CLIENTS);
        }
        return new CommandResult(getMessageForClientCount(clientCount));
    }

    /**
     * Shows only clients with a non-cancelled appointment today, ordered by their earliest appointment time today,
     * and reports each client's appointment times today.
     */
    private CommandResult executeToday(Model model) {
        // Keep one date for the entire filter, even if execution crosses midnight.
        LocalDate today = LocalDate.now(clock);
        model.updateFilteredPersonList(person -> !getTimesOn(person, today).isEmpty(),
                Comparator.comparing((Person person) -> getEarliestTimeOn(person, today))
                        .thenComparing(Person::getName));

        List<Person> clients = model.getFilteredPersonList();
        if (clients.isEmpty()) {
            return new CommandResult(MESSAGE_NO_CLIENTS_TODAY);
        }
        String schedule = clients.stream()
                .map(person -> formatScheduleLine(person, today))
                .collect(Collectors.joining("\n"));
        return new CommandResult(getMessageForClientCount(clients.size()) + MESSAGE_TODAY_SUFFIX + schedule);
    }

    /**
     * Returns the times of {@code person}'s non-cancelled appointments on {@code date}, earliest first.
     */
    private static List<LocalTime> getTimesOn(Person person, LocalDate date) {
        return person.getAppointments().stream()
                .filter(appointment -> !appointment.isCancelled() && appointment.getDate().equals(date))
                .map(Appointment::getTime)
                .sorted()
                .toList();
    }

    /**
     * Returns the time of {@code person}'s earliest non-cancelled appointment on {@code date},
     * or the end of the day if they have none.
     */
    private static LocalTime getEarliestTimeOn(Person person, LocalDate date) {
        return getTimesOn(person, date).stream().findFirst().orElse(LocalTime.MAX);
    }

    /**
     * Returns a line such as {@code 07:00, 18:00 Alex Tanner (ID: 1)} for {@code person}'s appointments on
     * {@code date}.
     */
    private static String formatScheduleLine(Person person, LocalDate date) {
        String times = getTimesOn(person, date).stream()
                .map(Appointment.TIME_FORMAT::format)
                .collect(Collectors.joining(", "));
        return times + " " + person.getName() + " (ID: " + person.getId() + ")";
    }

    /**
     * Returns the feedback message for the given positive number of listed clients.
     */
    private static String getMessageForClientCount(int clientCount) {
        assert clientCount > 0 : "Empty lists have their own messages";
        return clientCount == 1 ? MESSAGE_SUCCESS_SINGULAR : String.format(MESSAGE_SUCCESS_PLURAL, clientCount);
    }
}
