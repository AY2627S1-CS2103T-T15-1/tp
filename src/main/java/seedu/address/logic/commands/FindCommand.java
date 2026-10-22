package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Finds and lists all clients that match every given filter.
 * Filters can be a part of the client's name, a part of the client's phone number, the client's ID,
 * or a group or label the client is in.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds the clients that match all the given details "
            + "and lists them. Names are matched ignoring upper and lower case, and only need to contain the given "
            + "text. Phone numbers only need to contain the given digits. Groups are matched ignoring upper and "
            + "lower case, and can also be gym or personal.\n"
            + "Parameters (at least one): [" + PREFIX_NAME_LONG + " NAME] [" + PREFIX_PHONE_LONG + " PHONE] ["
            + PREFIX_ID + " ID] [" + PREFIX_GROUP + " GROUP]\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_NAME_LONG + " alex, "
            + COMMAND_WORD + " " + PREFIX_GROUP + " morning";

    public static final String MESSAGE_NO_MATCHES = "No clients match your search.";
    public static final String MESSAGE_ONE_MATCH = "Found 1 client";
    public static final String MESSAGE_MATCHES = "Found %1$d clients";

    private final List<PersonSelector> filters;
    /** The group or label the clients found must be in, or null if clients in any group can be found. */
    private final Tag group;

    /**
     * Creates a FindCommand that lists the clients matching every filter in {@code filters}.
     * {@code filters} must not be empty.
     */
    public FindCommand(List<PersonSelector> filters) {
        requireNonNull(filters);
        assert !filters.isEmpty() : "find needs at least one filter";
        this.filters = List.copyOf(filters);
        this.group = null;
    }

    /**
     * Creates a FindCommand that lists the clients in {@code group} that also match every filter in
     * {@code filters}. {@code filters} may be empty.
     */
    public FindCommand(List<PersonSelector> filters, Tag group) {
        requireNonNull(filters);
        requireNonNull(group);
        this.filters = List.copyOf(filters);
        this.group = group;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(this::matchesAllFilters);
        return new CommandResult(getMessageForMatchCount(model.getFilteredPersonList().size()));
    }

    private boolean matchesAllFilters(Person person) {
        return filters.stream().allMatch(filter -> filter.matches(person)) && isInGroup(person);
    }

    /**
     * Returns true if {@code person} has a tag with the same name as {@link #group}, ignoring upper and
     * lower case, or if no group is given.
     */
    private boolean isInGroup(Person person) {
        return group == null || person.getTags().stream()
                .anyMatch(tag -> tag.tagName.equalsIgnoreCase(group.tagName));
    }

    /**
     * Returns the feedback message for the given number of matching clients.
     */
    private static String getMessageForMatchCount(int matchCount) {
        if (matchCount == 0) {
            return MESSAGE_NO_MATCHES;
        }
        if (matchCount == 1) {
            return MESSAGE_ONE_MATCH;
        }
        return String.format(MESSAGE_MATCHES, matchCount);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return filters.equals(otherFindCommand.filters)
                && Objects.equals(group, otherFindCommand.group);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("filters", filters)
                .add("group", group)
                .toString();
    }
}
