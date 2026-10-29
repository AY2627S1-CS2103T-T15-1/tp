package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final Font COMMAND_FONT = loadCommandFont();

    private final CommandExecutor commandExecutor;
    private final CommandHistory commandHistory = new CommandHistory();
    private boolean isRecallingCommand;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = requireNonNull(commandExecutor);
        commandTextField.setFont(COMMAND_FONT);
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> {
            setStyleToDefault();
            if (!isRecallingCommand) {
                commandHistory.resetNavigation();
            }
        });
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKeyPressed);
    }

    private static Font loadCommandFont() {
        // Load through a stream so installation paths containing spaces work too.
        try (InputStream stream = CommandBox.class.getResourceAsStream("/fonts/NotoSansMono-Regular.ttf")) {
            return requireNonNull(Font.loadFont(stream, 16), "Unable to load the bundled command font");
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read the bundled command font", e);
        }
    }

    /**
     * Recalls commands with unmodified arrow keys without executing them.
     */
    private void handleHistoryKeyPressed(KeyEvent event) {
        if (event.isShiftDown() || event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
            return;
        }
        if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN) {
            return;
        }

        String currentText = commandTextField.getText();
        String recalledText = event.getCode() == KeyCode.UP
                ? commandHistory.previous(currentText) : commandHistory.next(currentText);
        isRecallingCommand = true;
        try {
            commandTextField.setText(recalledText);
            commandTextField.positionCaret(recalledText.length());
        } finally {
            isRecallingCommand = false;
        }
        event.consume();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isBlank()) {
            return;
        }

        commandHistory.record(commandText);
        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
