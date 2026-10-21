package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command recall independently of JavaFX and command execution.
 */
public class CommandHistoryTest {

    @Test
    public void navigate_emptyHistory_preservesCurrentText() {
        CommandHistory history = new CommandHistory();
        assertEquals("unfinished", history.previous("unfinished"));
        assertEquals("unfinished", history.previous("unfinished"));
        assertEquals("unfinished", history.next("unfinished"));
    }

    @Test
    public void previous_multipleCommands_stopsAtOldest() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find Alice");

        assertEquals("find Alice", history.previous("draft"));
        assertEquals("list", history.previous("find Alice"));
        assertEquals("list", history.previous("list"));
    }

    @Test
    public void next_browsingHistory_restoresDraftAfterNewest() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find Alice");

        history.previous("unfinished command");
        history.previous("find Alice");
        assertEquals("find Alice", history.next("list"));
        assertEquals("unfinished command", history.next("find Alice"));
        assertEquals("unfinished command", history.next("unfinished command"));
        assertEquals("find Alice", history.previous("new draft"));
        assertEquals("new draft", history.next("find Alice"));
    }

    @Test
    public void next_notBrowsing_preservesCurrentText() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        assertEquals("draft", history.next("draft"));
        assertEquals("edited draft", history.next("edited draft"));
    }

    @Test
    public void record_blankCommands_ignoresEntries() {
        CommandHistory history = new CommandHistory();
        history.record("");
        history.record(" \t\n");
        assertEquals("draft", history.previous("draft"));

        history.record("list");
        history.record("   ");
        assertEquals("list", history.previous("draft"));
        assertEquals("list", history.previous("list"));
    }

    @Test
    public void record_consecutiveDuplicates_keepsOneEntry() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find Alice");
        history.record("find Alice");

        assertEquals("find Alice", history.previous(""));
        assertEquals("list", history.previous("find Alice"));
    }

    @Test
    public void record_nonConsecutiveDuplicates_keepsBothEntries() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find Alice");
        history.record("list");

        assertEquals("list", history.previous(""));
        assertEquals("find Alice", history.previous("list"));
        assertEquals("list", history.previous("find Alice"));
    }

    @Test
    public void record_commandWithWhitespace_preservesOriginalText() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("  list  ");

        assertEquals("  list  ", history.previous(""));
        assertEquals("list", history.previous("  list  "));
    }

    @Test
    public void record_moreThanFiftyCommands_evictsOldestEntries() {
        CommandHistory history = new CommandHistory();
        for (int i = 1; i <= 55; i++) {
            history.record("command " + i);
        }

        String currentText = "draft";
        for (int i = 55; i >= 6; i--) {
            currentText = history.previous(currentText);
            assertEquals("command " + i, currentText);
        }
        assertEquals("command 6", history.previous(currentText));
    }

    @Test
    public void record_whileBrowsing_restartsAtNewestCommand() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.previous("old draft");
        history.record("find Alice");

        assertEquals("find Alice", history.previous("new draft"));
        assertEquals("new draft", history.next("find Alice"));
    }

    @Test
    public void record_duplicateWhileBrowsing_resetsNavigation() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find Alice");
        history.previous("old draft");
        history.previous("find Alice");
        history.record("find Alice");

        assertEquals("find Alice", history.previous("new draft"));
        assertEquals("new draft", history.next("find Alice"));
    }

    @Test
    public void resetNavigation_afterEditing_keepsHistoryAndUsesNewDraft() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find Alice");
        history.previous("old draft");
        history.previous("find Alice");
        history.resetNavigation();

        assertEquals("edited command", history.next("edited command"));
        assertEquals("find Alice", history.previous("edited command"));
        assertEquals("edited command", history.next("find Alice"));
        assertEquals("find Alice", history.previous(""));
        assertEquals("", history.next("find Alice"));
    }

    @Test
    public void nullInput_throwsWithoutChangingNavigation() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.previous("draft");

        assertThrows(NullPointerException.class, () -> history.record(null));
        assertThrows(NullPointerException.class, () -> history.previous(null));
        assertThrows(NullPointerException.class, () -> history.next(null));
        assertEquals("draft", history.next("list"));
    }

    @Test
    public void newHistory_hasNoEntriesFromOtherInstances() {
        CommandHistory firstHistory = new CommandHistory();
        firstHistory.record("list");

        CommandHistory secondHistory = new CommandHistory();
        assertEquals("draft", secondHistory.previous("draft"));
    }
}
