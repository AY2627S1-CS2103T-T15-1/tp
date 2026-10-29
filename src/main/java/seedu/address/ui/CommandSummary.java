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

    /**
     * Creates a command summary. All fields must be non-null.
     * The example is a complete input command, without an explanatory prefix.
     */
    public CommandSummary(String category, String commandWord, String description, String syntax, String example) {
        this.category = requireNonNull(category);
        this.commandWord = requireNonNull(commandWord);
        this.description = requireNonNull(description);
        this.syntax = requireNonNull(syntax);
        this.example = requireNonNull(example);
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
}
