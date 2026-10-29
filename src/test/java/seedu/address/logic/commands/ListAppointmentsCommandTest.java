package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Tests listing appointments for a day or a week using the real model.
 */
public class ListAppointmentsCommandTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 19);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 10, 25);

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void forDay_nullDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ListAppointmentsCommand.forDay(null));
        assertThrows(NullPointerException.class, () -> ListAppointmentsCommand.forWeek(null));
    }

    @Test
    public void forWeek_sunday_coversPrecedingMonday() {
        assertEquals(ListAppointmentsCommand.forWeek(MONDAY), ListAppointmentsCommand.forWeek(SUNDAY));
        LocalDate thursday = LocalDate.of(2026, 10, 22);
        assertEquals(ListAppointmentsCommand.forWeek(MONDAY), ListAppointmentsCommand.forWeek(thursday));
        assertFalse(ListAppointmentsCommand.forWeek(MONDAY).equals(
                ListAppointmentsCommand.forWeek(SUNDAY.plusDays(1))));
        assertExecuteSuccess(ListAppointmentsCommand.forWeek(SUNDAY),
                "No appointments from Mon 2026-10-19 to Sun 2026-10-25.", Set.of());
    }

    @Test
    public void isSupportedWeek() {
        assertTrue(ListAppointmentsCommand.isSupportedWeek(MONDAY));
        // The first and last weeks with every day from 0000-01-01 to 9999-12-31
        assertTrue(ListAppointmentsCommand.isSupportedWeek(LocalDate.of(0, 1, 3)));
        assertTrue(ListAppointmentsCommand.isSupportedWeek(LocalDate.of(9999, 12, 26)));
        // Weeks that start in year -1 or end in year 10000
        assertFalse(ListAppointmentsCommand.isSupportedWeek(LocalDate.of(0, 1, 2)));
        assertFalse(ListAppointmentsCommand.isSupportedWeek(LocalDate.of(9999, 12, 27)));
        assertFalse(ListAppointmentsCommand.isSupportedWeek(LocalDate.of(9999, 12, 31)));
        assertThrows(NullPointerException.class, () -> ListAppointmentsCommand.isSupportedWeek(null));
    }

    @Test
    public void execute_day_listsInTimeOrderAndFiltersClients() {
        give(ALICE, new Appointment(MONDAY, LocalTime.of(10, 0)), new Appointment(SUNDAY, LocalTime.of(10, 0)));
        give(BENSON, new Appointment(MONDAY, LocalTime.of(9, 0)));
        give(CARL, new Appointment(MONDAY.plusDays(1), LocalTime.of(8, 0)));

        assertExecuteSuccess(ListAppointmentsCommand.forDay(MONDAY), "Appointments on Mon 2026-10-19:\n"
                + "09:00 Benson Meier (ID: 2)\n"
                + "10:00 Alice Pauline (ID: 1)", Set.of(ALICE.getId().value, BENSON.getId().value));
    }

    @Test
    public void execute_sameTime_ordersByClientId() {
        give(BENSON, new Appointment(MONDAY, LocalTime.of(9, 0)));
        give(ALICE, new Appointment(MONDAY, LocalTime.of(9, 0)));

        assertExecuteSuccess(ListAppointmentsCommand.forDay(MONDAY), "Appointments on Mon 2026-10-19:\n"
                + "09:00 Alice Pauline (ID: 1)\n"
                + "09:00 Benson Meier (ID: 2)", Set.of(ALICE.getId().value, BENSON.getId().value));
    }

    @Test
    public void execute_week_includesCancelledMarked() {
        give(ALICE, new Appointment(SUNDAY, LocalTime.of(18, 0)),
                new Appointment(SUNDAY.plusDays(1), LocalTime.of(18, 0)));
        give(BENSON, new Appointment(MONDAY, LocalTime.of(9, 0), true));
        give(CARL, new Appointment(MONDAY.minusDays(1), LocalTime.of(9, 0)));

        assertExecuteSuccess(ListAppointmentsCommand.forWeek(LocalDate.of(2026, 10, 22)),
                "Appointments from Mon 2026-10-19 to Sun 2026-10-25:\n"
                + "Mon 2026-10-19 09:00 Benson Meier (ID: 2) (Cancelled)\n"
                + "Sun 2026-10-25 18:00 Alice Pauline (ID: 1)", Set.of(ALICE.getId().value, BENSON.getId().value));
    }

    @Test
    public void execute_cancelledOnDay_marked() {
        give(ALICE, new Appointment(MONDAY, LocalTime.of(7, 30), true));

        assertExecuteSuccess(ListAppointmentsCommand.forDay(MONDAY), "Appointments on Mon 2026-10-19:\n"
                + "07:30 Alice Pauline (ID: 1) (Cancelled)", Set.of(ALICE.getId().value));
    }

    @Test
    public void execute_none_showsNoAppointmentsMessage() {
        give(ALICE, new Appointment(MONDAY.plusDays(1), LocalTime.of(10, 0)));

        assertExecuteSuccess(ListAppointmentsCommand.forDay(MONDAY), "No appointments on Mon 2026-10-19.", Set.of());
    }

    @Test
    public void equals() {
        ListAppointmentsCommand day = ListAppointmentsCommand.forDay(MONDAY);
        assertTrue(day.equals(day));
        assertEquals(day, ListAppointmentsCommand.forDay(MONDAY));
        assertFalse(day.equals(ListAppointmentsCommand.forDay(SUNDAY)));
        assertFalse(day.equals(ListAppointmentsCommand.forWeek(MONDAY)));
        assertFalse(day.equals(null));
        assertFalse(day.equals(new ClearCommand(false)));
    }

    @Test
    public void toStringMethod() {
        String expected = ListAppointmentsCommand.class.getCanonicalName() + "{start=" + MONDAY + ", end=" + SUNDAY
                + ", isWeek=true}";
        assertEquals(expected, ListAppointmentsCommand.forWeek(SUNDAY).toString());
    }

    private void give(Person person, Appointment... appointments) {
        Person current = model.getAddressBook().getPersonList().stream()
                .filter(p -> p.getId().equals(person.getId())).findFirst().orElseThrow();
        model.setPerson(current, current.withAppointments(List.of(appointments)));
    }

    private void assertExecuteSuccess(ListAppointmentsCommand command, String message, Set<Integer> shownIds) {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> shownIds.contains(person.getId().value));
        assertCommandSuccess(command, model, message, expectedModel);
    }
}
