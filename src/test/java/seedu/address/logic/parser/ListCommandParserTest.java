package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ListCommandParserTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 12, 31);
    private final ListCommandParser parser = new ListCommandParser(
            Clock.fixed(Instant.parse("2026-12-31T12:00:00Z"), ZoneOffset.UTC));

    @Test
    public void parse_emptyArgs_restoresAllClients() throws Exception {
        for (String args : new String[]{"", " \t "}) {
            Model model = createModel();
            model.updateFilteredPersonList(person -> false);

            assertEquals("Listed 2 clients", parser.parse(args).execute(model).getFeedbackToUser());
            assertEquals(2, model.getFilteredPersonList().size());
        }
    }

    @Test
    public void parse_todayFlag_filtersClientsWithNonCancelledAppointments() throws Exception {
        for (String args : new String[]{"--today", " \t --today \t "}) {
            Model model = createModel();
            Person expectedClient = model.getFilteredPersonList().getFirst();

            assertEquals("Listed 1 client with appointments today",
                    parser.parse(args).execute(model).getFeedbackToUser());
            assertEquals(List.of(expectedClient), model.getFilteredPersonList());
        }
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        for (String args : new String[]{"3", "--todai", "--Today", "--today tomorrow",
            "extra --today", "--today --today", "--today=true", "\"--today\"", "--today --unknown"}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommandParser(null));
    }

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    private Model createModel() {
        Person alice = new PersonBuilder(ALICE).withAppointments(
                new Appointment(TODAY, LocalTime.NOON)).build();
        Person benson = new PersonBuilder(BENSON).withAppointments(
                new Appointment(TODAY, LocalTime.NOON, true)).build();
        return new ModelManager(new AddressBookBuilder().withPerson(alice).withPerson(benson).build(),
                new UserPrefs());
    }
}
