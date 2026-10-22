package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;

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
            if (!summary.isAvailable()) {
                continue;
            }
            String example = summary.getExample();
            assertEquals(summary.getCommandWord(), example.split("\\s+", 2)[0]);
            assertTrue(summary.getSyntax().startsWith(summary.getCommandWord()));
            actualTypes.add(parser.parseCommand(example).getClass().getSimpleName());
        }
        assertEquals(expectedTypes, actualTypes);
    }

    @Test
    public void getCommands_upcomingForms_onlyListsToday() {
        List<CommandSummary> upcoming = HelpContent.getCommands().stream()
                .filter(summary -> !summary.isAvailable()).toList();
        assertEquals(List.of("list --today"), upcoming.stream().map(CommandSummary::getSyntax).toList());
        assertEquals("list --today", upcoming.getFirst().getExample());
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
