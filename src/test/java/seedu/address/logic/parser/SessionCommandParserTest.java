package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.LongFormText;
import seedu.address.model.person.PersonId;
import seedu.address.model.session.Session;

public class SessionCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE);
    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);

    private final SessionCommandParser parser = new SessionCommandParser();

    @Test
    public void parse_longFormNotes_preservesWhitespaceAndDecodesLineBreaks() {
        for (String action : new String[]{"--add", "--edit"}) {
            LongFormText note = new LongFormText("  Squats\nRest\n\nRemember --date and --edit  ");
            var selector = PersonSelector.byId(new PersonId(2));
            assertParseSuccess(parser,
                    " " + action + " --id 2 --date 2026-10-21"
                            + " --note \"  Squats\\nRest\\n\\nRemember --date and --edit  \"",
                    action.equals("--add") ? new AddSessionCommand(selector, DATE, note)
                            : new EditSessionCommand(selector, DATE, note));
        }
    }

    @Test
    public void parse_unquotedOrIncompleteNote_failure() {
        for (String action : new String[]{"--add", "--edit"}) {
            for (String note : new String[]{"", "Squats", "\"Squats", "Squats\""}) {
                assertParseFailure(parser, " " + action + " --id 2 --date 2026-10-21 --note " + note,
                        ParserUtil.MESSAGE_TEXT_NOT_QUOTED);
            }
        }
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byId(new PersonId(2)), DATE, new LongFormText("Worked on squat form"));

        assertParseSuccess(parser,
                " --add --id 2 --date 2026-10-21 --note \"Worked on squat form\"", expectedCommand);
    }

    @Test
    public void parse_nameAndEmptyNote_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byName("Alice Pauline"), DATE, new LongFormText(""));

        assertParseSuccess(parser, " --name Alice Pauline --note \"\" --date 2026-10-21 --add", expectedCommand);
    }

    @Test
    public void parse_noteOmitted_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byId(new PersonId(2)), DATE, new LongFormText(""));

        assertParseSuccess(parser, " --add --id 2 --date 2026-10-21", expectedCommand);
    }

    @Test
    public void parse_quotedValues_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byName("Alice Pauline"), DATE, new LongFormText("Worked on squat form"));

        assertParseSuccess(parser,
                " --add --name \"Alice Pauline\" --date \"2026-10-21\" --note \"Worked on squat form\"",
                expectedCommand);
    }

    @Test
    public void parse_editWithNote_success() {
        EditSessionCommand expectedCommand = new EditSessionCommand(
                PersonSelector.byId(new PersonId(2)), DATE, new LongFormText("Improved squat depth"));

        assertParseSuccess(parser,
                " --edit --id 2 --date 2026-10-21 --note \"Improved squat depth\"", expectedCommand);
    }

    @Test
    public void parse_editWithEmptyNote_success() {
        EditSessionCommand expectedCommand = new EditSessionCommand(
                PersonSelector.byName("Alice Pauline"), DATE, new LongFormText(""));

        assertParseSuccess(parser,
                " --name Alice Pauline --date 2026-10-21 --note \"\" --edit", expectedCommand);
    }

    @Test
    public void parse_invalidAction_failure() {
        assertParseFailure(parser, " --id 2 --date 2026-10-21", SessionCommandParser.MESSAGE_ACTION_REQUIRED);
        assertParseFailure(parser, " --add --edit --id 2 --date 2026-10-21",
                SessionCommandParser.MESSAGE_ACTION_REQUIRED);
        assertParseFailure(parser, " --add unexpected --id 2 --date 2026-10-21", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingRequiredField_failure() {
        assertParseFailure(parser, " --add --date 2026-10-21", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " --add --id 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " --edit --id 2 --date 2026-10-21",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, " --add --id 2 --date 21-10-2026", Session.MESSAGE_DATE_CONSTRAINTS);
        assertParseFailure(parser, " --add --id 2 --date 2026-02-30", Session.MESSAGE_DATE_CONSTRAINTS);
        assertParseFailure(parser, " --add --id 2 --date +10000-10-21", Session.MESSAGE_DATE_CONSTRAINTS);
        assertParseFailure(parser, " --add --id 2 --date -0001-10-21", Session.MESSAGE_DATE_CONSTRAINTS);
        assertParseFailure(parser, " --add --id 2 --date 2026-1-01", Session.MESSAGE_DATE_CONSTRAINTS);
    }

    @Test
    public void parse_quotedNoteContainingFlags_preservesNote() {
        assertParseSuccess(parser,
                " --add --id 2 --date 2026-10-21 --note \"Remember --edit and --date\"",
                new AddSessionCommand(PersonSelector.byId(new PersonId(2)), DATE,
                        new LongFormText("Remember --edit and --date")));
    }

    @Test
    public void parse_misspelledAction_failure() {
        assertParseFailure(parser, " --address --id 2 --date 2026-10-21",
                SessionCommandParser.MESSAGE_ACTION_REQUIRED);
    }

    @Test
    public void parse_validLeapDay_success() {
        assertParseSuccess(parser, " --add --id 2 --date 2028-02-29",
                new AddSessionCommand(PersonSelector.byId(new PersonId(2)), LocalDate.of(2028, 2, 29),
                        new LongFormText("")));
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        assertParseFailure(parser, " --add --add --id 2 --date 2026-10-21",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADD));
        assertParseFailure(parser, " --add --id 2 --date 2026-10-21 --date 2026-10-22",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
        assertParseFailure(parser, " --add --id 2 --date 2026-10-21 --note First --note Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTE));
    }
}
