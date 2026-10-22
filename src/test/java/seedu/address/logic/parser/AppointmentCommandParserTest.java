package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.ListAppointmentsCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.PersonId;

public class AppointmentCommandParserTest {

    private static final String ADD_USAGE = AddAppointmentCommand.MESSAGE_USAGE;
    private static final String INVALID_ADD_FORMAT = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ADD_USAGE);
    private static final String INVALID_LIST_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListAppointmentsCommand.MESSAGE_USAGE);
    private static final LocalDate DATE = LocalDate.of(2026, 10, 26);
    private static final LocalTime TIME = LocalTime.of(9, 0);

    private final AppointmentCommandParser parser = new AppointmentCommandParser();

    @Test
    public void parse_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_addById_success() {
        AddAppointmentCommand expected = new AddAppointmentCommand(PersonSelector.byId(new PersonId(2)), DATE, TIME);
        assertParseSuccess(parser, " --add --id 2 --date 2026-10-26 --time 09:00", expected);
        assertParseSuccess(parser, " --add --id 2 --date \"2026-10-26\" --time \"09:00\"", expected);
        assertParseSuccess(parser, " --add --time 09:00 --date 2026-10-26 --id 2", expected);
    }

    @Test
    public void parse_addByName_success() {
        AddAppointmentCommand expected = new AddAppointmentCommand(PersonSelector.byName("Alice Pauline"), DATE, TIME);
        assertParseSuccess(parser, " --add --name Alice Pauline --date 2026-10-26 --time 09:00", expected);
        assertParseSuccess(parser, " --add --name \"Alice Pauline\" --date 2026-10-26 --time 09:00", expected);
    }

    @Test
    public void parse_addMissingTime_failure() {
        assertParseFailure(parser, " --add --id 2 --date 2026-10-26", INVALID_ADD_FORMAT);
    }

    @Test
    public void parse_addMissingDate_failure() {
        assertParseFailure(parser, " --add --id 2 --time 09:00", INVALID_ADD_FORMAT);
    }

    @Test
    public void parse_addMissingSelector_failure() {
        assertParseFailure(parser, " --add --date 2026-10-26 --time 09:00", INVALID_ADD_FORMAT);
    }

    @Test
    public void parse_addWithValue_failure() {
        assertParseFailure(parser, " --add x --id 2 --date 2026-10-26 --time 09:00", INVALID_ADD_FORMAT);
    }

    @Test
    public void parse_addInvalidDate_failure() {
        assertParseFailure(parser, " --add --id 2 --date 2026-02-30 --time 09:00",
                ParserUtil.withUsage(Appointment.MESSAGE_DATE_CONSTRAINTS, ADD_USAGE));
    }

    @Test
    public void parse_addYearBeyondFourDigits_failure() {
        assertParseFailure(parser, " --add --id 2 --date +999999999-12-31 --time 09:00",
                ParserUtil.withUsage(Appointment.MESSAGE_DATE_CONSTRAINTS, ADD_USAGE));
    }

    @Test
    public void parse_addInvalidTime_failure() {
        assertParseFailure(parser, " --add --id 2 --date 2026-10-26 --time 9:00",
                ParserUtil.withUsage(Appointment.MESSAGE_TIME_CONSTRAINTS, ADD_USAGE));
    }

    @Test
    public void parse_addDuplicateDate_failure() {
        assertParseFailure(parser, " --add --id 2 --date 2026-10-26 --date 2026-11-02 --time 09:00",
                ParserUtil.withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_DATE),
                        ADD_USAGE));
    }

    @Test
    public void parse_addRepeated_failure() {
        assertParseFailure(parser, " --add --add --id 2 --date 2026-10-26 --time 09:00",
                ParserUtil.withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ADD),
                        ADD_USAGE));
    }

    @Test
    public void parse_addWithWeek_failure() {
        assertParseFailure(parser, " --add --id 2 --date 2026-10-26 --time 09:00 --week 2026-10-26",
                INVALID_ADD_FORMAT);
    }

    @Test
    public void parse_date_success() {
        assertParseSuccess(parser, " --date 2026-10-26", ListAppointmentsCommand.forDay(DATE));
        assertParseSuccess(parser, " --date \"2026-10-26\"", ListAppointmentsCommand.forDay(DATE));
    }

    @Test
    public void parse_week_success() {
        assertParseSuccess(parser, " --week 2026-10-28", ListAppointmentsCommand.forWeek(DATE));
    }

    @Test
    public void parse_dateAndWeek_failure() {
        assertParseFailure(parser, " --date 2026-10-26 --week 2026-10-26", INVALID_LIST_FORMAT);
    }

    @Test
    public void parse_dateWithId_failure() {
        assertParseFailure(parser, " --date 2026-10-26 --id 2", INVALID_LIST_FORMAT);
        assertParseFailure(parser, " --week 2026-10-26 --name Alex", INVALID_LIST_FORMAT);
        assertParseFailure(parser, " --date 2026-10-26 --time 09:00", INVALID_LIST_FORMAT);
        assertParseFailure(parser, " today --date 2026-10-26", INVALID_LIST_FORMAT);
    }

    @Test
    public void parse_invalidWeekDate_failure() {
        assertParseFailure(parser, " --week 2026-13-01",
                ParserUtil.withUsage(Appointment.MESSAGE_DATE_CONSTRAINTS, ListAppointmentsCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " --date 22-10-2026",
                ParserUtil.withUsage(Appointment.MESSAGE_DATE_CONSTRAINTS, ListAppointmentsCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateDate_failure() {
        assertParseFailure(parser, " --date 2026-10-26 --date 2026-10-27",
                ParserUtil.withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_DATE),
                        ListAppointmentsCommand.MESSAGE_USAGE));
    }

    @Test
    public void usage_listsEveryForm() {
        assertTrue(AppointmentCommandParser.MESSAGE_USAGE.contains(AddAppointmentCommand.MESSAGE_USAGE));
        assertTrue(AppointmentCommandParser.MESSAGE_USAGE.contains(ListAppointmentsCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noForm_failure() {
        String invalidFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AppointmentCommandParser.MESSAGE_USAGE);
        assertParseFailure(parser, "", invalidFormat);
        assertParseFailure(parser, " --id 2 --time 09:00", invalidFormat);
        assertParseFailure(parser, " add --id 2 --time 09:00", invalidFormat);
    }
}
