package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;

/**
 * Checks that the help catalog describes supported commands with usable examples.
 */
public class HelpContentTest {

    @Test
    public void getCommands_currentParserCommands_coversEachCommandOnce() {
        Set<String> expectedCommands = Set.of("add", "view", "edit", "delete", "list", "find", "note", "plan",
                "active", "inactive", "tag", "clear", "help", "exit");
        Set<String> actualCommands = new HashSet<>();

        for (CommandSummary summary : HelpContent.getCommands()) {
            assertTrue(actualCommands.add(summary.getCommandWord()),
                    "Duplicate help entry: " + summary.getCommandWord());
        }

        assertEquals(expectedCommands, actualCommands);
    }

    @Test
    public void getCommands_examples_parseAsTheirAdvertisedCommands() throws Exception {
        AddressBookParser parser = new AddressBookParser();

        for (CommandSummary summary : HelpContent.getCommands()) {
            String example = summary.getExample();
            assertEquals(summary.getCommandWord(), example.split("\\s+", 2)[0]);
            assertTrue(summary.getSyntax().startsWith(summary.getCommandWord()));
            parser.parseCommand(example);
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
