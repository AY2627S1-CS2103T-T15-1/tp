package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";
    public static final String MESSAGE_CONFIRMATION =
            "This will delete all saved clients. Run `clear --confirm` to confirm.";

    private final boolean confirmed;

    /**
     * Creates a clear command with the specified confirmation status.
     */
    public ClearCommand(boolean confirmed) {
        this.confirmed = confirmed;
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
