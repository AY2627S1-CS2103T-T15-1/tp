package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.SetActiveCommand;
import seedu.address.model.person.PersonId;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the SetActiveCommandParser code. The path variations for choosing a client
 * occur inside the ParserUtil, and therefore should be covered by the ParserUtilTest.
 */
public class SetActiveCommandParserTest {

    private final SetActiveCommandParser activeParser = new SetActiveCommandParser(true);
    private final SetActiveCommandParser inactiveParser = new SetActiveCommandParser(false);

    @Test
    public void parse_validArgs_returnsSetActiveCommand() {
        assertParseSuccess(activeParser, " --id 3", new SetActiveCommand(PersonSelector.byId(new PersonId(3)), true));
        assertParseSuccess(inactiveParser, " --name bob", new SetActiveCommand(PersonSelector.byName("bob"), false));
        assertParseSuccess(inactiveParser, " --phone 9123 4567",
                new SetActiveCommand(PersonSelector.byPhone("91234567"), false));
    }

    @Test
    public void parse_noClientChosen_showsUsageOfThatCommand() {
        assertParseFailure(activeParser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetActiveCommand.MESSAGE_USAGE_ACTIVE));
        assertParseFailure(inactiveParser, "3",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetActiveCommand.MESSAGE_USAGE_INACTIVE));
    }
}
