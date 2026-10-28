package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONFIRM;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a clear command.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Accepts no arguments for the prompt, or exactly {@code --confirm} to clear all clients.
     *
     * @throws ParseException if any other arguments are supplied.
     */
    @Override
    public ClearCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ClearCommand(false);
        }
        if (trimmedArgs.equals(PREFIX_CONFIRM.getPrefix())) {
            return new ClearCommand(true);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
    }
}
