package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.LongFormText;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Replaces the note of a client, chosen by ID or by name.
 */
public class NoteCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Replaces the note of a client, "
            + "chosen by ID or by part of their name. The note must be in double quotes, "
            + "and an empty note (\"\") removes the client's note.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then "
            + PREFIX_NOTE + " \"TEXT\"\n"
            + "Use \\n inside TEXT for a line break.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ID + " 1 " + PREFIX_NOTE + " \"Knee injury, avoid lunges\"";

    public static final String MESSAGE_NOTE_UPDATED = "Updated note of client: %1$s";
    public static final String MESSAGE_NOTE_REMOVED = "Removed note of client: %1$s";

    private static final Logger logger = LogsCenter.getLogger(NoteCommand.class);

    private final LongFormText note;

    /**
     * Creates a NoteCommand that replaces the note of the client chosen by {@code selector} with {@code note}.
     */
    public NoteCommand(PersonSelector selector, LongFormText note) {
        super(selector);
        requireNonNull(note);
        this.note = note;
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        Person editedPerson = target.withNote(note);
        model.setPerson(target, editedPerson);
        logger.info((note.isBlank() ? "Removed note of client " : "Updated note of client ") + target.getId());

        String messageFormat = note.isBlank() ? MESSAGE_NOTE_REMOVED : MESSAGE_NOTE_UPDATED;
        return new CommandResult(String.format(messageFormat, Messages.format(editedPerson)), editedPerson);
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_NOTE + " " + quoteLongFormText(note);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NoteCommand otherNoteCommand)) {
            return false;
        }

        return selector.equals(otherNoteCommand.selector)
                && note.equals(otherNoteCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("note", note)
                .toString();
    }
}
