package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import seedu.address.logic.commands.SetActiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SetActiveCommand object
 * for either the {@code active} or the {@code inactive} command.
 */
public class SetActiveCommandParser implements Parser<SetActiveCommand> {

    private final boolean isActive;

    /**
     * Creates a parser for the {@code active} command if {@code isActive} is true,
     * and for the {@code inactive} command otherwise.
     */
    public SetActiveCommandParser(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Parses the given {@code String} of arguments in the context of the SetActiveCommand
     * and returns a SetActiveCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public SetActiveCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_PHONE_LONG);
        String messageUsage = isActive
                ? SetActiveCommand.MESSAGE_USAGE_ACTIVE
                : SetActiveCommand.MESSAGE_USAGE_INACTIVE;
        return new SetActiveCommand(ParserUtil.parsePersonSelector(argMultimap, messageUsage,
                PREFIX_ID, PREFIX_NAME_LONG, PREFIX_PHONE_LONG), isActive);
    }

}
