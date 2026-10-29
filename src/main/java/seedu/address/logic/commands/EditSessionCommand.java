package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EDIT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.session.Session;

/**
 * Replaces the note of an existing session.
 */
public class EditSessionCommand extends SessionCommand {

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits a session note. "
            + "Parameters: " + PREFIX_EDIT + " (" + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME) "
            + PREFIX_DATE + " YYYY-MM-DD " + PREFIX_NOTE + " [NOTE]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_EDIT + " " + PREFIX_ID + " 1 "
            + PREFIX_DATE + " 2026-10-21 " + PREFIX_NOTE + " Improved squat depth";

    public static final String MESSAGE_SUCCESS = "Updated session for %1$s on %2$s.";
    public static final String MESSAGE_SESSION_NOT_FOUND = "No session for %1$s exists on %2$s.";

    private final LocalDate date;
    private final String note;

    /**
     * Creates an {@code EditSessionCommand} for the selected client and session date.
     */
    public EditSessionCommand(PersonSelector selector, LocalDate date, String note) {
        super(selector);
        this.date = requireNonNull(date);
        this.note = requireNonNull(note);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        List<Session> updatedSessions = new ArrayList<>(target.getSessions());
        int sessionIndex = findSessionIndex(updatedSessions);
        if (sessionIndex < 0) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, target.getName(), date));
        }

        updatedSessions.set(sessionIndex, new Session(date, note));
        Person updatedPerson = target.withSessions(updatedSessions);

        model.setPerson(target, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, target.getName(), date), updatedPerson);
    }

    private int findSessionIndex(List<Session> sessions) {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getDate().equals(date)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_EDIT + " " + PREFIX_DATE + " " + date + " " + PREFIX_NOTE + " " + quote(note);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditSessionCommand otherCommand)) {
            return false;
        }

        return selector.equals(otherCommand.selector)
                && date.equals(otherCommand.date)
                && note.equals(otherCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("date", date)
                .add("note", note)
                .toString();
    }
}
