package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_noFilter_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "     ", expectedMessage);

        // old format without prefixes
        assertParseFailure(parser, "Alice Bob", expectedMessage);
    }

    @Test
    public void parse_oneFilter_returnsFindCommand() {
        assertParseSuccess(parser, " --name \"Alice Bob\"",
                new FindCommand(List.of(PersonSelector.byName("Alice Bob"))));
        assertParseSuccess(parser, " --phone 9123 4567",
                new FindCommand(List.of(PersonSelector.byPhone("91234567"))));
        assertParseSuccess(parser, " --id 3", new FindCommand(List.of(PersonSelector.byId(new PersonId(3)))));
    }

    @Test
    public void parse_severalFilters_returnsFindCommandWithAllFilters() {
        assertParseSuccess(parser, " --phone 9123 --name Alice",
                new FindCommand(List.of(PersonSelector.byName("Alice"), PersonSelector.byPhone("9123"))));
    }

    @Test
    public void parse_invalidValue_throwsParseExceptionWithUsage() {
        assertParseFailure(parser, " --phone abc",
                ParserUtil.withUsage(ParserUtil.MESSAGE_INVALID_PHONE_KEYWORD, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --name Alice --name Bob", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG), FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_groupOnly_returnsFindCommandWithGroup() {
        FindCommand expectedCommand = new FindCommand(List.of(), List.of(new Tag("morning")));
        assertParseSuccess(parser, " --group morning", expectedCommand);

        // group ignores upper and lower case, and may be in quotes
        assertParseSuccess(parser, " --group \"Morning\"", expectedCommand);

        // client types can be used as groups
        assertParseSuccess(parser, " --group GYM", new FindCommand(List.of(), List.of(Tag.GYM)));
    }

    @Test
    public void parse_groupAndOtherFilters_returnsFindCommandWithAll() {
        assertParseSuccess(parser, " --group rehab --name Alice",
                new FindCommand(List.of(PersonSelector.byName("Alice")), List.of(new Tag("rehab"))));
    }

    @Test
    public void parse_invalidGroup_throwsParseExceptionWithUsage() {
        String expectedMessage =
                ParserUtil.withUsage(FindCommandParser.MESSAGE_INVALID_GROUP, FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --group", expectedMessage);
        assertParseFailure(parser, " --group early birds", expectedMessage);
        assertParseFailure(parser, " --group rehab!", expectedMessage);
    }

    @Test
    public void parse_groupWithInvalidOtherFilter_throwsParseExceptionWithUsage() {
        assertParseFailure(parser, " --group rehab --phone abc",
                ParserUtil.withUsage(ParserUtil.MESSAGE_INVALID_PHONE_KEYWORD, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_groupWithPreamble_throwsParseException() {
        assertParseFailure(parser, " rehab --group morning",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_severalGroups_returnsFindCommandWithAllGroups() {
        assertParseSuccess(parser, " --group morning --group gym",
                new FindCommand(List.of(), List.of(new Tag("morning"), Tag.GYM)));

        // the same group given twice is only kept once
        assertParseSuccess(parser, " --group morning --group Morning",
                new FindCommand(List.of(), List.of(new Tag("morning"))));
    }

    @Test
    public void parse_severalGroupsWithOneInvalid_throwsParseExceptionWithUsage() {
        assertParseFailure(parser, " --group morning --group rehab!",
                ParserUtil.withUsage(FindCommandParser.MESSAGE_INVALID_GROUP, FindCommand.MESSAGE_USAGE));
    }
}
