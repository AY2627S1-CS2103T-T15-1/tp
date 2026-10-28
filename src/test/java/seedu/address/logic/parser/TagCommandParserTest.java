package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddToGroupCommand;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {

    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE);

    private final TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_validArgs_returnsTagCommand() {
        assertParseSuccess(parser, " --id 2 --type gym",
                new TagCommand(PersonSelector.byId(new PersonId(2)), Tag.GYM));

        // type ignores upper and lower case, and may be in quotes
        assertParseSuccess(parser, " --type \"Personal\" --name john",
                new TagCommand(PersonSelector.byName("john"), Tag.PERSONAL));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, " --type gym", INVALID_FORMAT);
        assertParseFailure(parser, " --id 2", INVALID_FORMAT);
        assertParseFailure(parser, "", INVALID_FORMAT);
    }

    @Test
    public void parse_invalidType_failure() {
        String expectedMessage =
                ParserUtil.withUsage(TagCommandParser.MESSAGE_INVALID_CLIENT_TYPE, TagCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --id 2 --type friends", expectedMessage);
        assertParseFailure(parser, " --id 2 --type", expectedMessage);
    }

    @Test
    public void parse_repeatedType_failure() {
        assertParseFailure(parser, " --id 2 --type gym --type personal", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TYPE), TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_group_returnsAddToGroupCommand() {
        AddToGroupCommand expectedCommand = new AddToGroupCommand(PersonSelector.byId(new PersonId(2)),
                new Tag("morning"));
        assertParseSuccess(parser, " --id 2 --group morning", expectedCommand);

        // group ignores upper and lower case, and may be in quotes
        assertParseSuccess(parser, " --group \"Morning\" --id 2", expectedCommand);

        assertParseSuccess(parser, " --name john --group rehab",
                new AddToGroupCommand(PersonSelector.byName("john"), new Tag("rehab")));

        // group with spaces
        assertParseSuccess(parser, " --id 2 --group \"Weight Loss\"",
                new AddToGroupCommand(PersonSelector.byId(new PersonId(2)), new Tag("weight loss")));
    }

    @Test
    public void parse_invalidGroup_failure() {
        assertParseFailure(parser, " --id 2 --group weight--loss",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, TagCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --id 2 --group",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_INVALID_GROUP_NAME, TagCommand.MESSAGE_USAGE));

        // client types are set with --type, not added as groups
        assertParseFailure(parser, " --id 2 --group gym",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_CLIENT_TYPE_GROUP, TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_untag_returnsUntagCommand() {
        assertParseSuccess(parser, " --name john --untag Morning",
                new UntagCommand(PersonSelector.byName("john"), new Tag("morning")));

        // client types can be removed too
        assertParseSuccess(parser, " --id 2 --untag gym", new UntagCommand(PersonSelector.byId(new PersonId(2)),
                Tag.GYM));
    }

    @Test
    public void parse_invalidUntag_failure() {
        String expectedMessage = ParserUtil.withUsage(TagCommandParser.MESSAGE_INVALID_UNTAG,
                TagCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --id 2 --untag", expectedMessage);
        assertParseFailure(parser, " --id 2 --untag rehab!", expectedMessage);
    }

    @Test
    public void parse_severalActions_failure() {
        String expectedMessage = ParserUtil.withUsage(TagCommandParser.MESSAGE_CONFLICTING_ACTIONS,
                TagCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --id 2 --type gym --group morning", expectedMessage);
        assertParseFailure(parser, " --id 2 --group morning --untag rehab", expectedMessage);
    }

    @Test
    public void parse_repeatedGroup_failure() {
        assertParseFailure(parser, " --id 2 --group morning --group rehab", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GROUP), TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_deleteGroup_returnsDeleteGroupCommand() {
        DeleteGroupCommand expectedCommand = new DeleteGroupCommand(new Tag("morning"));
        assertParseSuccess(parser, " --delete --group morning", expectedCommand);

        // order does not matter, and group ignores upper and lower case
        assertParseSuccess(parser, " --group Morning --delete", expectedCommand);
    }

    @Test
    public void parse_deleteWithOtherArguments_failure() {
        assertParseFailure(parser, " --delete", INVALID_FORMAT);
        assertParseFailure(parser, " --delete now --group morning", INVALID_FORMAT);
        assertParseFailure(parser, " --id 2 --delete --group morning", INVALID_FORMAT);
        assertParseFailure(parser, " --delete --group morning --untag rehab", INVALID_FORMAT);
    }

    @Test
    public void parse_deleteClientType_failure() {
        assertParseFailure(parser, " --delete --group gym",
                ParserUtil.withUsage(GroupCommandParser.MESSAGE_CLIENT_TYPE_GROUP, TagCommand.MESSAGE_USAGE));
    }
}
