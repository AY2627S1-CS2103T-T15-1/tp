package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LIST;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.ListGroupsCommand;
import seedu.address.logic.commands.MoveGroupCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new GroupCommand object, a MoveGroupCommand object
 * if {@code --move} is given, a DeleteGroupCommand object if {@code --delete} is given,
 * or a ListGroupsCommand object if {@code --list} is given.
 */
public class GroupCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_GROUP_NAME = "Group names should be words made of letters and digits, "
            + "separated by spaces or single hyphens, e.g. morning or weight loss.";
    public static final String MESSAGE_CLIENT_TYPE_GROUP = "gym and personal are built-in labels, not groups. "
            + "Use tag --type to label a client as a gym or personal client.";
    public static final String MESSAGE_SAME_GROUP = "The groups after " + PREFIX_FROM + " and " + PREFIX_TO
            + " must be different.";
    public static final String MESSAGE_MISSING_FROM = "Missing " + PREFIX_FROM
            + " GROUP: give the group to move the client out of.";
    public static final String MESSAGE_MISSING_TO = "Missing " + PREFIX_TO
            + " GROUP: give the group to move the client into.";
    public static final String MESSAGE_MISSING_FROM_AND_TO = "Missing " + PREFIX_FROM + " GROUP and " + PREFIX_TO
            + " GROUP: give the group to move the client out of and the group to move them into.";

    /**
     * Parses the given {@code String} of arguments in the context of the group command
     * and returns the matching command object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME_LONG, PREFIX_MOVE, PREFIX_ID,
                PREFIX_FROM, PREFIX_TO, PREFIX_DELETE, PREFIX_LIST);
        if (argMultimap.getValue(PREFIX_DELETE).isPresent()) {
            return parseDelete(argMultimap);
        }
        if (argMultimap.getValue(PREFIX_LIST).isPresent()) {
            return parseList(argMultimap);
        }
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
     * Parses {@code argMultimap}, which contains {@code --list}, into a ListGroupsCommand.
     * Only {@code --list} with no value may be given.
     *
     * @throws ParseException if anything other than {@code --list} is given.
     */
    private static ListGroupsCommand parseList(ArgumentMultimap argMultimap) throws ParseException {
        boolean hasOtherArguments = !argMultimap.getPreamble().isEmpty()
                || !argMultimap.getValue(PREFIX_LIST).get().isEmpty()
                || Stream.of(PREFIX_NAME_LONG, PREFIX_MOVE, PREFIX_ID, PREFIX_FROM, PREFIX_TO, PREFIX_DELETE)
                        .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        if (hasOtherArguments) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_LIST);
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), GroupCommand.MESSAGE_USAGE), pe);
        }
        return new ListGroupsCommand();
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
        if (!argMultimap.getValue(PREFIX_MOVE).get().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }
        Optional<String> fromValue = argMultimap.getValue(PREFIX_FROM);
        Optional<String> toValue = argMultimap.getValue(PREFIX_TO);

        try {
            requireBothGroups(fromValue.isPresent(), toValue.isPresent());
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
     * Checks that both {@code --from} and {@code --to} were given to a move.
     *
     * @throws ParseException naming whichever of them is missing.
     */
    private static void requireBothGroups(boolean hasFrom, boolean hasTo) throws ParseException {
        if (!hasFrom && !hasTo) {
            throw new ParseException(MESSAGE_MISSING_FROM_AND_TO);
        }
        if (!hasFrom) {
            throw new ParseException(MESSAGE_MISSING_FROM);
        }
        if (!hasTo) {
            throw new ParseException(MESSAGE_MISSING_TO);
        }
    }

    /**
     * Parses {@code argMultimap}, which contains {@code --delete}, into a DeleteGroupCommand.
     * Only {@code --delete} with no value and one {@code --name} may be given.
     *
     * @throws ParseException if other arguments are given, or the group is invalid.
     */
    private static DeleteGroupCommand parseDelete(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> nameValue = argMultimap.getValue(PREFIX_NAME_LONG);
        boolean hasOtherArguments = !argMultimap.getPreamble().isEmpty()
                || !argMultimap.getValue(PREFIX_DELETE).get().isEmpty()
                || Stream.of(PREFIX_MOVE, PREFIX_ID, PREFIX_FROM, PREFIX_TO, PREFIX_LIST)
                        .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        if (hasOtherArguments || nameValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DELETE, PREFIX_NAME_LONG);
            return new DeleteGroupCommand(parseGroup(nameValue.get()));
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
        String normalizedName = ParserUtil.normalizeTagName(name);
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
