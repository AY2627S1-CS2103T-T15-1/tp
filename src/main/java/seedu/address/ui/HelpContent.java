package seedu.address.ui;

import java.util.List;

import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.PlanCommand;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.commands.SetActiveCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.ViewCommand;

/**
 * Provides the command reference for the help window, independently of JavaFX controls.
 * Mark planned commands as unavailable until the application parser implements them.
 */
public final class HelpContent {
    private static final String CLIENT_DETAILS = "Client details";
    private static final String FINDING_CLIENTS = "Finding clients";
    private static final String TRAINING_INFORMATION = "Training information";
    private static final String CLIENT_STATUS = "Client status";
    private static final String CLIENT_TYPE = "Client types and groups";
    private static final String APPOINTMENTS = "Appointments";
    private static final String SESSIONS = "Session records";
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
            new CommandSummary(FINDING_CLIENTS, ListCommand.COMMAND_WORD,
                    "Show clients with appointments today.",
                    ListCommand.COMMAND_WORD + " --today",
                    "list --today", false),
            new CommandSummary(FINDING_CLIENTS, FindCommand.COMMAND_WORD,
                    "Find clients using at least one filter. Clients must match every supplied filter.",
                    FindCommand.COMMAND_WORD + " [--name NAME] [--phone PHONE] [--id ID] [--group GROUP]",
                    "find --group morning --name james"),
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
            new CommandSummary(CLIENT_TYPE, GroupCommand.COMMAND_WORD,
                    "Create a custom group. Group names use letters and digits; gym and personal are reserved.",
                    GroupCommand.COMMAND_WORD + " --name GROUP",
                    "group --name morning"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Add a client to an existing custom group. Create the group first with group --name GROUP.",
                    TagCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " --group GROUP",
                    "tag --id 2 --group morning"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Remove a tag from a client, keeping the group and the client's other details.",
                    TagCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " --untag TAG",
                    "tag --id 2 --untag morning"),
            new CommandSummary(CLIENT_TYPE, GroupCommand.COMMAND_WORD,
                    "Move a client between existing custom groups. The client must be in the source group.",
                    GroupCommand.COMMAND_WORD + " --move " + CLIENT_SELECTOR + " --from GROUP --to GROUP",
                    "group --move --id 2 --from morning --to evening"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Delete a custom group and remove it from every client. gym and personal cannot be deleted.",
                    TagCommand.COMMAND_WORD + " --delete --group GROUP",
                    "tag --delete --group morning"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Replace a client's appointments with four weekly appointments, starting on the given date.",
                    AddAppointmentCommand.COMMAND_WORD + " --add " + CLIENT_SELECTOR
                            + " --date YYYY-MM-DD --time HH:MM",
                    "appointment --add --id 2 --date 2026-10-26 --time 09:00"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Show all clients' appointments on a date, including cancelled appointments.",
                    AddAppointmentCommand.COMMAND_WORD + " --date YYYY-MM-DD",
                    "appointment --date 2026-10-26"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Show appointments in the Monday to Sunday week containing the date, including cancellations.",
                    AddAppointmentCommand.COMMAND_WORD + " --week YYYY-MM-DD",
                    "appointment --week 2026-10-26"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Cancel an appointment without deleting it. INDEX is its number in the client's profile (1 to 4).",
                    AddAppointmentCommand.COMMAND_WORD + " --cancel " + CLIENT_SELECTOR + " --index INDEX",
                    "appointment --cancel --id 2 --index 1"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Change an appointment's date and time. A cancelled appointment becomes active again.",
                    AddAppointmentCommand.COMMAND_WORD + " --reschedule " + CLIENT_SELECTOR
                            + " --index INDEX --date YYYY-MM-DD --time HH:MM",
                    "appointment --reschedule --id 2 --index 1 --date 2026-10-27 --time 18:00"),
            new CommandSummary(SESSIONS, SessionCommand.COMMAND_WORD,
                    "Record a session and show the updated profile. Only one session per client per date is allowed.",
                    SessionCommand.COMMAND_WORD + " --add " + CLIENT_SELECTOR + " --date YYYY-MM-DD [--note TEXT]",
                    "session --add --id 2 --date 2026-10-22 --note \"Squats: 3 sets\""),
            new CommandSummary(SESSIONS, SessionCommand.COMMAND_WORD,
                    "Replace a session's note and show the updated profile. Use --note without text to clear it.",
                    SessionCommand.COMMAND_WORD + " --edit " + CLIENT_SELECTOR + " --date YYYY-MM-DD --note [TEXT]",
                    "session --edit --id 2 --date 2026-10-22 --note \"Improved squat depth\""),
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
