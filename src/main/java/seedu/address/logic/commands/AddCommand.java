package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a client. "
            + "Parameters: "
            + PREFIX_NAME_LONG + " \"NAME\" "
            + PREFIX_PHONE_LONG + " \"PHONE\"\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME_LONG + " \"John Doe\" "
            + PREFIX_PHONE_LONG + " \"+65 9123 4567\"";

    public static final String MESSAGE_SUCCESS = "New client added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This client already exists in Cindy.";
    public static final String MESSAGE_NO_IDS_LEFT = "Cannot add the client: every client ID up to "
            + PersonId.MAX_CLIENT_ID + " has been used. Run clear --confirm to start the numbering again from 1.";

    private final Name name;
    private final Phone phone;

    /**
     * Creates an AddCommand to add a client with the specified {@code name} and {@code phone}.
     * The client is given the next available ID when the command is executed.
     */
    public AddCommand(Name name, Phone phone) {
        requireNonNull(name);
        requireNonNull(phone);
        this.name = name;
        this.phone = phone;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        PersonId id = model.getNextPersonId();
        if (!id.isClientId()) {
            throw new CommandException(MESSAGE_NO_IDS_LEFT);
        }
        Person toAdd = new Person(id, name, phone);
        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        boolean isPhoneInUse = model.hasPersonWithPhone(toAdd.getPhone());
        model.addPerson(toAdd);
        String successMessage = String.format(MESSAGE_SUCCESS, Messages.format(toAdd));
        if (isPhoneInUse) {
            successMessage += "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;
        }
        return new CommandResult(successMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return name.equals(otherAddCommand.name)
                && phone.equals(otherAddCommand.phone);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .toString();
    }
}
