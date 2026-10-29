package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.CliSyntax;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;

/**
 * Checks that the help catalog describes supported commands with usable examples.
 */
public class HelpContentTest {

    @Test
    public void getCommands_currentParserCommands_coversEveryCommandFamily() {
        Set<String> expectedCommands = Set.of("add", "view", "edit", "delete", "list", "find", "note", "plan",
                "active", "inactive", "tag", "group", "appointment", "session", "clear", "help", "exit");
        Set<String> actualCommands = new HashSet<>();

        for (CommandSummary summary : HelpContent.getCommands()) {
            actualCommands.add(summary.getCommandWord());
        }

        assertEquals(expectedCommands, actualCommands);
    }

    @Test
    public void getCommands_examples_parseAsTheirAdvertisedCommands() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        Set<String> expectedTypes = Set.of("AddCommand", "ViewCommand", "EditCommand", "DeleteCommand",
                "ListCommand", "FindCommand", "NoteCommand", "PlanCommand", "SetActiveCommand", "TagCommand",
                "GroupCommand", "MoveGroupCommand", "AddToGroupCommand", "UntagCommand", "DeleteGroupCommand",
                "AddAppointmentCommand", "CancelAppointmentCommand", "RescheduleAppointmentCommand",
                "ListAppointmentsCommand", "AddSessionCommand", "EditSessionCommand", "ClearCommand",
                "HelpCommand", "ExitCommand");
        Set<String> actualTypes = new HashSet<>();
        Set<String> formats = new HashSet<>();

        for (CommandSummary summary : HelpContent.getCommands()) {
            assertTrue(formats.add(summary.getSyntax()), "Duplicate command format: " + summary.getSyntax());
            String example = summary.getExample();
            assertEquals(summary.getCommandWord(), example.split("\\s+", 2)[0]);
            assertTrue(summary.getSyntax().startsWith(summary.getCommandWord()));
            actualTypes.add(parser.parseCommand(example).getClass().getSimpleName());
        }
        assertEquals(expectedTypes, actualTypes);
    }

    @Test
    public void getCommands_statusAndGroupExamples_executeOnFreshSampleData() {
        AddressBookParser parser = new AddressBookParser();
        Set<String> commandWords = Set.of("active", "inactive", "group", "tag", "find");

        for (CommandSummary summary : HelpContent.getCommands()) {
            if (!commandWords.contains(summary.getCommandWord())) {
                continue;
            }
            Model model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
            assertDoesNotThrow(() -> parser.parseCommand(summary.getExample()).execute(model),
                    "Example must work on fresh sample data: " + summary.getExample());
            if (summary.getCommandWord().equals("find")) {
                assertFalse(model.getFilteredPersonList().isEmpty(), "Search example must find a sample client");
            }
            if (summary.getCommandWord().equals("active")) {
                assertTrue(model.getAddressBook().getPersonList().stream().allMatch(person -> person.isActive()),
                        "Activation example must reactivate the inactive sample client");
            }
        }
    }

    @Test
    public void getCommands_flags_matchSharedPrefixes() throws Exception {
        Set<String> sharedPrefixes = new HashSet<>();
        for (Field field : CliSyntax.class.getFields()) {
            if (field.getType().equals(Prefix.class)) {
                sharedPrefixes.add(((Prefix) field.get(null)).getPrefix());
            }
        }
        Pattern flag = Pattern.compile("--[a-z]+(?:-[a-z]+)*");
        for (CommandSummary summary : HelpContent.getCommands()) {
            for (String text : List.of(summary.getSyntax(), summary.getExample(), summary.getDescription())) {
                Matcher matcher = flag.matcher(text);
                while (matcher.find()) {
                    assertTrue(sharedPrefixes.contains(matcher.group()),
                            "Help uses an unrecognised flag: " + matcher.group());
                }
            }
        }
    }

    @Test
    public void getCommands_displayContent_hasNoBlankFieldsAndKeepsCategoriesTogether() {
        Set<String> completedCategories = new HashSet<>();
        String previousCategory = "";

        for (CommandSummary summary : HelpContent.getCommands()) {
            assertFalse(summary.getCategory().isBlank());
            assertFalse(summary.getDescription().isBlank());
            assertFalse(summary.getSyntax().isBlank());
            assertFalse(summary.getExample().isBlank());
            if (!summary.getCategory().equals(previousCategory)) {
                assertFalse(completedCategories.contains(summary.getCategory()),
                        "Category appears in separate sections: " + summary.getCategory());
                completedCategories.add(previousCategory);
                previousCategory = summary.getCategory();
            }
        }
    }

    @Test
    public void getCommands_attemptToRemoveEntry_preservesCatalog() {
        assertThrows(UnsupportedOperationException.class, () -> HelpContent.getCommands().removeFirst());
    }
}
