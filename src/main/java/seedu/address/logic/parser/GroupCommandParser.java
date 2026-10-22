package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import java.util.Locale;
import java.util.Optional;

import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new GroupCommand object
 */
public class GroupCommandParser implements Parser<GroupCommand> {

    public static final String MESSAGE_INVALID_GROUP_NAME = "Group names should be made of letters and digits only, "
            + "with no spaces.";
    public static final String MESSAGE_CLIENT_TYPE_GROUP = "gym and personal are built-in labels, not groups. "
            + "Use tag --type to label a client as a gym or personal client.";

    /**
     * Parses the given {@code String} of arguments in the context of the GroupCommand
     * and returns a GroupCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public GroupCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME_LONG);

        Optional<String> nameValue = argMultimap.getValue(PREFIX_NAME_LONG);
        if (nameValue.isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME_LONG);
            return new GroupCommand(parseGroup(nameValue.get()));
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), GroupCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses {@code name}, ignoring upper and lower case, into a group.
     *
     * @throws ParseException if {@code name} is not a valid tag name, or is a client type.
     */
    static Tag parseGroup(String name) throws ParseException {
        String normalizedName = removeOptionalQuotes(name.trim()).trim().toLowerCase(Locale.ROOT);
        if (!Tag.isValidTagName(normalizedName)) {
            throw new ParseException(MESSAGE_INVALID_GROUP_NAME);
        }

        Tag group = new Tag(normalizedName);
        if (Tag.CLIENT_TYPES.contains(group)) {
            throw new ParseException(MESSAGE_CLIENT_TYPE_GROUP);
        }
        return group;
    }

}
