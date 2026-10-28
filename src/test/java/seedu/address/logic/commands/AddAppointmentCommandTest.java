package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Tests scheduling weekly appointments using the real model and client selection flow.
 */
public class AddAppointmentCommandTest {

    /** Today is Tue 2026-10-20 in these tests. */
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-20T04:00:00Z"), ZoneOffset.UTC);
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
    private static final LocalDate DATE = LocalDate.of(2026, 10, 26);
    private static final LocalTime TIME = LocalTime.of(9, 0);

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        assertThrows(NullPointerException.class, () -> new AddAppointmentCommand(null, DATE, TIME));
        assertThrows(NullPointerException.class, () -> new AddAppointmentCommand(selector, null, TIME));
        assertThrows(NullPointerException.class, () -> new AddAppointmentCommand(selector, DATE, null));
        assertThrows(NullPointerException.class, () -> new AddAppointmentCommand(selector, DATE, TIME, null));
    }

    @Test
    public void execute_clientWithoutAppointments_schedulesFour() {
        assertAddSuccess(PersonSelector.byId(ALICE.getId()), ALICE);
    }

    @Test
    public void execute_firstDateToday_schedules() {
        assertAddSuccess(PersonSelector.byId(ALICE.getId()), ALICE, TODAY, "Tue 2026-10-20 09:00");
    }

    @Test
    public void execute_firstDateAYearAhead_schedules() {
        assertAddSuccess(PersonSelector.byId(ALICE.getId()), ALICE, LocalDate.of(2027, 10, 20),
                "Wed 2027-10-20 09:00");
    }

    @Test
    public void execute_firstDateMoreThanAYearAhead_throws() {
        AddAppointmentCommand nextDay = new AddAppointmentCommand(PersonSelector.byId(ALICE.getId()),
                LocalDate.of(2027, 10, 21), TIME, CLOCK);
        assertCommandFailure(nextDay, model,
                String.format(AddAppointmentCommand.MESSAGE_DATE_TOO_FAR, "2027-10-21", "2027-10-20"));

        AddAppointmentCommand lastSupportedDate = new AddAppointmentCommand(PersonSelector.byId(ALICE.getId()),
                LocalDate.of(9999, 12, 31), TIME, CLOCK);
        assertCommandFailure(lastSupportedDate, model,
                String.format(AddAppointmentCommand.MESSAGE_DATE_TOO_FAR, "9999-12-31", "2027-10-20"));
    }

    @Test
    public void execute_firstDateBeforeToday_throws() {
        AddAppointmentCommand yesterday = new AddAppointmentCommand(PersonSelector.byId(ALICE.getId()),
                TODAY.minusDays(1), TIME, CLOCK);
        assertCommandFailure(yesterday, model,
                String.format(AddAppointmentCommand.MESSAGE_DATE_IN_PAST, "2026-10-19", "2026-10-20"));

        AddAppointmentCommand longAgo = new AddAppointmentCommand(PersonSelector.byId(ALICE.getId()),
                LocalDate.of(2020, 1, 1), TIME, CLOCK);
        assertCommandFailure(longAgo, model,
                String.format(AddAppointmentCommand.MESSAGE_DATE_IN_PAST, "2020-01-01", "2026-10-20"));
    }

    @Test
    public void execute_clientWithAppointments_replacesThem() {
        Person withAppointments = ALICE.withAppointments(
                Appointment.weeklySeries(LocalDate.of(2026, 9, 1), LocalTime.of(18, 0)));
        model.setPerson(ALICE, withAppointments);
        assertAddSuccess(PersonSelector.byId(ALICE.getId()), withAppointments);
    }

    @Test
    public void execute_partialNameIgnoringCase_schedules() {
        assertAddSuccess(PersonSelector.byName("ALICE p"), ALICE);
    }

    @Test
    public void execute_clientHiddenByFilter_schedulesAndShowsAll() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertAddSuccess(PersonSelector.byId(CARL.getId()), CARL);
    }

    @Test
    public void execute_nameMatchesSeveral_listsClientsWithFullRetryCommand() {
        PersonSelector selector = PersonSelector.byName("meier");
        AddAppointmentCommand command = new AddAppointmentCommand(selector, DATE, TIME, CLOCK);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);
        String message = "2 clients have a name containing \"meier\". They are listed below.\n"
                + "Use --id to choose one of them, e.g. appointment --add --id 2 --date 2026-10-26 --time 09:00";

        assertCommandSuccess(command, model, message, expectedModel);
    }

    @Test
    public void execute_noMatch_throws() {
        AddAppointmentCommand byId = new AddAppointmentCommand(PersonSelector.byId(new PersonId(999)), DATE, TIME);
        assertCommandFailure(byId, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
        AddAppointmentCommand byName = new AddAppointmentCommand(PersonSelector.byName("Zack"), DATE, TIME);
        assertCommandFailure(byName, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_NAME, "Zack"));
    }

    @Test
    public void equals() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        AddAppointmentCommand command = new AddAppointmentCommand(selector, DATE, TIME, CLOCK);
        assertTrue(command.equals(command));
        assertEquals(command, new AddAppointmentCommand(PersonSelector.byId(ALICE.getId()), DATE, TIME));
        assertFalse(command.equals(new AddAppointmentCommand(PersonSelector.byId(BENSON.getId()), DATE, TIME)));
        assertFalse(command.equals(new AddAppointmentCommand(selector, DATE.plusDays(1), TIME)));
        assertFalse(command.equals(new AddAppointmentCommand(selector, DATE, TIME.plusHours(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand(false)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        AddAppointmentCommand command = new AddAppointmentCommand(selector, DATE, TIME, CLOCK);
        String expected = AddAppointmentCommand.class.getCanonicalName() + "{selector=" + selector
                + ", firstDate=" + DATE + ", time=" + TIME + "}";
        assertEquals(expected, command.toString());
    }

    private void assertAddSuccess(PersonSelector selector, Person target) {
        assertAddSuccess(selector, target, DATE, "Mon 2026-10-26 09:00");
    }

    /**
     * Asserts that scheduling from {@code firstDate} succeeds, showing {@code firstAppointment} as the first one.
     */
    private void assertAddSuccess(PersonSelector selector, Person target, LocalDate firstDate,
            String firstAppointment) {
        Person updated = target.withAppointments(Appointment.weeklySeries(firstDate, TIME));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(target, updated);
        String message = String.format(AddAppointmentCommand.MESSAGE_SUCCESS, Messages.format(updated),
                firstAppointment);

        assertCommandSuccess(new AddAppointmentCommand(selector, firstDate, TIME, CLOCK), model,
                new CommandResult(message, updated), expectedModel);
    }
}
