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

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TO_NAME, PREFIX_TO_PHONE);

        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, EditCommand.MESSAGE_USAGE);
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

        return new EditCommand(selector, editPersonDescriptor);
    }
}
