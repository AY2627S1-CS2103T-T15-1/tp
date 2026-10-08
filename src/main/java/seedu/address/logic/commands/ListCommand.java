package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all clients in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS_SINGULAR = "Listed 1 client";
    public static final String MESSAGE_SUCCESS_PLURAL = "Listed %1$d clients";
    public static final String MESSAGE_NO_CLIENTS = "No clients yet. Use `add` to add your first client.";

    /**
     * Executes the list command, showing all clients and reporting how many are listed.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        int clientCount = model.getFilteredPersonList().size();
        return new CommandResult(getMessageForClientCount(clientCount));
    }

    /**
     * Returns the feedback message for the given number of listed clients.
     */
    private static String getMessageForClientCount(int clientCount) {
        if (clientCount == 0) {
            return MESSAGE_NO_CLIENTS;
        }
        if (clientCount == 1) {
            return MESSAGE_SUCCESS_SINGULAR;
        }
        return String.format(MESSAGE_SUCCESS_PLURAL, clientCount);
    }
}
