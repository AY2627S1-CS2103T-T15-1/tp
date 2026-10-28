package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.MoveGroupCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

public class GroupCommandParserTest {

    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE);

    private final GroupCommandParser parser = new GroupCommandParser();

    @Test
    public void parse_validArgs_returnsGroupCommand() {
        GroupCommand expectedCommand = new GroupCommand(new Tag("morning"));
        assertParseSuccess(parser, " --name morning", expectedCommand);

        // name ignores upper and lower case, and may be in quotes
        assertParseSuccess(parser, " --name Morning", expectedCommand);
        assertParseSuccess(parser, " --name \"MORNING\"", expectedCommand);

        // digits are allowed
        assertParseSuccess(parser, " --name batch2", new GroupCommand(new Tag("batch2")));

        // spaces and hyphens between words are allowed, with or without quotes
        GroupCommand weightLossCommand = new GroupCommand(new Tag("weight loss"));
        assertParseSuccess(parser, " --name \"Weight Loss\"", weightLossCommand);
        assertParseSuccess(parser, " --name weight loss", weightLossCommand);
        assertParseSuccess(parser, " --name \"  weight   loss \"", weightLossCommand);
        assertParseSuccess(parser, " --name Weight-Loss", new GroupCommand(new Tag("weight-loss")));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", INVALID_FORMAT);
        assertParseFailure(parser, " morning", INVALID_FORMAT);
        assertParseFailure(parser, " morning --name rehab", INVALID_FORMAT);

        // move arguments without --move
        assertParseFailure(parser, " --name morning --from rehab", INVALID_FORMAT);
    }

    @Test
    public void parse_invalidName_failure() {
        String expectedMessage =
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, GroupCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --name", expectedMessage);
        assertParseFailure(parser, " --name \" \"", expectedMessage);
        assertParseFailure(parser, " --name rehab!", expectedMessage);
        assertParseFailure(parser, " --name weight--loss", expectedMessage);
        assertParseFailure(parser, " --name -loss", expectedMessage);
        assertParseFailure(parser, " --name weight-", expectedMessage);
    }

    @Test
    public void parse_clientType_failure() {
        String expectedMessage =
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_CLIENT_TYPE_GROUP, GroupCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --name gym", expectedMessage);
        assertParseFailure(parser, " --name Personal", expectedMessage);
    }

    @Test
    public void parse_repeatedName_failure() {
        assertParseFailure(parser, " --name morning --name rehab", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG), GroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_move_returnsMoveGroupCommand() {
        Tag morning = new Tag("morning");
        Tag evening = new Tag("evening");
        assertParseSuccess(parser, " --move --id 2 --from morning --to evening",
                new MoveGroupCommand(PersonSelector.byId(new PersonId(2)), morning, evening));

        // client chosen by name, and groups ignore upper and lower case
        assertParseSuccess(parser, " --move --name john --from Morning --to \"EVENING\"",
                new MoveGroupCommand(PersonSelector.byName("john"), morning, evening));

        // groups with spaces
        assertParseSuccess(parser, " --move --id 2 --from \"Weight Loss\" --to early birds",
                new MoveGroupCommand(PersonSelector.byId(new PersonId(2)), new Tag("weight loss"),
                        new Tag("early birds")));
    }

    @Test
    public void parse_moveMissingParts_failure() {
        assertParseFailure(parser, " --move --id 2 --from morning", INVALID_FORMAT);
        assertParseFailure(parser, " --move --id 2 --to evening", INVALID_FORMAT);
        assertParseFailure(parser, " --move --from morning --to evening", INVALID_FORMAT);
        assertParseFailure(parser, " --move now --id 2 --from morning --to evening", INVALID_FORMAT);
    }

    @Test
    public void parse_moveInvalidGroups_failure() {
        assertParseFailure(parser, " --move --id 2 --from morning --to Morning",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_SAME_GROUP, GroupCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --move --id 2 --from gym --to evening",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_CLIENT_TYPE_GROUP, GroupCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --move --id 2 --from morning --to weight--loss",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, GroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_delete_returnsDeleteGroupCommand() {
        DeleteGroupCommand expectedCommand = new DeleteGroupCommand(new Tag("morning"));
        assertParseSuccess(parser, " --delete --name morning", expectedCommand);

        // flags in any order, and name ignores upper and lower case, and may be in quotes
        assertParseSuccess(parser, " --name Morning --delete", expectedCommand);
        assertParseSuccess(parser, " --delete --name \"MORNING\"", expectedCommand);
    }

    @Test
    public void parse_deleteInvalidFormat_failure() {
        assertParseFailure(parser, " --delete", INVALID_FORMAT);
        assertParseFailure(parser, " --delete morning", INVALID_FORMAT);
        assertParseFailure(parser, " --delete now --name morning", INVALID_FORMAT);
        assertParseFailure(parser, " extra --delete --name morning", INVALID_FORMAT);
        assertParseFailure(parser, " --delete --name morning --id 2", INVALID_FORMAT);
        assertParseFailure(parser, " --delete --move --name morning", INVALID_FORMAT);
        assertParseFailure(parser, " --delete --name morning --from rehab --to evening", INVALID_FORMAT);
    }

    @Test
    public void parse_deleteInvalidGroup_failure() {
        assertParseFailure(parser, " --delete --name",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, GroupCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --delete --name weight--loss",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, GroupCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --delete --name gym",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_CLIENT_TYPE_GROUP, GroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_deleteRepeatedPrefix_failure() {
        assertParseFailure(parser, " --delete --name morning --name rehab", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG), GroupCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --delete --delete --name morning", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DELETE), GroupCommand.MESSAGE_USAGE));
    }
}
