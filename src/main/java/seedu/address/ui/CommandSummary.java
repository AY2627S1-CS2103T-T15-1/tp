package seedu.address.ui;

import static java.util.Objects.requireNonNull;

/**
 * Stores the display content for one command in the help window.
 * Instances are immutable so the shared help catalog cannot be changed by a view.
 */
public final class CommandSummary {
    private final String category;
    private final String commandWord;
    private final String description;
    private final String syntax;
    private final String example;
    private final boolean isAvailable;

    /**
     * Creates a command summary. All fields must be non-null.
     * The example is a complete input command, without an explanatory prefix.
     */
    public CommandSummary(String category, String commandWord, String description, String syntax, String example) {
        this(category, commandWord, description, syntax, example, true);
    }

    /**
     * Creates a summary with an availability flag. Unavailable commands are displayed as coming soon.
     * All text fields must be non-null.
     */
    public CommandSummary(String category, String commandWord, String description, String syntax, String example,
            boolean isAvailable) {
        this.category = requireNonNull(category);
        this.commandWord = requireNonNull(commandWord);
        this.description = requireNonNull(description);
        this.syntax = requireNonNull(syntax);
        this.example = requireNonNull(example);
        this.isAvailable = isAvailable;
    }

    public String getCategory() {
        return category;
    }

    public String getCommandWord() {
        return commandWord;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getExample() {
        return example;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
