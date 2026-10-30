package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_PHONE;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter for edit: %1$s.";

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ParserUtil.verifyKnownParameters(args, MESSAGE_UNKNOWN_PARAMETER, EditCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TO_NAME, PREFIX_TO_PHONE);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TO_NAME, PREFIX_TO_PHONE);

        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, EditCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG);
        try {
            return new EditCommand(selector, parseEditPersonDescriptor(argMultimap));
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), EditCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses the new values in {@code argMultimap} into an {@code EditPersonDescriptor}.
     *
     * @throws ParseException if a new value is invalid or given more than once, or no new value is given.
     */
    private static EditPersonDescriptor parseEditPersonDescriptor(ArgumentMultimap argMultimap)
            throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TO_NAME, PREFIX_TO_PHONE);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_TO_NAME).isPresent()) {
            editPersonDescriptor.setName(
                    ParserUtil.parseName(removeOptionalQuotes(argMultimap.getValue(PREFIX_TO_NAME).get())));
        }
        if (argMultimap.getValue(PREFIX_TO_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(
                    ParserUtil.parsePhone(removeOptionalQuotes(argMultimap.getValue(PREFIX_TO_PHONE).get())));
        }
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }
        return editPersonDescriptor;
    }
}
