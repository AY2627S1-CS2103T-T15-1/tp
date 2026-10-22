package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNTAG;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddToGroupCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand, AddToGroupCommand or UntagCommand object,
 * depending on whether {@code --type}, {@code --group} or {@code --untag} is given,
 * or a DeleteGroupCommand object if {@code --delete} is given.
 */
public class TagCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_CLIENT_TYPE = "Invalid client type: enter "
            + PREFIX_TYPE + " gym or " + PREFIX_TYPE + " personal.";
    public static final String MESSAGE_CONFLICTING_ACTIONS = "Give only one of " + PREFIX_TYPE + ", "
            + PREFIX_GROUP + " and " + PREFIX_UNTAG + ".";
    public static final String MESSAGE_INVALID_UNTAG = "Enter a tag name made of letters and digits only after "
            + PREFIX_UNTAG + ".";

    private static final Prefix[] ACTION_PREFIXES = {PREFIX_TYPE, PREFIX_GROUP, PREFIX_UNTAG};

    /**
     * Parses the given {@code String} of arguments in the context of the tag command
     * and returns the matching command object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TYPE,
                PREFIX_GROUP, PREFIX_UNTAG, PREFIX_DELETE);
        if (argMultimap.getValue(PREFIX_DELETE).isPresent()) {
            return parseDeleteGroup(argMultimap);
        }

        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, TagCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG);
        List<Prefix> givenActions = Stream.of(ACTION_PREFIXES)
                .filter(prefix -> argMultimap.getValue(prefix).isPresent())
                .toList();
        if (givenActions.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        try {
            if (givenActions.size() > 1) {
                throw new ParseException(MESSAGE_CONFLICTING_ACTIONS);
            }
            argMultimap.verifyNoDuplicatePrefixesFor(ACTION_PREFIXES);
            Prefix action = givenActions.get(0);
            String value = argMultimap.getValue(action).get();
            if (action.equals(PREFIX_GROUP)) {
                return new AddToGroupCommand(selector, GroupCommandParser.parseGroup(value));
            }
            if (action.equals(PREFIX_UNTAG)) {
                return new UntagCommand(selector, parseTag(value));
            }
            assert action.equals(PREFIX_TYPE) : "The tag command only has --type, --group and --untag";
            return new TagCommand(selector, parseClientType(value));
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), TagCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses {@code argMultimap}, which contains {@code --delete}, into a DeleteGroupCommand.
     * Only {@code --delete} with no value and one {@code --group} may be given.
     *
     * @throws ParseException if other arguments are given, or the group is invalid.
     */
    private static DeleteGroupCommand parseDeleteGroup(ArgumentMultimap argMultimap) throws ParseException {
        boolean hasOtherArguments = !argMultimap.getPreamble().isEmpty()
                || !argMultimap.getValue(PREFIX_DELETE).get().isEmpty()
                || Stream.of(PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TYPE, PREFIX_UNTAG)
                        .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        if (hasOtherArguments || argMultimap.getValue(PREFIX_GROUP).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DELETE, PREFIX_GROUP);
            return new DeleteGroupCommand(GroupCommandParser.parseGroup(argMultimap.getValue(PREFIX_GROUP).get()));
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

    /**
     * Parses {@code tag}, ignoring upper and lower case, into a {@code Tag}, which can be a group or a client type.
     *
     * @throws ParseException if {@code tag} is not a valid tag name.
     */
    private static Tag parseTag(String tag) throws ParseException {
        String normalizedTag = removeOptionalQuotes(tag.trim()).trim().toLowerCase(Locale.ROOT);
        if (!Tag.isValidTagName(normalizedTag)) {
            throw new ParseException(MESSAGE_INVALID_UNTAG);
        }
        return new Tag(normalizedTag);
    }

}
