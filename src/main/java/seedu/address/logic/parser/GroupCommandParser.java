package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.MoveGroupCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new GroupCommand object, or a MoveGroupCommand object
 * if {@code --move} is given.
 */
public class GroupCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_GROUP_NAME = "Group names should be made of letters and digits only, "
            + "with no spaces.";
    public static final String MESSAGE_CLIENT_TYPE_GROUP = "gym and personal are built-in labels, not groups. "
            + "Use tag --type to label a client as a gym or personal client.";
    public static final String MESSAGE_SAME_GROUP = "The groups after " + PREFIX_FROM + " and " + PREFIX_TO
            + " must be different.";

    /**
     * Parses the given {@code String} of arguments in the context of the group command
     * and returns the matching command object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME_LONG, PREFIX_MOVE, PREFIX_ID,
                PREFIX_FROM, PREFIX_TO);
        if (argMultimap.getValue(PREFIX_MOVE).isPresent()) {
            return parseMove(argMultimap);
        }

        Optional<String> nameValue = argMultimap.getValue(PREFIX_NAME_LONG);
        boolean hasMoveArguments = Stream.of(PREFIX_ID, PREFIX_FROM, PREFIX_TO)
                .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        if (nameValue.isEmpty() || !argMultimap.getPreamble().isEmpty() || hasMoveArguments) {
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
     * Parses {@code argMultimap}, which contains {@code --move}, into a MoveGroupCommand.
     *
     * @throws ParseException if {@code --move} has a value, the client is not chosen correctly,
     *     {@code --from} or {@code --to} is missing or invalid, or both name the same group.
     */
    private static MoveGroupCommand parseMove(ArgumentMultimap argMultimap) throws ParseException {
        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, GroupCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG);
        Optional<String> fromValue = argMultimap.getValue(PREFIX_FROM);
        Optional<String> toValue = argMultimap.getValue(PREFIX_TO);
        if (!argMultimap.getValue(PREFIX_MOVE).get().isEmpty() || fromValue.isEmpty() || toValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MOVE, PREFIX_FROM, PREFIX_TO);
            Tag from = parseGroup(fromValue.get());
            Tag to = parseGroup(toValue.get());
            if (from.equals(to)) {
                throw new ParseException(MESSAGE_SAME_GROUP);
            }
            return new MoveGroupCommand(selector, from, to);
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
