package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 12, 31);
    private static final Clock TODAY_CLOCK = Clock.fixed(Instant.parse("2026-12-31T12:00:00Z"), ZoneOffset.UTC);

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_emptyAddressBook_showsNoClientsMessage() {
        Model emptyModel = new ModelManager();
        Model expectedEmptyModel = new ModelManager();
        assertCommandSuccess(new ListCommand(), emptyModel, ListCommand.MESSAGE_NO_CLIENTS, expectedEmptyModel);
    }

    @Test
    public void execute_oneClient_showsSingularMessage() {
        Model singleModel = new ModelManager(new AddressBookBuilder().withPerson(ALICE).build(), new UserPrefs());
        Model expectedSingleModel = new ModelManager(singleModel.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new ListCommand(), singleModel, "Listed 1 client", expectedSingleModel);
    }

    @Test
    public void execute_listIsNotFiltered_showsPluralMessage() {
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS_PLURAL, getTypicalPersons().size());
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientsAddedOutOfOrder_showsClientsSortedByName() {
        Model unsortedModel = new ModelManager(
                new AddressBookBuilder().withPerson(CARL).withPerson(ALICE).withPerson(BENSON).build(),
                new UserPrefs());
        new ListCommand().execute(unsortedModel);
        assertEquals(List.of(ALICE, BENSON, CARL), unsortedModel.getFilteredPersonList());
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS_PLURAL, getTypicalPersons().size());
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_today_filtersByDateAndExcludesCancelledAppointments() {
        Person today = new PersonBuilder(ALICE).withAppointments(
                new Appointment(TODAY, LocalTime.MIDNIGHT),
                new Appointment(TODAY, LocalTime.of(23, 59))).build();
        Person cancelled = new PersonBuilder(BENSON).withAppointments(
                new Appointment(TODAY, LocalTime.NOON, true)).build();
        Person otherDays = new PersonBuilder(CARL).withAppointments(
                new Appointment(TODAY.minusDays(1), LocalTime.NOON),
                new Appointment(TODAY.plusDays(1), LocalTime.NOON)).build();
        Model todayModel = modelWithPersons(otherDays, cancelled, today);
        List<Person> originalClients = List.copyOf(todayModel.getAddressBook().getPersonList());

        CommandResult result = ListCommand.forToday(TODAY_CLOCK).execute(todayModel);

        assertEquals(List.of(today), todayModel.getFilteredPersonList());
        assertEquals(new CommandResult("Listed 1 client with appointments today:\n"
                + scheduleLine("00:00, 23:59", today)), result);
        assertEquals(originalClients, todayModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_today_replacesPreviousFilterAndSortsByEarliestTimeToday() {
        Person alice = new PersonBuilder(ALICE).withActive(false).withAppointments(
                new Appointment(TODAY, LocalTime.NOON, true),
                new Appointment(TODAY, LocalTime.of(18, 0))).build();
        Person benson = new PersonBuilder(BENSON).withAppointments(
                new Appointment(TODAY, LocalTime.of(9, 0))).build();
        Model todayModel = modelWithPersons(benson, CARL, alice);
        todayModel.updateFilteredPersonList(person -> person.equals(CARL));

        CommandResult result = ListCommand.forToday(TODAY_CLOCK).execute(todayModel);

        assertEquals(List.of(benson, alice), todayModel.getFilteredPersonList());
        assertEquals(new CommandResult("Listed 2 clients with appointments today:\n"
                + scheduleLine("09:00", benson) + "\n" + scheduleLine("18:00", alice)), result);
    }

    @Test
    public void execute_today_sortsByEarliestTimeIgnoringOtherDays() {
        Person alice = new PersonBuilder(ALICE).withAppointments(
                new Appointment(TODAY.minusDays(1), LocalTime.of(6, 0)),
                new Appointment(TODAY, LocalTime.of(23, 0))).build();
        Person benson = new PersonBuilder(BENSON).withAppointments(
                new Appointment(TODAY, LocalTime.of(19, 0)),
                new Appointment(TODAY, LocalTime.of(7, 0))).build();
        Person carl = new PersonBuilder(CARL).withAppointments(
                new Appointment(TODAY, LocalTime.of(7, 0))).build();
        Model todayModel = modelWithPersons(alice, carl, benson);

        CommandResult result = ListCommand.forToday(TODAY_CLOCK).execute(todayModel);

        // benson and carl both start at 07:00, so they are sorted by name
        assertEquals(List.of(benson, carl, alice), todayModel.getFilteredPersonList());
        assertEquals(new CommandResult("Listed 3 clients with appointments today:\n"
                + scheduleLine("07:00, 19:00", benson) + "\n" + scheduleLine("07:00", carl) + "\n"
                + scheduleLine("23:00", alice)), result);
    }

    @Test
    public void execute_allAfterToday_restoresAlphabeticalOrder() {
        Person alice = new PersonBuilder(ALICE).withAppointments(new Appointment(TODAY, LocalTime.of(20, 0))).build();
        Person benson = new PersonBuilder(BENSON).withAppointments(new Appointment(TODAY, LocalTime.of(8, 0))).build();
        Model todayModel = modelWithPersons(alice, benson);
        ListCommand.forToday(TODAY_CLOCK).execute(todayModel);
        assertEquals(List.of(benson, alice), todayModel.getFilteredPersonList());

        new ListCommand().execute(todayModel);

        assertEquals(List.of(alice, benson), todayModel.getFilteredPersonList());
    }

    @Test
    public void execute_todayWithoutMatches_showsNoAppointmentsMessage() {
        Person cancelled = new PersonBuilder(ALICE).withAppointments(
                new Appointment(TODAY, LocalTime.NOON, true)).build();
        Model todayModel = modelWithPersons(cancelled, BENSON);

        CommandResult result = ListCommand.forToday(TODAY_CLOCK).execute(todayModel);

        assertEquals(List.of(), todayModel.getFilteredPersonList());
        assertEquals(new CommandResult("No clients have appointments today."), result);
    }

    @Test
    public void execute_todayWithEmptyAddressBook_showsNoAppointmentsMessage() {
        assertCommandSuccess(ListCommand.forToday(TODAY_CLOCK), new ModelManager(),
                "No clients have appointments today.", new ModelManager());
    }

    @Test
    public void execute_allAfterToday_restoresAllClients() {
        Model todayModel = modelWithPersons(ALICE, BENSON);
        ListCommand.forToday(TODAY_CLOCK).execute(todayModel);

        CommandResult result = new ListCommand().execute(todayModel);

        assertEquals(List.of(ALICE, BENSON), todayModel.getFilteredPersonList());
        assertEquals(new CommandResult("Listed 2 clients"), result);
    }

    @Test
    public void execute_today_usesClockTimeZone() {
        Clock singaporeClock = Clock.fixed(Instant.parse("2026-12-30T16:00:00Z"), ZoneId.of("Asia/Singapore"));
        Person alice = new PersonBuilder(ALICE).withAppointments(new Appointment(TODAY, LocalTime.NOON)).build();
        Model todayModel = modelWithPersons(alice);

        ListCommand.forToday(singaporeClock).execute(todayModel);

        assertEquals(List.of(alice), todayModel.getFilteredPersonList());
    }

    @Test
    public void execute_today_resolvesDateOncePerExecution() {
        AtomicReference<Instant> nextInstant = new AtomicReference<>(Instant.parse("2026-12-30T23:59:59Z"));
        Clock advancingClock = new Clock() {
            @Override
            public ZoneId getZone() {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return Clock.fixed(nextInstant.get(), zone);
            }

            @Override
            public Instant instant() {
                return nextInstant.getAndUpdate(instant -> instant.plusSeconds(1));
            }
        };
        ListCommand command = ListCommand.forToday(advancingClock);
        nextInstant.set(Instant.parse("2026-12-31T23:59:59Z"));
        Person alice = new PersonBuilder(ALICE).withAppointments(new Appointment(TODAY, LocalTime.NOON)).build();
        Person benson = new PersonBuilder(BENSON).withAppointments(new Appointment(TODAY, LocalTime.NOON)).build();
        Person carl = new PersonBuilder(CARL).withAppointments(
                new Appointment(TODAY.plusDays(1), LocalTime.NOON)).build();
        Model todayModel = modelWithPersons(alice, benson, carl);

        command.execute(todayModel);
        assertEquals(List.of(alice, benson), todayModel.getFilteredPersonList());

        command.execute(todayModel);
        assertEquals(List.of(carl), todayModel.getFilteredPersonList());
    }

    @Test
    public void forToday_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ListCommand.forToday(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand().execute(null));
        assertThrows(NullPointerException.class, () -> ListCommand.forToday(TODAY_CLOCK).execute(null));
    }

    private static String scheduleLine(String times, Person person) {
        return times + " " + person.getName() + " (ID: " + person.getId() + ")";
    }

    private Model modelWithPersons(Person... persons) {
        AddressBookBuilder builder = new AddressBookBuilder();
        for (Person person : persons) {
            builder.withPerson(person);
        }
        return new ModelManager(builder.build(), new UserPrefs());
    }
}
