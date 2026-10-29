package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.LongFormText;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;
import seedu.address.model.session.Session;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Session equivalence partitions through the real parser, model, logic and JSON storage.
 * Each case uses an isolated data file, including failures followed by a later successful save.
 */
public class SessionIntegrationTest {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);

    @TempDir
    public Path testFolder;

    @TestFactory
    public List<DynamicTest> execute_clientAndHistoryPartitions_preservesAllUnchangedData() {
        List<DynamicTest> tests = new ArrayList<>();
        for (boolean edit : List.of(false, true)) {
            for (boolean active : List.of(false, true)) {
                for (int historySize : List.of(0, 4, 8)) {
                    for (String selector : List.of("--id 1", "--id \"1\"", "--name \"ALICE p\"",
                            "--name pAuLiNe")) {
                        tests.add(dynamicTest(action(edit) + " " + selector + " active=" + active
                                + " history=" + historySize, () -> {
                                    Fixture fixture = fixture(edit, active, historySize);
                                    fixture.model().updateFilteredPersonList(person -> false);
                                    Person original = fixture.model().getAddressBook().getPersonList().getFirst();
                                    AddressBook expected = new AddressBook(fixture.model().getAddressBook());
                                    List<Session> sessions = new ArrayList<>(original.getSessions());
                                    Session replacement = new Session(DATE, new LongFormText("  Squats\n\nRest  "));
                                    if (edit) {
                                        sessions.set(historySize / 2, replacement);
                                    } else {
                                        sessions.add(replacement);
                                    }
                                    Person updated = original.withSessions(sessions);
                                    expected.setPerson(original, updated);

                                    CommandResult result = fixture.logic().execute("session " + action(edit) + " "
                                            + selector + " --date " + DATE + " --note \"  Squats\\n\\nRest  \"");

                                    assertEquals(updated, result.getPersonToView().orElseThrow());
                                    assertEquals(expected, fixture.model().getAddressBook());
                                    assertEquals(expected, fixture.storage().readAddressBook().orElseThrow());
                                    assertEquals(2, fixture.model().getFilteredPersonList().size());
                                }));
                    }
                }
            }
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> execute_ambiguousNames_retryChangesOnlyChosenClient() {
        List<DynamicTest> tests = new ArrayList<>();
        for (boolean edit : List.of(false, true)) {
            for (String typedNote : List.of("", "   ", "Squats\\nRest\\n\\nWalk", "Remember --add and --edit")) {
                tests.add(dynamicTest(action(edit) + " ambiguous name, note=" + typedNote, () -> {
                    Fixture fixture = fixture(edit, false, 4);
                    AddressBook original = new AddressBook(fixture.model().getAddressBook());
                    CommandResult result = fixture.logic().execute("session " + action(edit)
                            + " --name Alice --date " + DATE + " --note \"" + typedNote + "\"");
                    assertTrue(result.getPersonToView().isEmpty());
                    assertEquals(original, fixture.model().getAddressBook());
                    assertEquals(original, fixture.storage().readAddressBook().orElseThrow());
                    String retry = result.getFeedbackToUser().split("e.g. ", 2)[1];
                    assertFalse(retry.contains("\n"));

                    fixture.logic().execute(retry);

                    Person updated = fixture.model().getAddressBook().getPersonList().getFirst();
                    assertEquals(typedNote.replace("\\n", "\n"), updated.getSessions().stream()
                            .filter(session -> session.getDate().equals(DATE))
                            .findFirst().orElseThrow().getNote().value);
                    assertEquals(original.getPersonList().get(1),
                            fixture.model().getAddressBook().getPersonList().get(1));
                    assertEquals(fixture.model().getAddressBook(),
                            fixture.storage().readAddressBook().orElseThrow());
                }));
            }
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> execute_invalidCommands_doNotMutateOrSave() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String input : List.of("session --add --id 1 --date 2026-02-30",
                "session --edit --id 1 --date 2026-10-21 --note",
                "session --add --id 1 --date 2026-10-21 --note Unquoted")) {
            tests.add(dynamicTest(input, () -> {
                Fixture fixture = fixture(true, true, 4);
                AddressBook original = new AddressBook(fixture.model().getAddressBook());
                fixture.storage().failure = new IOException("Saving must not be attempted");
                assertThrows(ParseException.class, () -> fixture.logic().execute(input));
                assertEquals(original, fixture.model().getAddressBook());
                assertEquals(original, fixture.storage().readAddressBook().orElseThrow());
            }));
        }
        for (String input : List.of("session --add --id 1 --date 2026-10-21",
                "session --edit --id 1 --date 2026-10-22 --note \"Missing date\"",
                "session --add --id 99 --date 2026-10-21",
                "session --edit --name Nobody --date 2026-10-21 --note \"Missing client\"")) {
            tests.add(dynamicTest(input, () -> {
                Fixture fixture = fixture(true, true, 4);
                AddressBook original = new AddressBook(fixture.model().getAddressBook());
                assertThrows(CommandException.class, () -> fixture.logic().execute(input));
                assertEquals(original, fixture.model().getAddressBook());
                assertEquals(original, fixture.storage().readAddressBook().orElseThrow());
            }));
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> execute_saveFailurePartitions_rollsBackBeforeNextSuccessfulSave() {
        List<DynamicTest> tests = new ArrayList<>();
        for (boolean edit : List.of(false, true)) {
            for (IOException failure : List.of(new IOException("Disk full"), new AccessDeniedException("cindy.json"))) {
                for (String note : List.of("", "Changed\\nNote")) {
                    tests.add(dynamicTest(action(edit) + " " + failure.getClass().getSimpleName() + " " + note, () -> {
                        Fixture fixture = fixture(edit, false, 4);
                        AddressBook original = new AddressBook(fixture.model().getAddressBook());
                        fixture.storage().failure = failure;
                        assertThrows(CommandException.class, () -> fixture.logic().execute("session " + action(edit)
                                + " --id 1 --date " + DATE + " --note \"" + note + "\""));
                        assertEquals(original, fixture.model().getAddressBook());
                        assertEquals(original, fixture.storage().readAddressBook().orElseThrow());

                        fixture.storage().failure = null;
                        fixture.logic().execute("group --name recovery");
                        original.addGroup(new Tag("recovery"));
                        assertEquals(original, fixture.model().getAddressBook());
                        assertEquals(original, fixture.storage().readAddressBook().orElseThrow());
                    }));
                }
            }
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> execute_otherClientCommands_preservesSessionHistory() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String command : List.of("edit --id 1 --to-name Alice Updated --to-phone 81234567",
                "note --id 1 --note \"Updated client note\"", "plan --id 1 --plan \"New plan\\nRest\"",
                "tag --id 1 --type personal",
                "appointment --add --id 1 --date " + LocalDate.now().plusDays(7) + " --time 09:00")) {
            tests.add(dynamicTest(command, () -> {
                Fixture fixture = fixture(true, true, 8);
                List<Session> original = fixture.model().getAddressBook().getPersonList().getFirst().getSessions();
                fixture.logic().execute(command);
                assertEquals(original, fixture.model().getAddressBook().getPersonList().getFirst().getSessions());
                assertEquals(original, fixture.storage().readAddressBook().orElseThrow()
                        .getPersonList().getFirst().getSessions());
            }));
        }
        return tests;
    }

    private Fixture fixture(boolean existingSession, boolean active, int historySize) throws Exception {
        Path folder = Files.createTempDirectory(testFolder, "session-");
        FailingStorage storage = new FailingStorage(folder.resolve("cindy.json"));
        List<Session> history = new ArrayList<>();
        for (int i = 0; i < historySize; i++) {
            history.add(new Session(DATE.minusDays(i + 1), new LongFormText("Old note " + i)));
        }
        if (existingSession) {
            history.add(historySize / 2, new Session(DATE, new LongFormText("Original note")));
        }
        Person target = new PersonBuilder(ALICE).withActive(active).withNote("Client note")
                .withWorkoutPlan("Plan\nRest")
                .withTags("gym", "morning").withAppointments(new Appointment(DATE, LocalTime.NOON))
                .withSessions(history.toArray(Session[]::new)).build();
        Person other = new PersonBuilder(BENSON).withName("Alice Other")
                .withSessions(new Session(DATE, new LongFormText("Other client's note"))).build();
        Model model = new ModelManager();
        model.addPerson(target);
        model.addPerson(other);
        model.addGroup(new Tag("empty-group"));
        storage.saveAddressBook(model.getAddressBook());
        return new Fixture(model, storage, new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(folder.resolve("preferences.json")))));
    }

    private static String action(boolean edit) {
        return edit ? "--edit" : "--add";
    }

    private record Fixture(Model model, FailingStorage storage, LogicManager logic) { }

    private static class FailingStorage extends JsonAddressBookStorage {
        private IOException failure;

        FailingStorage(Path path) {
            super(path);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
            if (failure != null) {
                throw failure;
            }
            super.saveAddressBook(data);
        }
    }
}
