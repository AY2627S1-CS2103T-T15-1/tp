package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PLAN;

import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.PlanCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for setting, replacing, or clearing a client's workout plan.
 */
public class PlanCommandParser implements Parser<PlanCommand> {

    /**
     * Parses a client selector and a required workout plan, whose value may be empty.
     * Converts literal {@code \n} sequences in the plan to line breaks.
     *
     * @throws ParseException if the selector or required plan prefix is missing or invalid.
     */
    public PlanCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_PLAN);
        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, PlanCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG);
        String plan = argMultimap.getValue(PREFIX_PLAN).orElseThrow(()
                -> new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlanCommand.MESSAGE_USAGE)));
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_PLAN);
            return new PlanCommand(selector, ParserUtil.parseLongFormText(plan));
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), PlanCommand.MESSAGE_USAGE), pe);
        }
    }
}
