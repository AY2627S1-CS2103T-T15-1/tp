package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Labels a client, chosen by ID or by name, as a gym or a personal client.
 * A client has at most one of these labels, so setting one removes the other.
 */
public class TagCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Labels a client as a gym or a personal client, "
            + "chosen by ID or by part of their name. A client is either a gym or a personal client, so this "
            + "replaces the client's previous label.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then "
            + PREFIX_TYPE + " gym or " + PREFIX_TYPE + " personal\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ID + " 1 " + PREFIX_TYPE + " gym";

    public static final String MESSAGE_TYPE_SET = "Labelled client as a %1$s client: %2$s";
    public static final String MESSAGE_TYPE_UNCHANGED = "This client is already a %1$s client: %2$s";

    private final Tag clientType;

    /**
     * Creates a TagCommand that labels the client chosen by {@code selector} with {@code clientType}.
     * {@code clientType} must be one of {@link Tag#CLIENT_TYPES}.
     */
    public TagCommand(PersonSelector selector, Tag clientType) {
        super(selector);
        requireNonNull(clientType);
        assert Tag.CLIENT_TYPES.contains(clientType) : "A client can only be labelled as gym or personal";
        this.clientType = clientType;
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        if (target.getTags().contains(clientType)) {
            return new CommandResult(String.format(MESSAGE_TYPE_UNCHANGED, clientType.tagName,
                    Messages.format(target)));
        }

        Set<Tag> updatedTags = new HashSet<>(target.getTags());
        updatedTags.removeAll(Tag.CLIENT_TYPES);
        updatedTags.add(clientType);
        Person editedPerson = target.withTags(updatedTags);
        model.setPerson(target, editedPerson);

        return new CommandResult(String.format(MESSAGE_TYPE_SET, clientType.tagName, Messages.format(editedPerson)));
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
        if (!(other instanceof TagCommand otherTagCommand)) {
            return false;
        }

        return selector.equals(otherTagCommand.selector)
                && clientType.equals(otherTagCommand.clientType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("clientType", clientType)
                .toString();
    }
}
