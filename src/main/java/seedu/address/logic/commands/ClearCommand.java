package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes all saved clients and groups after confirmation.\n"
            + "Use clear to see the confirmation prompt, or clear --confirm to delete all saved clients and groups.";
    public static final String MESSAGE_SUCCESS = "Cindy's clients and groups have been cleared!";
    public static final String MESSAGE_CONFIRMATION =
            "This will delete all saved clients and groups. Run `clear --confirm` to confirm.";

    private final boolean confirmed;

    /**
     * Creates a clear command with the specified confirmation status.
     */
    public ClearCommand(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public boolean isReadOnly() {
        return !confirmed;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (!confirmed) {
            return new CommandResult(MESSAGE_CONFIRMATION);
        }
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
