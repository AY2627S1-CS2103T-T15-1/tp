package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.PlanCommand;
import seedu.address.model.LongFormText;
import seedu.address.model.person.PersonId;

public class PlanCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlanCommand.MESSAGE_USAGE);
    private static final PersonSelector SELECT_ID = PersonSelector.byId(new PersonId(2));
    private static final String PLAN = "Squats: 3 sets of 12";

    private final PlanCommandParser parser = new PlanCommandParser();

    @Test
    public void parse_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_quotedNote_success() {
        PlanCommand expected = new PlanCommand(SELECT_ID, new LongFormText(PLAN));
        assertParseSuccess(parser, " --id 2 --note \"" + PLAN + "\"", expected);
        assertParseSuccess(parser, " --id \"2\" --note \"" + PLAN + "\"", expected);
    }

    @Test
    public void parse_unquotedOrIncompleteQuotes_failure() {
        for (String note : new String[] {"Squats", "\"Squats", "Squats\"", "", "\""}) {
            assertParseFailure(parser, " --id 2 --note " + note,
                    ParserUtil.withUsage("Put text in double quotes, e.g. \"TEXT\". Use \"\" to clear it.",
                            PlanCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_nameWithOptionalQuotes_success() {
        PlanCommand expected = new PlanCommand(PersonSelector.byName("Alice Pauline"), new LongFormText(PLAN));
        assertParseSuccess(parser, " --name Alice Pauline --note \"" + PLAN + "\"", expected);
        assertParseSuccess(parser, " --name \"Alice Pauline\" --note \"" + PLAN + "\"", expected);
    }

    @Test
    public void parse_noteBeforeSelector_success() {
        assertParseSuccess(parser, " --note \"" + PLAN + "\" --id 2",
                new PlanCommand(SELECT_ID, new LongFormText(PLAN)));
    }

    @Test
    public void parse_newlineEscapes_convertsInsideQuotes() {
        PlanCommand expected = new PlanCommand(SELECT_ID, new LongFormText("Squats\nPush-ups\n\nRest"));
        assertParseSuccess(parser, " --id 2 --note \"Squats\\nPush-ups\\n\\nRest\"", expected);
    }

    @Test
    public void parse_otherBackslashes_preservesText() {
        String note = "Rest\\t60s; tempo \\ 3-1-2";
        assertParseSuccess(parser, " --id 2 --note \"" + note + "\"",
                new PlanCommand(SELECT_ID, new LongFormText(note)));
    }

    @Test
    public void parse_emptyNote_clearsPlan() {
        PlanCommand expected = new PlanCommand(SELECT_ID, new LongFormText(""));
        assertParseSuccess(parser, " --id 2 --note \"\"", expected);
        assertParseSuccess(parser, " --note \"\" --id 2", expected);
    }

    @Test
    public void parse_noteText_preservesInternalSpacesPunctuationAndQuotes() {
        String note = "Squats: 3 x 12;  rest 60s. Coach says \"steady\".";
        assertParseSuccess(parser, " --id 2 --note \"" + note + "\"",
                new PlanCommand(SELECT_ID, new LongFormText(note)));
    }

    @Test
    public void parse_missingRequiredParts_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " --id 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " --name Alice", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " --note " + PLAN, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "2 --note " + PLAN, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "unexpected --id 2 --note " + PLAN, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidId_failure() {
        for (String id : new String[] {"", "0", "-1", "abc", "1.5", "2147483648"}) {
            assertParseFailure(parser, " --id " + id + " --note " + PLAN,
                    ParserUtil.withUsage(PersonId.MESSAGE_CONSTRAINTS, PlanCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_blankName_failure() {
        String expected = ParserUtil.withUsage(ParserUtil.MESSAGE_BLANK_NAME_KEYWORD, PlanCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " --name --note " + PLAN, expected);
        assertParseFailure(parser, " --name \"\" --note " + PLAN, expected);
    }

    @Test
    public void parse_conflictingSelectors_failure() {
        assertParseFailure(parser, " --id 2 --name Alice --note " + PLAN,
                ParserUtil.withUsage(String.format(ParserUtil.MESSAGE_CONFLICTING_SELECTORS, "--id or --name"),
                        PlanCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_repeatedPrefixes_failure() {
        assertParseFailure(parser, " --id 2 --id 3 --note " + PLAN,
                ParserUtil.withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ID),
                        PlanCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --name Alice --name Bob --note " + PLAN,
                ParserUtil.withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME_LONG),
                        PlanCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --id 2 --note Squats --note Push-ups",
                ParserUtil.withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NOTE),
                        PlanCommand.MESSAGE_USAGE));
    }
}
