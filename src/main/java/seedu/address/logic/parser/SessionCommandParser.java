package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EDIT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.session.Session;

/**
 * Parses arguments for the {@code session} command.
 */
public class SessionCommandParser implements Parser<SessionCommand> {

    public static final String MESSAGE_ACTION_REQUIRED =
            "Choose exactly one session action: --add or --edit.";

    /**
     * Parses the given arguments and creates a session command.
     */
    @Override
    public SessionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ADD, PREFIX_EDIT,
                PREFIX_ID, PREFIX_NAME_LONG, PREFIX_DATE, PREFIX_NOTE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ADD, PREFIX_EDIT, PREFIX_ID, PREFIX_NAME_LONG,
                PREFIX_DATE, PREFIX_NOTE);

        boolean hasAdd = argMultimap.getValue(PREFIX_ADD).isPresent();
        boolean hasEdit = argMultimap.getValue(PREFIX_EDIT).isPresent();
        if (hasAdd == hasEdit) {
            throw new ParseException(MESSAGE_ACTION_REQUIRED);
        }

        String messageUsage = hasAdd ? AddSessionCommand.MESSAGE_USAGE : EditSessionCommand.MESSAGE_USAGE;
        if (!argMultimap.getValue(hasAdd ? PREFIX_ADD : PREFIX_EDIT).orElse("").isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }

        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, messageUsage);
        String dateValue = argMultimap.getValue(PREFIX_DATE)
                .orElseThrow(() -> new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage)));
        LocalDate date = parseDate(removeOptionalQuotes(dateValue));
        if (hasEdit && argMultimap.getValue(PREFIX_NOTE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }

        String note = argMultimap.getValue(PREFIX_NOTE).map(ParserUtil::removeOptionalQuotes).orElse("");
        return hasAdd
                ? new AddSessionCommand(selector, date, note)
                : new EditSessionCommand(selector, date, note);
    }

    private static LocalDate parseDate(String date) throws ParseException {
        try {
            return Session.parseDate(date);
        } catch (DateTimeParseException exception) {
            throw new ParseException(Session.MESSAGE_DATE_CONSTRAINTS);
        }
    }
}
