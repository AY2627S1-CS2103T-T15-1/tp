package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.LongFormText;
import seedu.address.model.person.PersonId;

public class NoteCommandParserTest {

    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE);

    private final NoteCommandParser parser = new NoteCommandParser();

    @Test
    public void parse_validArgs_returnsNoteCommand() {
        // by ID
        assertParseSuccess(parser, " --id 2 --note \"Knee injury\"",
                new NoteCommand(PersonSelector.byId(new PersonId(2)), new LongFormText("Knee injury")));

        // by name, with arguments in a different order
        assertParseSuccess(parser, " --note \"Prefers mornings\" --name john",
                new NoteCommand(PersonSelector.byName("john"), new LongFormText("Prefers mornings")));

        // empty note removes the note
        assertParseSuccess(parser, " --id 2 --note \"\"",
                new NoteCommand(PersonSelector.byId(new PersonId(2)), new LongFormText("")));
    }

    @Test
    public void parse_missingParts_failure() {
        // no client chosen
        assertParseFailure(parser, " --note \"Knee injury\"", INVALID_FORMAT);

        // no note
        assertParseFailure(parser, " --id 2", INVALID_FORMAT);

        // nothing
        assertParseFailure(parser, "", INVALID_FORMAT);
    }

    @Test
    public void parse_noteWithoutQuotes_failure() {
        String expectedMessage =
                ParserUtil.withUsage(ParserUtil.MESSAGE_TEXT_NOT_QUOTED, NoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --id 2 --note Knee injury", expectedMessage);
        assertParseFailure(parser, " --id 2 --note \"Knee injury", expectedMessage);
        assertParseFailure(parser, " --id 2 --note", expectedMessage);
    }

    @Test
    public void parse_longNote_preservesFullText() {
        String text = "  " + "a".repeat(501) + "  ";
        assertParseSuccess(parser, " --id 2 --note \"" + text + "\"",
                new NoteCommand(PersonSelector.byId(new PersonId(2)), new LongFormText(text)));
    }

    @Test
    public void parse_repeatedNote_failure() {
        assertParseFailure(parser, " --id 2 --note \"a\" --note \"b\"", ParserUtil.withUsage(
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTE), NoteCommand.MESSAGE_USAGE));
    }
}
