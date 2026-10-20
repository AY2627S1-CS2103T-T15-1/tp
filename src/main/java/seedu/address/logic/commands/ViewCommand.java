package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Shows the full details of a client, chosen by ID or by name.
 */
public class ViewCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the full details of a client, chosen by ID or by part of their name.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ID + " 1";

    public static final String MESSAGE_VIEW_PERSON_SUCCESS = "Viewing client: %1$s";

    /**
     * Creates a ViewCommand to show the details of the client chosen by {@code selector}.
     */
    public ViewCommand(PersonSelector selector) {
        super(selector);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        return new CommandResult(String.format(MESSAGE_VIEW_PERSON_SUCCESS, Messages.format(target)), target);
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
        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return selector.equals(otherViewCommand.selector);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .toString();
    }
}
