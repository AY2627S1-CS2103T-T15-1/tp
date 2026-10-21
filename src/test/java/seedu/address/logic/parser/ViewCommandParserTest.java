package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.person.PersonId;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the ViewCommand code. The path variations for choosing a client
 * occur inside the ParserUtil, and therefore should be covered by the ParserUtilTest.
 */
public class ViewCommandParserTest {

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, " --id 1", new ViewCommand(PersonSelector.byId(new PersonId(1))));
        assertParseSuccess(parser, " --name \"Alice Pauline\"",
                new ViewCommand(PersonSelector.byName("Alice Pauline")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);

        // list position instead of ID
        assertParseFailure(parser, "1", expectedMessage);

        // nothing given
        assertParseFailure(parser, "", expectedMessage);

        // invalid ID
        assertParseFailure(parser, " --id 0",
                ParserUtil.withUsage(PersonId.MESSAGE_CONSTRAINTS, ViewCommand.MESSAGE_USAGE));
    }
}
