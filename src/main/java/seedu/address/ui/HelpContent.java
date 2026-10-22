package seedu.address.ui;

import java.util.List;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.PlanCommand;
import seedu.address.logic.commands.SetActiveCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.ViewCommand;

/**
 * Provides the command reference for the help window, independently of JavaFX controls.
 * Add entries when commands become available in the application parser.
 */
public final class HelpContent {
    private static final String CLIENT_DETAILS = "Client details";
    private static final String FINDING_CLIENTS = "Finding clients";
    private static final String TRAINING_INFORMATION = "Training information";
    private static final String CLIENT_STATUS = "Client status";
    private static final String CLIENT_TYPE = "Client type";
    private static final String APPLICATION = "Application";

    private static final String CLIENT_SELECTOR = "(--id ID | --name NAME)";
    private static final String STATUS_SELECTOR = "(--id ID | --name NAME | --phone PHONE)";

    /**
     * Entries are ordered by category, then by their intended display order within that category.
     */
    private static final List<CommandSummary> COMMANDS = List.of(
            new CommandSummary(CLIENT_DETAILS, AddCommand.COMMAND_WORD,
                    "Add a client with a name and phone number.",
                    AddCommand.COMMAND_WORD + " --name \"NAME\" --phone \"PHONE\"",
                    "add --name \"James Ho\" --phone \"+65 9123 4567\""),
            new CommandSummary(CLIENT_DETAILS, ViewCommand.COMMAND_WORD,
                    "Open a client's profile.",
                    ViewCommand.COMMAND_WORD + " " + CLIENT_SELECTOR,
                    "view --id 2"),
            new CommandSummary(CLIENT_DETAILS, EditCommand.COMMAND_WORD,
                    "Change a client's name or phone number. Provide at least one field to change.",
                    EditCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " [--to-name NEW_NAME] [--to-phone NEW_PHONE]",
                    "edit --id 2 --to-name \"James Lee\""),
            new CommandSummary(CLIENT_DETAILS, DeleteCommand.COMMAND_WORD,
                    "Permanently delete a client and their saved details.",
                    DeleteCommand.COMMAND_WORD + " " + CLIENT_SELECTOR,
                    "delete --id 3"),
            new CommandSummary(FINDING_CLIENTS, ListCommand.COMMAND_WORD,
                    "Show all clients.",
                    ListCommand.COMMAND_WORD,
                    "list"),
            new CommandSummary(FINDING_CLIENTS, FindCommand.COMMAND_WORD,
                    "Find clients using at least one filter. Clients must match every supplied filter.",
                    FindCommand.COMMAND_WORD + " [--name NAME] [--phone PHONE] [--id ID]",
                    "find --name james --phone 9123"),
            new CommandSummary(TRAINING_INFORMATION, NoteCommand.COMMAND_WORD,
                    "Replace a client's note. Double quotes are required; "
                            + "use \"\" to clear it and \\n for line breaks.",
                    NoteCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " --note \"TEXT\"",
                    "note --id 3 --note \"Knee injury\\nAvoid lunges\""),
            new CommandSummary(TRAINING_INFORMATION, PlanCommand.COMMAND_WORD,
                    "Replace a workout plan. Double quotes are required; use \"\" to clear it and \\n for line breaks.",
                    PlanCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " --plan \"TEXT\"",
                    "plan --id 2 --plan \"Squats: 3 sets of 12\""),
            new CommandSummary(CLIENT_STATUS, SetActiveCommand.COMMAND_WORD_ACTIVE,
                    "Mark a client as active again.",
                    SetActiveCommand.COMMAND_WORD_ACTIVE + " " + STATUS_SELECTOR,
                    "active --phone 9123"),
            new CommandSummary(CLIENT_STATUS, SetActiveCommand.COMMAND_WORD_INACTIVE,
                    "Mark a client as inactive while keeping their saved details.",
                    SetActiveCommand.COMMAND_WORD_INACTIVE + " " + STATUS_SELECTOR,
                    "inactive --id 4"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Set a client's type to gym or personal, replacing their previous type.",
                    TagCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " --type (gym | personal)",
                    "tag --id 2 --type gym"),
            new CommandSummary(APPLICATION, ClearCommand.COMMAND_WORD,
                    "Show a confirmation prompt. With --confirm, permanently delete all clients and reset IDs to 1.",
                    ClearCommand.COMMAND_WORD + " [--confirm]",
                    "clear"),
            new CommandSummary(APPLICATION, HelpCommand.COMMAND_WORD,
                    "Open the help window. You can also use the Help menu or F1.",
                    HelpCommand.COMMAND_WORD,
                    "help"),
            new CommandSummary(APPLICATION, ExitCommand.COMMAND_WORD,
                    "Close Cindy. Client data is saved automatically.",
                    ExitCommand.COMMAND_WORD,
                    "exit"));

    private HelpContent() {
    }

    /**
     * Returns the immutable command summaries in display order, grouped by category.
     */
    public static List<CommandSummary> getCommands() {
        return COMMANDS;
    }
}
