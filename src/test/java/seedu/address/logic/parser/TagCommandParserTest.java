package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.TagCommand;
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
}
