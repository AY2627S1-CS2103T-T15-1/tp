package seedu.address.logic.commands;

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
import seedu.address.model.person.PersonId;

/**
 * Tests cancelling one appointment using the real model and client selection flow.
 */
public class CancelAppointmentCommandTest {

    private static final List<Appointment> SERIES =
            Appointment.weeklySeries(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0));

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CancelAppointmentCommand(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () ->
                new CancelAppointmentCommand(PersonSelector.byId(ALICE.getId()), null));
    }

    @Test
    public void execute_validIndex_marksCancelledAndKeepsOthers() {
        Person withSeries = giveSeries(ALICE);
        List<Appointment> expectedAppointments = new ArrayList<>(SERIES);
        expectedAppointments.set(1, SERIES.get(1).cancel());
        Person updated = withSeries.withAppointments(expectedAppointments);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(withSeries, updated);
        String message = String.format(CancelAppointmentCommand.MESSAGE_SUCCESS, 2, "Mon 2026-11-02 09:00",
                Messages.format(updated));

        assertCommandSuccess(new CancelAppointmentCommand(PersonSelector.byId(ALICE.getId()), Index.fromOneBased(2)),
                model, new CommandResult(message, updated), expectedModel);
    }

    @Test
    public void execute_alreadyCancelled_throws() {
        Person withSeries = giveSeries(ALICE);
        List<Appointment> appointments = new ArrayList<>(SERIES);
        appointments.set(0, SERIES.get(0).cancel());
        model.setPerson(withSeries, withSeries.withAppointments(appointments));

        assertCommandFailure(new CancelAppointmentCommand(PersonSelector.byId(ALICE.getId()), Index.fromOneBased(1)),
                model, String.format(CancelAppointmentCommand.MESSAGE_ALREADY_CANCELLED, 1));
    }

    @Test
    public void execute_indexTooLarge_throws() {
        giveSeries(ALICE);
        assertCommandFailure(new CancelAppointmentCommand(PersonSelector.byId(ALICE.getId()), Index.fromOneBased(5)),
                model, "This client has 4 appointment(s). Choose an index from 1 to 4.");
    }

    @Test
    public void execute_noAppointments_throws() {
        assertCommandFailure(new CancelAppointmentCommand(PersonSelector.byId(ALICE.getId()), Index.fromOneBased(1)),
                model, AppointmentIndexCommand.MESSAGE_NO_APPOINTMENTS);
    }

    @Test
    public void execute_unknownClient_throws() {
        CancelAppointmentCommand command =
                new CancelAppointmentCommand(PersonSelector.byId(new PersonId(999)), Index.fromOneBased(1));
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void execute_nameMatchesSeveral_retryHasCancelAndIndex() {
        PersonSelector selector = PersonSelector.byName("meier");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);
        String message = "2 clients have a name containing \"meier\". They are listed below.\n"
                + "Use --id to choose one of them, e.g. appointment --cancel --id 2 --index 3";

        assertCommandSuccess(new CancelAppointmentCommand(selector, Index.fromOneBased(3)), model, message,
                expectedModel);
    }

    @Test
    public void equals() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        CancelAppointmentCommand command = new CancelAppointmentCommand(selector, Index.fromOneBased(1));
        assertTrue(command.equals(command));
        assertEquals(command, new CancelAppointmentCommand(PersonSelector.byId(ALICE.getId()), Index.fromOneBased(1)));
        assertFalse(command.equals(new CancelAppointmentCommand(PersonSelector.byId(BENSON.getId()),
                Index.fromOneBased(1))));
        assertFalse(command.equals(new CancelAppointmentCommand(selector, Index.fromOneBased(2))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand(false)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        Index index = Index.fromOneBased(1);
        String expected = CancelAppointmentCommand.class.getCanonicalName() + "{selector=" + selector
                + ", index=" + index + "}";
        assertEquals(expected, new CancelAppointmentCommand(selector, index).toString());
    }

    private Person giveSeries(Person person) {
        Person withSeries = person.withAppointments(SERIES);
        model.setPerson(person, withSeries);
        return withSeries;
    }
}
