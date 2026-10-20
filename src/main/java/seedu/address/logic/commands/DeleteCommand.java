package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a client, together with their full record, chosen by ID or by name.
 */
public class DeleteCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a client, chosen by ID or by part of their name.\n"
            + "All of the client's details are deleted with them.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ID + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted client: %1$s";

    /**
     * Creates a DeleteCommand to delete the client chosen by {@code selector}.
     */
    public DeleteCommand(PersonSelector selector) {
        super(selector);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        model.deletePerson(target);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(target)));
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return selector.equals(otherDeleteCommand.selector);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .toString();
    }
}
