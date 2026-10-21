package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandParserTest {

    private final ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_emptyArgs_preservesClientsAndNextId() throws Exception {
        for (String args : new String[]{"", " \t "}) {
            Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

            assertCommandSuccess(parser.parse(args), model, ClearCommand.MESSAGE_CONFIRMATION, expectedModel);
        }
    }

    @Test
    public void parse_confirmFlag_clearsClientsAndResetsNextId() throws Exception {
        for (String args : new String[]{"--confirm", " \t --confirm \t "}) {
            Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

            assertCommandSuccess(parser.parse(args), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
        }
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);

        for (String args : new String[]{"3", "--foo", "--Confirm", "--confirm extra",
            "extra --confirm", "--confirm --confirm", "--confirm=true", "\"--confirm\""}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
