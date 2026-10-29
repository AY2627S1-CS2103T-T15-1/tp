package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

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
 * Moves a client, chosen by ID or by name, from one group to another.
 * The client's other details are kept.
 */
public class MoveGroupCommand extends PersonTargetedCommand {

    public static final String MESSAGE_MOVED = "Moved client from group %1$s to group %2$s: %3$s";
    public static final String MESSAGE_NOT_IN_GROUP = "This client is not in group %1$s: %2$s";

    private static final Logger logger = LogsCenter.getLogger(MoveGroupCommand.class);

    private final Tag from;
    private final Tag to;

    /**
     * Creates a MoveGroupCommand that moves the client chosen by {@code selector} from {@code from} to {@code to}.
     * {@code from} and {@code to} must be different, and must not be client types.
     */
    public MoveGroupCommand(PersonSelector selector, Tag from, Tag to) {
        super(selector);
        requireNonNull(from);
        requireNonNull(to);
        assert !from.equals(to) : "A client must be moved to a different group";
        assert !Tag.CLIENT_TYPES.contains(from) && !Tag.CLIENT_TYPES.contains(to)
                : "Client types are set with tag --type, not moved between";
        this.from = from;
        this.to = to;
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        if (!target.getTags().contains(from)) {
            throw new CommandException(String.format(MESSAGE_NOT_IN_GROUP, from.tagName, Messages.format(target)));
        }
        if (!model.hasGroup(to)) {
            throw new CommandException(String.format(AddToGroupCommand.MESSAGE_GROUP_NOT_FOUND, to.tagName));
        }

        Set<Tag> updatedTags = new HashSet<>(target.getTags());
        updatedTags.remove(from);
        updatedTags.add(to);
        Person editedPerson = target.withTags(updatedTags);
        model.setPerson(target, editedPerson);
        // Show every client again, so a client who left a filtered group does not seem to vanish.
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        logger.info("Moved client " + target.getId() + " from group " + from.tagName + " to " + to.tagName);
        return new CommandResult(String.format(MESSAGE_MOVED, from.tagName, to.tagName,
                Messages.format(editedPerson)));
    }

    @Override
    protected String getCommandWord() {
        return GroupCommand.COMMAND_WORD + " " + PREFIX_MOVE;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_FROM + " " + from.tagName + " " + PREFIX_TO + " " + to.tagName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MoveGroupCommand otherCommand)) {
            return false;
        }

        return selector.equals(otherCommand.selector)
                && from.equals(otherCommand.from)
                && to.equals(otherCommand.to);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("from", from)
                .add("to", to)
                .toString();
    }
}
