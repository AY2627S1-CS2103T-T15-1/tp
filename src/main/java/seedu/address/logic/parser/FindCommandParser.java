package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    public static final String MESSAGE_INVALID_GROUP = "Enter a group name made of letters, digits, "
            + "spaces and hyphens, at most " + Tag.MAX_LENGTH + " characters long, after " + PREFIX_GROUP + ".";

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter for find: %1$s.";

    private static final Prefix[] PERSON_FILTER_PREFIXES = {PREFIX_NAME_LONG, PREFIX_PHONE_LONG, PREFIX_ID};

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ParserUtil.verifyKnownParameters(args, MESSAGE_UNKNOWN_PARAMETER, FindCommand.MESSAGE_USAGE,
                PREFIX_NAME_LONG, PREFIX_PHONE_LONG, PREFIX_ID, PREFIX_GROUP);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME_LONG, PREFIX_PHONE_LONG, PREFIX_ID,
                PREFIX_GROUP);

        List<String> groupValues = argMultimap.getAllValues(PREFIX_GROUP);
        if (groupValues.isEmpty()) {
            return new FindCommand(ParserUtil.parsePersonFilters(argMultimap, FindCommand.MESSAGE_USAGE,
                    PERSON_FILTER_PREFIXES));
        }

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        List<Tag> groups = new ArrayList<>();
        for (String groupValue : groupValues) {
            try {
                Tag group = parseGroup(groupValue);
                if (!groups.contains(group)) {
                    groups.add(group);
                }
            } catch (ParseException pe) {
                throw new ParseException(ParserUtil.withUsage(pe.getMessage(), FindCommand.MESSAGE_USAGE), pe);
            }
        }

        boolean hasPersonFilter = Stream.of(PERSON_FILTER_PREFIXES)
                .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        List<PersonSelector> filters = hasPersonFilter
                ? ParserUtil.parsePersonFilters(argMultimap, FindCommand.MESSAGE_USAGE, PERSON_FILTER_PREFIXES)
                : List.of();
        return new FindCommand(filters, groups);
    }

    /**
     * Parses {@code group}, ignoring upper and lower case, into a {@code Tag}.
     *
     * @throws ParseException if {@code group} is not a valid tag name.
     */
    private static Tag parseGroup(String group) throws ParseException {
        String normalizedGroup = ParserUtil.normalizeTagName(group);
        if (!Tag.isValidTagName(normalizedGroup)) {
            throw new ParseException(MESSAGE_INVALID_GROUP);
        }
        return new Tag(normalizedGroup);
    }

}
