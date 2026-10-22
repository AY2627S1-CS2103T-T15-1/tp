package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds a client, chosen by ID or by name, to an existing group.
 */
public class AddToGroupCommand extends PersonTargetedCommand {

    public static final String MESSAGE_ADDED = "Added client to group %1$s: %2$s";
    public static final String MESSAGE_ALREADY_IN_GROUP = "This client is already in group %1$s: %2$s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "The group %1$s does not exist. Create it first with: "
            + GroupCommand.COMMAND_WORD + " " + PREFIX_NAME_LONG + " %1$s";

    private static final Logger logger = LogsCenter.getLogger(AddToGroupCommand.class);

    private final Tag group;

    /**
     * Creates an AddToGroupCommand that adds the client chosen by {@code selector} to {@code group}.
     * {@code group} must not be a client type.
     */
    public AddToGroupCommand(PersonSelector selector, Tag group) {
        super(selector);
        requireNonNull(group);
        assert !Tag.CLIENT_TYPES.contains(group) : "Client types are set with tag --type, not added as groups";
        this.group = group;
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        if (!model.hasGroup(group)) {
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, group.tagName));
        }
        if (target.getTags().contains(group)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_IN_GROUP, group.tagName,
                    Messages.format(target)));
        }

        Set<Tag> updatedTags = new HashSet<>(target.getTags());
        updatedTags.add(group);
        Person editedPerson = target.withTags(updatedTags);
        model.setPerson(target, editedPerson);
        logger.info("Added client " + target.getId() + " to group " + group.tagName);
        return new CommandResult(String.format(MESSAGE_ADDED, group.tagName, Messages.format(editedPerson)));
    }

    @Override
    protected String getCommandWord() {
        return TagCommand.COMMAND_WORD;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_GROUP + " " + group.tagName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddToGroupCommand otherCommand)) {
            return false;
        }

        return selector.equals(otherCommand.selector)
                && group.equals(otherCommand.group);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("group", group)
                .toString();
    }
}
