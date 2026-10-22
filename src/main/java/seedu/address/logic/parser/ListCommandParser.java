package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TODAY;

import java.time.Clock;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a list command.
 */
public class ListCommandParser implements Parser<ListCommand> {

    private final Clock clock;

    /**
     * Creates a parser using the computer's local time zone.
     */
    public ListCommandParser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser using the supplied clock to resolve today at command execution time.
     */
    ListCommandParser(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    /**
     * Accepts no arguments to list all clients, or exactly {@code --today} to filter by appointments today.
     *
     * @throws ParseException if any other arguments are supplied.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = requireNonNull(args).trim();
        if (trimmedArgs.isEmpty()) {
            return new ListCommand();
        }
        if (trimmedArgs.equals(PREFIX_TODAY.getPrefix())) {
            return ListCommand.forToday(clock);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }
}
