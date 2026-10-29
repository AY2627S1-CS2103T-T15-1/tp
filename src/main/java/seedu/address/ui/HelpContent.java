package seedu.address.ui;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CANCEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONFIRM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EDIT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PLAN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RESCHEDULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TODAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNTAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

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

    private static final String CLIENT_SELECTOR = "(" + PREFIX_ID + " ID | " + PREFIX_NAME_LONG + " NAME)";
    private static final String STATUS_SELECTOR = "(" + PREFIX_ID + " ID | " + PREFIX_NAME_LONG + " NAME | "
            + PREFIX_PHONE_LONG + " PHONE)";

    /**
     * Entries are ordered by category, then by their intended display order within that category.
     */
    private static final List<CommandSummary> COMMANDS = List.of(
            new CommandSummary(CLIENT_DETAILS, AddCommand.COMMAND_WORD,
                    "Add a client with a name and phone number.",
                    AddCommand.COMMAND_WORD + " " + PREFIX_NAME_LONG + " \"NAME\" " + PREFIX_PHONE_LONG + " \"PHONE\"",
                    "add " + PREFIX_NAME_LONG + " \"James Ho\" " + PREFIX_PHONE_LONG + " \"+65 9123 4567\""),
            new CommandSummary(CLIENT_DETAILS, ViewCommand.COMMAND_WORD,
                    "Open a client's profile.",
                    ViewCommand.COMMAND_WORD + " " + CLIENT_SELECTOR,
                    "view " + PREFIX_ID + " 2"),
            new CommandSummary(CLIENT_DETAILS, EditCommand.COMMAND_WORD,
                    "Change a client's name or phone number. Provide at least one field to change.",
                    EditCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " [" + PREFIX_TO_NAME + " NEW_NAME] ["
                            + PREFIX_TO_PHONE + " NEW_PHONE]",
                    "edit " + PREFIX_ID + " 2 " + PREFIX_TO_NAME + " \"James Lee\""),
            new CommandSummary(CLIENT_DETAILS, DeleteCommand.COMMAND_WORD,
                    "Permanently delete a client and their saved details.",
                    DeleteCommand.COMMAND_WORD + " " + CLIENT_SELECTOR,
                    "delete " + PREFIX_ID + " 3"),
            new CommandSummary(FINDING_CLIENTS, ListCommand.COMMAND_WORD,
                    "Show all clients.",
                    ListCommand.COMMAND_WORD,
                    "list"),
            new CommandSummary(FINDING_CLIENTS, ListCommand.COMMAND_WORD,
                    "Show clients with appointments today.",
                    ListCommand.COMMAND_WORD + " " + PREFIX_TODAY,
                    "list " + PREFIX_TODAY),
            new CommandSummary(FINDING_CLIENTS, FindCommand.COMMAND_WORD,
                    "Find clients using at least one filter. Clients must match every supplied filter.",
                    FindCommand.COMMAND_WORD + " [" + PREFIX_NAME_LONG + " NAME] [" + PREFIX_PHONE_LONG + " PHONE] ["
                            + PREFIX_ID + " ID] [" + PREFIX_GROUP + " GROUP]",
                    "find " + PREFIX_GROUP + " morning " + PREFIX_NAME_LONG + " alex"),
            new CommandSummary(TRAINING_INFORMATION, NoteCommand.COMMAND_WORD,
                    "Replace a client's note. Double quotes are required; "
                            + "use \"\" to clear it and \\n for line breaks.",
                    NoteCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " " + PREFIX_NOTE + " \"TEXT\"",
                    "note " + PREFIX_ID + " 3 " + PREFIX_NOTE + " \"Knee injury\\nAvoid lunges\""),
            new CommandSummary(TRAINING_INFORMATION, PlanCommand.COMMAND_WORD,
                    "Replace a workout plan. Double quotes are required; use \"\" to clear it and \\n for line breaks.",
                    PlanCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " " + PREFIX_PLAN + " \"TEXT\"",
                    "plan " + PREFIX_ID + " 2 " + PREFIX_PLAN + " \"Squats: 3 sets of 12\""),
            new CommandSummary(CLIENT_STATUS, SetActiveCommand.COMMAND_WORD_ACTIVE,
                    "Mark a client as active again.",
                    SetActiveCommand.COMMAND_WORD_ACTIVE + " " + STATUS_SELECTOR,
                    "active " + PREFIX_PHONE_LONG + " 9103"),
            new CommandSummary(CLIENT_STATUS, SetActiveCommand.COMMAND_WORD_INACTIVE,
                    "Mark a client as inactive while keeping their saved details.",
                    SetActiveCommand.COMMAND_WORD_INACTIVE + " " + STATUS_SELECTOR,
                    "inactive " + PREFIX_ID + " 4"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Set a client's type to gym or personal, replacing their previous type.",
                    TagCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " " + PREFIX_TYPE + " (gym | personal)",
                    "tag " + PREFIX_ID + " 2 " + PREFIX_TYPE + " gym"),
            new CommandSummary(CLIENT_TYPE, GroupCommand.COMMAND_WORD,
                    "Create a custom group. Group names use letters and digits; gym and personal are reserved.",
                    GroupCommand.COMMAND_WORD + " " + PREFIX_NAME_LONG + " GROUP",
                    "group " + PREFIX_NAME_LONG + " rehab"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Add a client to an existing custom group. Create the group first with group "
                            + PREFIX_NAME_LONG + " GROUP.",
                    TagCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " " + PREFIX_GROUP + " GROUP",
                    "tag " + PREFIX_ID + " 2 " + PREFIX_GROUP + " morning"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Remove a tag from a client, keeping the group and the client's other details.",
                    TagCommand.COMMAND_WORD + " " + CLIENT_SELECTOR + " " + PREFIX_UNTAG + " TAG",
                    "tag " + PREFIX_ID + " 1 " + PREFIX_UNTAG + " morning"),
            new CommandSummary(CLIENT_TYPE, GroupCommand.COMMAND_WORD,
                    "Move a client between existing custom groups. The client must be in the source group.",
                    GroupCommand.COMMAND_WORD + " " + PREFIX_MOVE + " " + CLIENT_SELECTOR + " " + PREFIX_FROM
                            + " GROUP " + PREFIX_TO + " GROUP",
                    "group " + PREFIX_MOVE + " " + PREFIX_ID + " 1 " + PREFIX_FROM + " morning "
                            + PREFIX_TO + " evening"),
            new CommandSummary(CLIENT_TYPE, TagCommand.COMMAND_WORD,
                    "Delete a custom group and remove it from every client. gym and personal cannot be deleted.",
                    TagCommand.COMMAND_WORD + " " + PREFIX_DELETE + " " + PREFIX_GROUP + " GROUP",
                    "tag " + PREFIX_DELETE + " " + PREFIX_GROUP + " morning"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Replace a client's appointments with four weekly appointments, starting on the given date.",
                    AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_ADD + " " + CLIENT_SELECTOR
                            + " " + PREFIX_DATE + " YYYY-MM-DD " + PREFIX_TIME + " HH:MM",
                    "appointment " + PREFIX_ADD + " " + PREFIX_ID + " 2 " + PREFIX_DATE + " 2026-12-07 "
                            + PREFIX_TIME + " 09:00"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Show all clients' appointments on a date, including cancelled appointments.",
                    AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_DATE + " YYYY-MM-DD",
                    "appointment " + PREFIX_DATE + " 2026-10-26"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Show appointments in the Monday to Sunday week containing the date, including cancellations.",
                    AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_WEEK + " YYYY-MM-DD",
                    "appointment " + PREFIX_WEEK + " 2026-10-26"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Cancel an appointment without deleting it. INDEX is its number in the client's profile (1 to 4).",
                    AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_CANCEL + " " + CLIENT_SELECTOR + " "
                            + PREFIX_INDEX + " INDEX",
                    "appointment " + PREFIX_CANCEL + " " + PREFIX_ID + " 2 " + PREFIX_INDEX + " 1"),
            new CommandSummary(APPOINTMENTS, AddAppointmentCommand.COMMAND_WORD,
                    "Change an appointment's date and time. A cancelled appointment becomes active again.",
                    AddAppointmentCommand.COMMAND_WORD + " " + PREFIX_RESCHEDULE + " " + CLIENT_SELECTOR
                            + " " + PREFIX_INDEX + " INDEX " + PREFIX_DATE + " YYYY-MM-DD " + PREFIX_TIME + " HH:MM",
                    "appointment " + PREFIX_RESCHEDULE + " " + PREFIX_ID + " 2 " + PREFIX_INDEX + " 1 "
                            + PREFIX_DATE + " 2026-12-08 " + PREFIX_TIME + " 18:00"),
            new CommandSummary(SESSIONS, SessionCommand.COMMAND_WORD,
                    "Record a session and show the updated profile. Only one session per client per date is allowed.",
                    SessionCommand.COMMAND_WORD + " " + PREFIX_ADD + " " + CLIENT_SELECTOR + " " + PREFIX_DATE
                            + " YYYY-MM-DD [" + PREFIX_NOTE + " TEXT]",
                    "session " + PREFIX_ADD + " " + PREFIX_ID + " 2 " + PREFIX_DATE + " 2026-10-22 "
                            + PREFIX_NOTE + " \"Squats: 3 sets\""),
            new CommandSummary(SESSIONS, SessionCommand.COMMAND_WORD,
                    "Replace a session's note and show the updated profile. Use " + PREFIX_NOTE
                            + " without text to clear it.",
                    SessionCommand.COMMAND_WORD + " " + PREFIX_EDIT + " " + CLIENT_SELECTOR + " " + PREFIX_DATE
                            + " YYYY-MM-DD " + PREFIX_NOTE + " [TEXT]",
                    "session " + PREFIX_EDIT + " " + PREFIX_ID + " 2 " + PREFIX_DATE + " 2026-10-22 "
                            + PREFIX_NOTE + " \"Improved squat depth\""),
            new CommandSummary(APPLICATION, ClearCommand.COMMAND_WORD,
                    "Show a confirmation prompt. With " + PREFIX_CONFIRM
                            + ", permanently delete all clients and groups, and reset IDs to 1.",
                    ClearCommand.COMMAND_WORD + " [" + PREFIX_CONFIRM + "]",
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
