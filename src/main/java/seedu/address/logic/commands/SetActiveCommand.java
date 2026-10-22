package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Marks a client, chosen by ID, name or phone number, as active or inactive.
 * Inactive clients are former or paused clients. They stay in Cindy, but are labelled as inactive.
 */
public class SetActiveCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD_ACTIVE = "active";
    public static final String COMMAND_WORD_INACTIVE = "inactive";

    public static final String MESSAGE_USAGE_ACTIVE = getUsage(COMMAND_WORD_ACTIVE,
            "Marks a client as active again");
    public static final String MESSAGE_USAGE_INACTIVE = getUsage(COMMAND_WORD_INACTIVE,
            "Marks a client as inactive, e.g. a former or paused client");

    public static final String MESSAGE_MARKED_ACTIVE = "Marked client as active: %1$s";
    public static final String MESSAGE_MARKED_INACTIVE = "Marked client as inactive: %1$s";
    public static final String MESSAGE_ALREADY_ACTIVE = "This client is already active: %1$s";
    public static final String MESSAGE_ALREADY_INACTIVE = "This client is already inactive: %1$s";

    private final boolean isActive;

    /**
     * Creates a SetActiveCommand that marks the client chosen by {@code selector} as active
     * if {@code isActive} is true, and as inactive otherwise.
     */
    public SetActiveCommand(PersonSelector selector, boolean isActive) {
        super(selector);
        this.isActive = isActive;
    }

    private static String getUsage(String commandWord, String description) {
        return commandWord + ": " + description + ", chosen by ID, or by part of their name or phone number.\n"
                + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME or " + PREFIX_PHONE_LONG
                + " PHONE\n"
                + "Example: " + commandWord + " " + PREFIX_ID + " 1";
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        if (target.isActive() == isActive) {
            String messageFormat = isActive ? MESSAGE_ALREADY_ACTIVE : MESSAGE_ALREADY_INACTIVE;
            return new CommandResult(String.format(messageFormat, Messages.format(target)));
        }

        Person editedPerson = target.withActive(isActive);
        model.setPerson(target, editedPerson);
        String messageFormat = isActive ? MESSAGE_MARKED_ACTIVE : MESSAGE_MARKED_INACTIVE;
        return new CommandResult(String.format(messageFormat, Messages.format(editedPerson)));
    }

    @Override
    protected String getCommandWord() {
        return isActive ? COMMAND_WORD_ACTIVE : COMMAND_WORD_INACTIVE;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetActiveCommand otherCommand)) {
            return false;
        }

        return selector.equals(otherCommand.selector)
                && isActive == otherCommand.isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("isActive", isActive)
                .toString();
    }
}
