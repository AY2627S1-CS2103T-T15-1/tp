package seedu.address.logic.commands;

/**
 * Represents a command that manages a client's sessions.
 */
public abstract class SessionCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "session";

    /**
     * Creates a session command that acts on the client chosen by {@code selector}.
     */
    protected SessionCommand(PersonSelector selector) {
        super(selector);
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }
}
