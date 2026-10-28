package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

/**
 * Creates a custom group that clients can later be added to.
 */
public class GroupCommand extends Command {

    public static final String COMMAND_WORD = "group";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates a group of clients, such as morning or "
            + "rehab, or moves a client, chosen by ID or by part of their name, from one group to another. "
            + "Group names are made of letters and digits, with spaces or hyphens between words, "
            + "and ignore upper and lower case. "
            + "gym and personal are built-in labels, so they cannot be groups.\n"
            + "Parameters: " + PREFIX_NAME_LONG + " GROUP, or " + PREFIX_MOVE + " " + PREFIX_ID + " ID (or "
            + PREFIX_NAME_LONG + " NAME) " + PREFIX_FROM + " GROUP " + PREFIX_TO + " GROUP\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_NAME_LONG + " morning, "
            + COMMAND_WORD + " " + PREFIX_MOVE + " " + PREFIX_ID + " 1 " + PREFIX_FROM + " morning " + PREFIX_TO
            + " evening";

    public static final String MESSAGE_SUCCESS = "Created group: %1$s";
    public static final String MESSAGE_DUPLICATE_GROUP = "The group %1$s already exists.";

    private static final Logger logger = LogsCenter.getLogger(GroupCommand.class);

    private final Tag group;

    /**
     * Creates a GroupCommand that creates {@code group}.
     * {@code group} must not be a client type.
     */
    public GroupCommand(Tag group) {
        requireNonNull(group);
        assert !Tag.CLIENT_TYPES.contains(group) : "Client types are built in and cannot be groups";
        this.group = group;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasGroup(group)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GROUP, group.tagName));
        }

        model.addGroup(group);
        logger.info("Created group: " + group.tagName);
        return new CommandResult(String.format(MESSAGE_SUCCESS, group.tagName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GroupCommand otherGroupCommand)) {
            return false;
        }

        return group.equals(otherGroupCommand.group);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group", group)
                .toString();
    }
}
