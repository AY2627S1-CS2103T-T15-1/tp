package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNTAG;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Removes a tag, which can be a group or a client type, from a client chosen by ID or by name.
 * The group itself is kept, so other clients stay in it.
 */
public class UntagCommand extends PersonTargetedCommand {

    public static final String MESSAGE_REMOVED = "Removed %1$s from client: %2$s";
    public static final String MESSAGE_NOT_TAGGED = "This client does not have the tag %1$s: %2$s";

    private static final Logger logger = LogsCenter.getLogger(UntagCommand.class);

    private final Tag tag;

    /**
     * Creates an UntagCommand that removes {@code tag} from the client chosen by {@code selector}.
     */
    public UntagCommand(PersonSelector selector, Tag tag) {
        super(selector);
        requireNonNull(tag);
        this.tag = tag;
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) throws CommandException {
        Set<Tag> updatedTags = target.getTags().stream()
                .filter(existingTag -> !existingTag.tagName.equalsIgnoreCase(tag.tagName))
                .collect(Collectors.toSet());
        if (updatedTags.size() == target.getTags().size()) {
            throw new CommandException(String.format(MESSAGE_NOT_TAGGED, tag.tagName, Messages.format(target)));
        }

        Person editedPerson = target.withTags(updatedTags);
        model.setPerson(target, editedPerson);
        logger.info("Removed tag " + tag.tagName + " from client " + target.getId());
        return new CommandResult(String.format(MESSAGE_REMOVED, tag.tagName, Messages.format(editedPerson)));
    }

    @Override
    protected String getCommandWord() {
        return TagCommand.COMMAND_WORD;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_UNTAG + " " + tag.tagName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UntagCommand otherCommand)) {
            return false;
        }

        return selector.equals(otherCommand.selector)
                && tag.equals(otherCommand.tag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("tag", tag)
                .toString();
    }
}
