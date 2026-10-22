package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.GroupCommand;
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
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", INVALID_FORMAT);
        assertParseFailure(parser, " morning", INVALID_FORMAT);
        assertParseFailure(parser, " morning --name rehab", INVALID_FORMAT);
    }

    @Test
    public void parse_invalidName_failure() {
        String expectedMessage =
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, GroupCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --name", expectedMessage);
        assertParseFailure(parser, " --name early birds", expectedMessage);
        assertParseFailure(parser, " --name rehab!", expectedMessage);
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
}
