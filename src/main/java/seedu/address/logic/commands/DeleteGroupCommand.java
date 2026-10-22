package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

/**
 * Deletes a group, and removes it from every client in it.
 */
public class DeleteGroupCommand extends Command {

    public static final String MESSAGE_DELETED = "Deleted group %1$s, and removed it from %2$d client(s).";
    public static final String MESSAGE_GROUP_NOT_FOUND = "The group %1$s does not exist.";

    private static final Logger logger = LogsCenter.getLogger(DeleteGroupCommand.class);

    private final Tag group;

    /**
     * Creates a DeleteGroupCommand that deletes {@code group}.
     * {@code group} must not be a client type.
     */
    public DeleteGroupCommand(Tag group) {
        requireNonNull(group);
        assert !Tag.CLIENT_TYPES.contains(group) : "Client types are built in and cannot be deleted";
        this.group = group;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasGroup(group)) {
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, group.tagName));
        }

        long clientsInGroup = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getTags().contains(group))
                .count();
        model.deleteGroup(group);
        logger.info("Deleted group " + group.tagName + " from " + clientsInGroup + " client(s)");
        return new CommandResult(String.format(MESSAGE_DELETED, group.tagName, clientsInGroup));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteGroupCommand otherCommand)) {
            return false;
        }

        return group.equals(otherCommand.group);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group", group)
                .toString();
    }
}
