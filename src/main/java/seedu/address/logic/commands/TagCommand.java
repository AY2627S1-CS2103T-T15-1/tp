package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNTAG;

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
            + "adds a client to a group, removes a tag from a client, or deletes a group. The client is chosen by "
            + "ID or by part of their name. A client is either a gym or a personal client, so " + PREFIX_TYPE
            + " replaces the client's previous label. A group must be created with group " + PREFIX_NAME_LONG
            + " GROUP before clients can be added to it.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then one of "
            + PREFIX_TYPE + " gym, " + PREFIX_TYPE + " personal, " + PREFIX_GROUP + " GROUP or "
            + PREFIX_UNTAG + " TAG. To delete a group from every client: " + PREFIX_DELETE + " " + PREFIX_GROUP
            + " GROUP\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_ID + " 1 " + PREFIX_TYPE + " gym, "
            + COMMAND_WORD + " " + PREFIX_ID + " 1 " + PREFIX_GROUP + " morning, "
            + COMMAND_WORD + " " + PREFIX_ID + " 1 " + PREFIX_UNTAG + " morning, "
            + COMMAND_WORD + " " + PREFIX_DELETE + " " + PREFIX_GROUP + " morning";

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
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_TYPE + " " + clientType.tagName;
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
