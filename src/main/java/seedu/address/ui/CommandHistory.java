package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores up to 50 submitted commands in memory and navigates them without executing them.
 * Each instance has its own history. This class is intended for use on the UI thread.
 */
public class CommandHistory {
    private static final int MAX_ENTRIES = 50;

    private final List<String> commands = new ArrayList<>();
    // commands.size() represents the draft position, after the newest command.
    private int cursor;
    private String draft = "";

    /**
     * Records a command verbatim, ignoring blank input and consecutive identical entries.
     * Resets navigation even when the command is ignored.
     *
     * @throws NullPointerException if {@code commandText} is null.
     */
    public void record(String commandText) {
        requireNonNull(commandText);
        if (!commandText.isBlank()
                && (commands.isEmpty() || !commands.getLast().equals(commandText))) {
            if (commands.size() == MAX_ENTRIES) {
                commands.removeFirst();
            }
            commands.add(commandText);
        }
        resetNavigation();
    }

    /**
     * Returns the previous command, stopping at the oldest entry.
     * Saves {@code currentText} as the draft when browsing begins. With no history,
     * returns {@code currentText} unchanged.
     *
     * @throws NullPointerException if {@code currentText} is null.
     */
    public String previous(String currentText) {
        requireNonNull(currentText);
        if (commands.isEmpty()) {
            return currentText;
        }
        if (cursor == commands.size()) {
            draft = currentText;
        }
        if (cursor > 0) {
            cursor--;
        }
        return commands.get(cursor);
    }

    /**
     * Returns the next command, restoring the draft after the newest entry.
     * When not browsing, returns {@code currentText} unchanged.
     *
     * @throws NullPointerException if {@code currentText} is null.
     */
    public String next(String currentText) {
        requireNonNull(currentText);
        if (cursor == commands.size()) {
            return currentText;
        }
        cursor++;
        return cursor == commands.size() ? draft : commands.get(cursor);
    }

    /**
     * Ends browsing without changing stored commands. Call this after the user edits
     * recalled text; the next call to {@link #previous(String)} captures the new draft.
     */
    public void resetNavigation() {
        cursor = commands.size();
        draft = "";
    }
}
