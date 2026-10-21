package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Represents a command that acts on one client, chosen using a {@code PersonSelector}.
 * If the selector matches several clients, the command shows those clients instead of acting on any of them,
 * so that the user can choose one by ID.
 */
public abstract class PersonTargetedCommand extends Command {

    protected final PersonSelector selector;

    /**
     * Creates a command that acts on the client chosen by {@code selector}.
     */
    protected PersonTargetedCommand(PersonSelector selector) {
        requireNonNull(selector);
        this.selector = selector;
    }

    @Override
    public final CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> matches = selector.findMatches(model.getAddressBook().getPersonList());

        if (matches.isEmpty()) {
            throw new CommandException(selector.getNoMatchMessage());
        }
        if (matches.size() > 1) {
            model.updateFilteredPersonList(selector::matches);
            String retryExample = getCommandWord() + " " + PREFIX_ID + " " + matches.get(0).getId()
                    + getArgumentsAfterSelector();
            return new CommandResult(selector.getMultipleMatchesMessage(matches, retryExample));
        }
        return executeOn(matches.get(0), model);
    }

    /**
     * Executes the command on {@code target}, the only client chosen by the selector.
     *
     * @throws CommandException If an error occurs during command execution.
     */
    protected abstract CommandResult executeOn(Person target, Model model) throws CommandException;

    /**
     * Returns the command word of this command.
     */
    protected abstract String getCommandWord();

    /**
     * Returns the arguments of this command other than the one choosing the client, as the user would type them,
     * starting with a space. They are used to show a complete command when the user has to choose a client by
     * {@code ID}. Commands with no other arguments return an empty string.
     */
    protected String getArgumentsAfterSelector() {
        return "";
    }

    /**
     * Returns {@code value} in double quotes, as the user would type it.
     */
    protected static String quote(String value) {
        return "\"" + value + "\"";
    }
}
