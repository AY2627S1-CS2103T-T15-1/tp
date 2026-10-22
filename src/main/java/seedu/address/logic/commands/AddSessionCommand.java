package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
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
 * Adds a dated session note to a client.
 */
public class AddSessionCommand extends SessionCommand {

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a session to a client. "
            + "Parameters: " + PREFIX_ADD + " (" + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME) "
            + PREFIX_DATE + " YYYY-MM-DD [" + PREFIX_NOTE + " NOTE]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ADD + " " + PREFIX_ID + " 1 "
            + PREFIX_DATE + " 2026-10-21 " + PREFIX_NOTE + " Worked on squat form";

    public static final String MESSAGE_SUCCESS = "Added session for %1$s on %2$s.";
    public static final String MESSAGE_DUPLICATE_SESSION =
            "A session for %1$s already exists on %2$s. Use session --edit to update it.";

    private final LocalDate date;
    private final String note;

    /**
     * Creates an {@code AddSessionCommand} for the selected client.
     */
    public AddSessionCommand(PersonSelector selector, LocalDate date, String note) {
        super(selector);
        this.date = requireNonNull(date);
        this.note = requireNonNull(note);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        if (target.getSessions().stream().anyMatch(session -> session.getDate().equals(date))) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_SESSION, target.getName(), date));
        }

        List<Session> updatedSessions = new ArrayList<>(target.getSessions());
        updatedSessions.add(new Session(date, note));
        Person updatedPerson = target.withSessions(updatedSessions);

        model.setPerson(target, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, target.getName(), date));
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_ADD + " " + PREFIX_DATE + " " + date + " " + PREFIX_NOTE + " " + quote(note);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddSessionCommand otherCommand)) {
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
