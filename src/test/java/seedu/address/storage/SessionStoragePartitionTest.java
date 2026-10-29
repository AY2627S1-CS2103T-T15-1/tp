package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.LongFormText;
import seedu.address.model.person.Person;
import seedu.address.model.session.Session;
import seedu.address.testutil.PersonBuilder;

/**
 * JSON equivalence partitions for legacy defaults, invalid records and exact content preservation.
 */
public class SessionStoragePartitionTest {
    private static final String CLIENT = "\"id\":1,\"name\":\"Alice\",\"phone\":\"91234567\"";
    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);

    @TestFactory
    public List<DynamicTest> toModelType_legacyPartitions_defaultsToEmpty() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String suffix : List.of("", ",\"sessions\":null", ",\"sessions\":[]")) {
            tests.add(dynamicTest("legacy sessions " + suffix, () -> assertEquals(List.of(),
                    JsonUtil.fromJsonString("{" + CLIENT + suffix + "}", JsonAdaptedPerson.class)
                            .toModelType().getSessions())));
        }
        for (String suffix : List.of("", ",\"note\":null", ",\"note\":\"\"")) {
            tests.add(dynamicTest("legacy note " + suffix, () -> {
                String json = "{" + CLIENT + ",\"sessions\":[{\"date\":\"2026-10-21\"" + suffix + "}]}";
                assertEquals(List.of(new Session(DATE, new LongFormText(""))),
                        JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType().getSessions());
            }));
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> toModelType_invalidSessionPartitions_rejectsCorruptData() {
        List<DynamicTest> tests = new ArrayList<>();
        List<String> records = List.of("null", "{}", "{\"date\":null}", "{\"date\":\"\"}",
                "{\"date\":\"2026-02-30\"}", "{\"date\":\"1900-02-29\"}",
                "{\"date\":\"2026-1-01\"}", "{\"date\":\"+10000-01-01\"}",
                "{\"date\":\"-0001-01-01\"}",
                "{\"date\":\"2026-10-21\",\"note\":\"First\"},{\"date\":\"2026-10-21\",\"note\":\"Second\"}");
        for (String record : records) {
            tests.add(dynamicTest("invalid record " + record, () -> {
                JsonAdaptedPerson person = JsonUtil.fromJsonString(
                        "{" + CLIENT + ",\"sessions\":[" + record + "]}", JsonAdaptedPerson.class);
                assertThrows(IllegalValueException.class, person::toModelType);
            }));
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> roundTrip_notePartitions_preservesExactSavedContents() {
        List<DynamicTest> tests = new ArrayList<>();
        List<String> notes = List.of("", " \t\n ", "Squats\nRest\n\nWalk", "Legacy literal \\n and \\t",
                "C:\\notes\\file.txt", "Coach says \"remember --date\"", "训练 💪 café", "x".repeat(50_000));
        for (int i = 0; i < notes.size(); i++) {
            String note = notes.get(i);
            tests.add(dynamicTest("stored note partition " + i, () -> {
                AddressBook original = new AddressBook();
                original.addPerson(new PersonBuilder().withSessions(new Session(DATE, new LongFormText(note))).build());
                String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(original));
                AddressBook loaded = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class).toModelType();
                assertEquals(original, loaded);
                assertEquals(note, loaded.getPersonList().getFirst().getSessions().getFirst().getNote().value);
            }));
        }
        for (int count : List.of(0, 1, 3, 4, 5, 100)) {
            tests.add(dynamicTest("stored history size " + count, () -> {
                List<Session> sessions = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    sessions.add(new Session(DATE.minusDays(i), new LongFormText("Note " + i)));
                }
                Person person = new PersonBuilder().withSessions(sessions.toArray(Session[]::new)).build();
                String json = JsonUtil.toJsonString(new JsonAdaptedPerson(person));
                assertEquals(person, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
            }));
        }
        return tests;
    }
}
