package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Tests moving one appointment using the real model and client selection flow.
 */
public class RescheduleAppointmentCommandTest {

    private static final List<Appointment> SERIES =
            Appointment.weeklySeries(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0));
    private static final LocalDate NEW_DATE = LocalDate.of(2026, 11, 10);
    private static final LocalTime NEW_TIME = LocalTime.of(18, 0);

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        Index index = Index.fromOneBased(1);
        assertThrows(NullPointerException.class, () ->
                new RescheduleAppointmentCommand(null, index, NEW_DATE, NEW_TIME));
        assertThrows(NullPointerException.class, () ->
                new RescheduleAppointmentCommand(selector, null, NEW_DATE, NEW_TIME));
        assertThrows(NullPointerException.class, () ->
                new RescheduleAppointmentCommand(selector, index, null, NEW_TIME));
        assertThrows(NullPointerException.class, () ->
                new RescheduleAppointmentCommand(selector, index, NEW_DATE, null));
    }

    @Test
    public void execute_validIndex_movesAndResorts() {
        Person withSeries = giveSeries(ALICE, SERIES);
        Appointment moved = new Appointment(NEW_DATE, NEW_TIME);
        Person updated = withSeries.withAppointments(List.of(SERIES.get(1), SERIES.get(2), moved, SERIES.get(3)));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(withSeries, updated);
        String message = String.format(RescheduleAppointmentCommand.MESSAGE_SUCCESS, 1, Messages.format(updated),
                "Tue 2026-11-10 18:00");

        assertCommandSuccess(reschedule(1, NEW_DATE, NEW_TIME), model, new CommandResult(message, updated),
                expectedModel);
        assertEquals(List.of(SERIES.get(1), SERIES.get(2), moved, SERIES.get(3)),
                findClient(withSeries).getAppointments());
    }

    @Test
    public void execute_cancelledAppointment_becomesActive() {
        List<Appointment> appointments = new ArrayList<>(SERIES);
        appointments.set(1, SERIES.get(1).cancel());
        Person withSeries = giveSeries(ALICE, appointments);
        LocalDate tuesday = LocalDate.of(2026, 11, 3);

        Person updated = executeAndGetClient(reschedule(2, tuesday, LocalTime.of(9, 0)), withSeries);

        assertEquals(new Appointment(tuesday, LocalTime.of(9, 0)), updated.getAppointments().get(1));
        assertTrue(updated.getAppointments().stream().noneMatch(Appointment::isCancelled));
    }

    @Test
    public void execute_cancelledToSameTime_becomesActive() {
        List<Appointment> appointments = new ArrayList<>(SERIES);
        appointments.set(0, SERIES.get(0).cancel());
        Person withSeries = giveSeries(ALICE, appointments);

        Person updated = executeAndGetClient(reschedule(1, LocalDate.of(2026, 10, 26), LocalTime.of(9, 0)),
                withSeries);

        assertEquals(SERIES, updated.getAppointments());
    }

    @Test
    public void execute_clashWithOwnAppointment_throws() {
        giveSeries(ALICE, SERIES);
        assertCommandFailure(reschedule(1, LocalDate.of(2026, 11, 2), LocalTime.of(9, 0)), model,
                String.format(RescheduleAppointmentCommand.MESSAGE_CLASH, "Mon 2026-11-02 09:00"));
    }

    @Test
    public void execute_clashWithCancelledAppointment_throws() {
        List<Appointment> appointments = new ArrayList<>(SERIES);
        appointments.set(1, SERIES.get(1).cancel());
        giveSeries(ALICE, appointments);
        assertCommandFailure(reschedule(1, LocalDate.of(2026, 11, 2), LocalTime.of(9, 0)), model,
                String.format(RescheduleAppointmentCommand.MESSAGE_CLASH, "Mon 2026-11-02 09:00"));
    }

    @Test
    public void execute_indexTooLarge_throws() {
        giveSeries(ALICE, SERIES);
        assertCommandFailure(reschedule(5, NEW_DATE, NEW_TIME), model,
                "This client has 4 appointment(s). Choose an index from 1 to 4.");
    }

    @Test
    public void execute_noAppointments_throws() {
        assertCommandFailure(reschedule(1, NEW_DATE, NEW_TIME), model, AppointmentIndexCommand.MESSAGE_NO_APPOINTMENTS);
    }

    @Test
    public void execute_nameMatchesSeveral_retryHasFullArguments() {
        PersonSelector selector = PersonSelector.byName("meier");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);
        String message = "2 clients have a name containing \"meier\". They are listed below.\n"
                + "Use --id to choose one of them, e.g. "
                + "appointment --reschedule --id 2 --index 1 --date 2026-10-27 --time 18:00";

        assertCommandSuccess(new RescheduleAppointmentCommand(selector, Index.fromOneBased(1),
                LocalDate.of(2026, 10, 27), NEW_TIME), model, message, expectedModel);
    }

    @Test
    public void equals() {
        RescheduleAppointmentCommand command = reschedule(1, NEW_DATE, NEW_TIME);
        assertTrue(command.equals(command));
        assertEquals(command, reschedule(1, NEW_DATE, NEW_TIME));
        assertFalse(command.equals(new RescheduleAppointmentCommand(PersonSelector.byId(BENSON.getId()),
                Index.fromOneBased(1), NEW_DATE, NEW_TIME)));
        assertFalse(command.equals(reschedule(2, NEW_DATE, NEW_TIME)));
        assertFalse(command.equals(reschedule(1, NEW_DATE.plusDays(1), NEW_TIME)));
        assertFalse(command.equals(reschedule(1, NEW_DATE, NEW_TIME.plusHours(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand(false)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        Index index = Index.fromOneBased(1);
        String expected = RescheduleAppointmentCommand.class.getCanonicalName() + "{selector=" + selector
                + ", index=" + index + ", date=" + NEW_DATE + ", time=" + NEW_TIME + "}";
        assertEquals(expected, new RescheduleAppointmentCommand(selector, index, NEW_DATE, NEW_TIME).toString());
    }

    private RescheduleAppointmentCommand reschedule(int oneBasedIndex, LocalDate date, LocalTime time) {
        return new RescheduleAppointmentCommand(PersonSelector.byId(ALICE.getId()), Index.fromOneBased(oneBasedIndex),
                date, time);
    }

    private Person giveSeries(Person person, List<Appointment> appointments) {
        Person withSeries = person.withAppointments(appointments);
        model.setPerson(person, withSeries);
        return withSeries;
    }

    private Person executeAndGetClient(RescheduleAppointmentCommand command, Person before) {
        CommandResult result = assertDoesNotThrow(() -> command.execute(model));
        assertTrue(result.getFeedbackToUser().startsWith("Rescheduled appointment"));
        return findClient(before);
    }

    private Person findClient(Person client) {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getId().equals(client.getId()))
                .findFirst()
                .orElseThrow();
    }
}
