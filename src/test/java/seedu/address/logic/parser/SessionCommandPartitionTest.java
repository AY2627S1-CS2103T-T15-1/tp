package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.LongFormText;
import seedu.address.model.person.PersonId;

/**
 * Equivalence partitions and boundaries for both session actions, tested through the public command parser.
 */
public class SessionCommandPartitionTest {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);
    private static final PersonSelector SELECTOR = PersonSelector.byId(new PersonId(1));
    private final AddressBookParser parser = new AddressBookParser();

    @TestFactory
    public List<DynamicTest> parse_datePartitions() {
        List<DynamicTest> tests = new ArrayList<>();
        List<String> valid = List.of("0001-01-01", "1900-02-28", "2000-02-29", "2024-02-29", "2026-01-31",
                "2026-04-30", "2026-10-29", "2026-12-31", "2027-01-01", "9999-12-31");
        List<String> invalid = List.of("", "\"\"", "2026-00-01", "2026-13-01", "2026-01-00", "2026-01-32",
                "2026-04-31", "1900-02-29", "2100-02-29", "2026-02-29", "2026-02-30", "2026-2-01",
                "2026-02-1", "26-02-01", "10000-01-01", "+10000-01-01", "-0001-01-01", "2026/10/21",
                "21-10-2026", "2026-10-21T12:00", "tomorrow", "2026-10-21 trailing");
        for (boolean edit : List.of(false, true)) {
            for (String date : valid) {
                tests.add(dynamicTest(action(edit) + " accepts " + date, () -> {
                    for (String typedDate : List.of(date, "\"" + date + "\"")) {
                        assertEquals(expected(edit, LocalDate.parse(date), "Note"), parser.parseCommand(
                                "session " + action(edit) + " --id 1 --date " + typedDate + " --note \"Note\""));
                    }
                }));
            }
            for (String date : invalid) {
                tests.add(rejected(action(edit) + " invalid date " + date,
                        "session " + action(edit) + " --id 1 --date " + date + " --note \"Note\""));
            }
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> parse_invalidActionSelectorAndNotePartitions() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String action : List.of("", "--add --edit", "--add --add", "--edit --edit", "--delete",
                "--address", "--add unexpected", "--edit unexpected")) {
            tests.add(rejected("invalid action " + action,
                    "session " + action + " --id 1 --date 2026-10-21 --note \"Note\""));
        }
        for (boolean edit : List.of(false, true)) {
            for (String selector : List.of("", "--id", "--id 0", "--id -1", "--id 1.5", "--id abc",
                    "--id 2147483648", "--id 1 --id 2", "--name", "--name \"\"", "--name \"   \"",
                    "--name Alice --name Bob", "--id 1 --name Alice", "--phone 91234567", "unexpected --id 1")) {
                tests.add(rejected(action(edit) + " invalid selector " + selector,
                        "session " + action(edit) + " " + selector + " --date 2026-10-21 --note \"Note\""));
            }
            for (String note : List.of("", "Note", "\"Note", "Note\"", "\"", "\"Note\" trailing")) {
                tests.add(rejected(action(edit) + " invalid note " + note,
                        "session " + action(edit) + " --id 1 --date 2026-10-21 --note " + note));
            }
            tests.add(rejected(action(edit) + " missing date",
                    "session " + action(edit) + " --id 1 --note \"Note\""));
            tests.add(rejected(action(edit) + " duplicate date",
                    "session " + action(edit) + " --id 1 --date 2026-10-21 --date 2026-10-22 --note \"Note\""));
            tests.add(rejected(action(edit) + " duplicate note",
                    "session " + action(edit) + " --id 1 --date 2026-10-21 --note \"One\" --note \"Two\""));
        }
        tests.add(rejected("edit requires note", "session --edit --id 1 --date 2026-10-21"));
        tests.add(dynamicTest("add permits omitted note", () -> assertEquals(expected(false, DATE, ""),
                parser.parseCommand("session --add --id 1 --date 2026-10-21"))));
        return tests;
    }

    @TestFactory
    public List<DynamicTest> parse_noteContentPartitions() {
        List<DynamicTest> tests = new ArrayList<>();
        // The doubled-backslash case deliberately asserts the documented escape limitation.
        String[][] cases = {{"", ""}, {"   ", "   "}, {"\\n\\n", "\n\n"}, {"Squats", "Squats"},
            {"  Squats  ", "  Squats  "}, {"Squats\\nRest\\n\\nWalk", "Squats\nRest\n\nWalk"},
            {"Remember --id --name --date --note --add --edit", "Remember --id --name --date --note --add --edit"},
            {"Coach says \"steady\".", "Coach says \"steady\"."}, {"Rest\\t60s", "Rest\\t60s"},
            {"Rest\\\\nWalk", "Rest\\\nWalk"}, {"训练 💪 café", "训练 💪 café"},
            {"x".repeat(50_000), "x".repeat(50_000)}};
        for (boolean edit : List.of(false, true)) {
            for (int i = 0; i < cases.length; i++) {
                String[] note = cases[i];
                tests.add(dynamicTest(action(edit) + " note partition " + i, () -> assertEquals(
                        expected(edit, DATE, note[1]), parser.parseCommand(
                                "session " + action(edit) + " --id 1 --date 2026-10-21 --note \"" + note[0] + "\""))));
            }
        }
        return tests;
    }

    @TestFactory
    public List<DynamicTest> parse_argumentOrderPartitions() {
        List<DynamicTest> tests = new ArrayList<>();
        for (boolean edit : List.of(false, true)) {
            List<List<String>> orders = new ArrayList<>();
            permutations(List.of(action(edit), "--id 1", "--date 2026-10-21",
                    "--note \"Remember --add and --edit\\nRest\""), List.of(), orders);
            for (List<String> order : orders) {
                tests.add(dynamicTest(String.join(" ", order), () -> {
                    for (String separator : List.of(" ", "\t", "   ")) {
                        assertEquals(expected(edit, DATE, "Remember --add and --edit\nRest"),
                                parser.parseCommand("session " + String.join(separator, order)));
                    }
                }));
            }
        }
        return tests;
    }

    private DynamicTest rejected(String name, String input) {
        return dynamicTest(name, () -> assertThrows(ParseException.class, () -> parser.parseCommand(input)));
    }

    private static SessionCommand expected(boolean edit, LocalDate date, String note) {
        return edit ? new EditSessionCommand(SELECTOR, date, new LongFormText(note))
                : new AddSessionCommand(SELECTOR, date, new LongFormText(note));
    }

    private static String action(boolean edit) {
        return edit ? "--edit" : "--add";
    }

    private static void permutations(List<String> remaining, List<String> prefix, List<List<String>> result) {
        if (remaining.isEmpty()) {
            result.add(prefix);
            return;
        }
        for (int i = 0; i < remaining.size(); i++) {
            List<String> next = new ArrayList<>(prefix);
            next.add(remaining.get(i));
            List<String> rest = new ArrayList<>(remaining);
            rest.remove(i);
            permutations(rest, next, result);
        }
    }
}
