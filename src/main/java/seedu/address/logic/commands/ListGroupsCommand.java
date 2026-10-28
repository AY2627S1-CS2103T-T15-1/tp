package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

/**
 * Lists every custom group by name, including groups with no clients in them.
 */
public class ListGroupsCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Groups (%1$d): %2$s";
    public static final String MESSAGE_NO_GROUPS = "No groups yet. Use group --name GROUP to create one.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Tag> groups = model.getAddressBook().getGroupList();
        if (groups.isEmpty()) {
            return new CommandResult(MESSAGE_NO_GROUPS);
        }

        String groupNames = groups.stream()
                .map(group -> group.tagName)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(", "));
        return new CommandResult(String.format(MESSAGE_SUCCESS, groups.size(), groupNames));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListGroupsCommand;
    }
}
