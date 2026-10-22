package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import java.util.Locale;
import java.util.Optional;

import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object
 */
public class TagCommandParser implements Parser<TagCommand> {

    public static final String MESSAGE_INVALID_CLIENT_TYPE = "Invalid client type: enter "
            + PREFIX_TYPE + " gym or " + PREFIX_TYPE + " personal.";

    /**
     * Parses the given {@code String} of arguments in the context of the TagCommand
     * and returns a TagCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TYPE);

        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, TagCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG);
        Optional<String> typeValue = argMultimap.getValue(PREFIX_TYPE);
        if (typeValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TYPE);
            return new TagCommand(selector, parseClientType(typeValue.get()));
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), TagCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses {@code type}, ignoring upper and lower case, into one of the client type tags.
     *
     * @throws ParseException if {@code type} is not one of the client types.
     */
    private static Tag parseClientType(String type) throws ParseException {
        String normalizedType = removeOptionalQuotes(type.trim()).trim().toLowerCase(Locale.ROOT);
        return Tag.CLIENT_TYPES.stream()
                .filter(clientType -> clientType.tagName.equals(normalizedType))
                .findFirst()
                .orElseThrow(() -> new ParseException(MESSAGE_INVALID_CLIENT_TYPE));
    }

}
