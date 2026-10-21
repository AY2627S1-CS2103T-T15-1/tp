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
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.person.PersonId;
import seedu.address.model.session.Session;

public class SessionCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE);
    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);

    private final SessionCommandParser parser = new SessionCommandParser();

    @Test
    public void parse_allFieldsSpecified_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byId(new PersonId(2)), DATE, "Worked on squat form");

        assertParseSuccess(parser,
                " --add --id 2 --date 2026-10-21 --note Worked on squat form", expectedCommand);
    }

    @Test
    public void parse_nameAndEmptyNote_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byName("Alice Pauline"), DATE, "");

        assertParseSuccess(parser, " --name Alice Pauline --note --date 2026-10-21 --add", expectedCommand);
    }

    @Test
    public void parse_noteOmitted_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byId(new PersonId(2)), DATE, "");

        assertParseSuccess(parser, " --add --id 2 --date 2026-10-21", expectedCommand);
    }

    @Test
    public void parse_quotedValues_success() {
        AddSessionCommand expectedCommand = new AddSessionCommand(
                PersonSelector.byName("Alice Pauline"), DATE, "Worked on squat form");

        assertParseSuccess(parser,
                " --add --name \"Alice Pauline\" --date \"2026-10-21\" --note \"Worked on squat form\"",
                expectedCommand);
    }

    @Test
    public void parse_invalidAction_failure() {
        assertParseFailure(parser, " --id 2 --date 2026-10-21", SessionCommandParser.MESSAGE_ACTION_REQUIRED);
        assertParseFailure(parser, " --add --edit --id 2 --date 2026-10-21",
                SessionCommandParser.MESSAGE_ACTION_REQUIRED);
        assertParseFailure(parser, " --edit --id 2 --date 2026-10-21",
                SessionCommandParser.MESSAGE_EDIT_NOT_AVAILABLE);
        assertParseFailure(parser, " --add unexpected --id 2 --date 2026-10-21", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingRequiredField_failure() {
        assertParseFailure(parser, " --add --date 2026-10-21", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " --add --id 2", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, " --add --id 2 --date 21-10-2026", Session.MESSAGE_DATE_CONSTRAINTS);
        assertParseFailure(parser, " --add --id 2 --date 2026-02-30", Session.MESSAGE_DATE_CONSTRAINTS);
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
