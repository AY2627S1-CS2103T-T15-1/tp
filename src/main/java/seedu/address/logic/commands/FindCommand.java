package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Finds and lists all clients that match every given filter.
 * Filters can be a part of the client's name, a part of the client's phone number, the client's ID,
 * or groups or labels the client is in.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds the clients that match all the given details "
            + "and lists them. Names are matched ignoring upper and lower case, and only need to contain the given "
            + "text. Phone numbers only need to contain the given digits. Groups are matched ignoring upper and "
            + "lower case, and can also be gym or personal. Give several groups to find the clients in all of them.\n"
            + "Parameters (at least one): [" + PREFIX_NAME_LONG + " NAME] [" + PREFIX_PHONE_LONG + " PHONE] ["
            + PREFIX_ID + " ID] [" + PREFIX_GROUP + " GROUP]...\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_NAME_LONG + " alex, "
            + COMMAND_WORD + " " + PREFIX_GROUP + " morning " + PREFIX_GROUP + " gym";

    public static final String MESSAGE_NO_MATCHES = "No clients match your search.";
    public static final String MESSAGE_ONE_MATCH = "Found 1 client";
    public static final String MESSAGE_MATCHES = "Found %1$d clients";

    private final List<PersonSelector> filters;
    /** The groups or labels the clients found must all be in. Empty if clients in any group can be found. */
    private final List<Tag> groups;

    /**
     * Creates a FindCommand that lists the clients matching every filter in {@code filters}.
     * {@code filters} must not be empty.
     */
    public FindCommand(List<PersonSelector> filters) {
        this(filters, List.of());
        assert !filters.isEmpty() : "find needs at least one filter";
    }

    /**
     * Creates a FindCommand that lists the clients in every group in {@code groups} that also match every
     * filter in {@code filters}. At least one of {@code filters} and {@code groups} must not be empty.
     */
    public FindCommand(List<PersonSelector> filters, List<Tag> groups) {
        requireNonNull(filters);
        requireNonNull(groups);
        assert !filters.isEmpty() || !groups.isEmpty() : "find needs at least one filter or group";
        this.filters = List.copyOf(filters);
        this.groups = List.copyOf(groups);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(this::matchesAllFilters);
        return new CommandResult(getMessageForMatchCount(model.getFilteredPersonList().size()));
    }

    private boolean matchesAllFilters(Person person) {
        return filters.stream().allMatch(filter -> filter.matches(person))
                && groups.stream().allMatch(group -> isInGroup(person, group));
    }

    /**
     * Returns true if {@code person} has a tag with the same name as {@code group}, ignoring upper and lower case.
     */
    private static boolean isInGroup(Person person, Tag group) {
        return person.getTags().stream()
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
                && groups.equals(otherFindCommand.groups);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("filters", filters)
                .add("groups", groups)
                .toString();
    }
}
