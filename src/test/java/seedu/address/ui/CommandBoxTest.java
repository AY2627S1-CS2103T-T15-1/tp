package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests command submission and history navigation through the command field's events.
 */
public class CommandBoxTest {
    private final List<String> executedCommands = new ArrayList<>();
    private CommandBox commandBox;
    private TextField commandTextField;

    @BeforeAll
    public static void initializeJavaFx() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Runnable initialize = () -> {
            Platform.setImplicitExit(false);
            started.countDown();
        };
        try {
            Platform.startup(initialize);
        } catch (IllegalStateException e) {
            // Another UI test may have already started the toolkit.
            Platform.runLater(initialize);
        }
        assertTrue(started.await(10, TimeUnit.SECONDS), "JavaFX startup timed out");
    }

    @BeforeEach
    public void setUp() throws Exception {
        runOnFxThread(() -> createCommandBox(command -> {
            executedCommands.add(command);
            return new CommandResult("Success");
        }));
    }

    @Test
    public void mainTheme_commandInput_usesBundledNotoSansMono() throws Exception {
        runOnFxThread(() -> {
            StackPane root = (StackPane) commandBox.getRoot();
            root.getStyleClass().add("app-shell");
            root.getStylesheets().add(CommandBox.class.getResource("/view/ContactTheme.css").toExternalForm());
            root.applyCss();

            assertEquals("Noto Sans Mono", commandTextField.getFont().getFamily());
        });
    }

    @Test
    public void constructor_nullExecutor_throwsNullPointerException() throws Exception {
        runOnFxThread(() -> assertThrows(NullPointerException.class, () -> new CommandBox(null)));
    }

    @Test
    public void navigate_emptyHistory_preservesDraft() throws Exception {
        runOnFxThread(() -> {
            commandTextField.setText("unfinished command");
            pressKey(KeyCode.UP);
            pressKey(KeyCode.UP);
            pressKey(KeyCode.DOWN);
            assertEquals("unfinished command", commandTextField.getText());
            assertTrue(executedCommands.isEmpty());
        });
    }

    @Test
    public void navigate_submittedCommands_stopsAtOldestAndRestoresDraft() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            submit("find Alice");
            commandTextField.setText("unfinished command");

            pressKey(KeyCode.UP);
            assertEquals("find Alice", commandTextField.getText());
            assertEquals("find Alice".length(), commandTextField.getCaretPosition());
            pressKey(KeyCode.UP);
            assertEquals("list", commandTextField.getText());
            pressKey(KeyCode.UP);
            assertEquals("list", commandTextField.getText());
            pressKey(KeyCode.DOWN);
            assertEquals("find Alice", commandTextField.getText());
            pressKey(KeyCode.DOWN);
            assertEquals("unfinished command", commandTextField.getText());
            assertEquals("unfinished command".length(), commandTextField.getCaretPosition());
            pressKey(KeyCode.DOWN);
            assertEquals("unfinished command", commandTextField.getText());
            assertEquals(List.of("list", "find Alice"), executedCommands);
        });
    }

    @Test
    public void submit_recalledCommand_executesOnlyOnEnterAndResetsNavigation() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            submit("find Alice");
            pressKey(KeyCode.UP);
            pressKey(KeyCode.UP);
            assertEquals(List.of("list", "find Alice"), executedCommands);

            commandTextField.fireEvent(new ActionEvent());
            assertEquals(List.of("list", "find Alice", "list"), executedCommands);
            assertEquals("", commandTextField.getText());
            pressKey(KeyCode.UP);
            assertEquals("list", commandTextField.getText());
            pressKey(KeyCode.DOWN);
            assertEquals("", commandTextField.getText());
        });
    }

    @Test
    public void edit_recalledCommand_endsBrowsingWithoutChangingHistory() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            submit("find Alice");
            commandTextField.setText("old draft");
            pressKey(KeyCode.UP);
            pressKey(KeyCode.UP);
            commandTextField.appendText(" edited");

            pressKey(KeyCode.DOWN);
            assertEquals("list edited", commandTextField.getText());
            pressKey(KeyCode.UP);
            assertEquals("find Alice", commandTextField.getText());
            pressKey(KeyCode.DOWN);
            assertEquals("list edited", commandTextField.getText());
            pressKey(KeyCode.UP);
            pressKey(KeyCode.UP);
            assertEquals("list", commandTextField.getText());
        });
    }

    @Test
    public void submit_parseFailure_keepsTextAndRecordsCommand() throws Exception {
        runOnFxThread(() -> assertFailedCommandRecall(command -> {
            throw new ParseException("Invalid command");
        }));
    }

    @Test
    public void submit_executionFailure_keepsTextAndRecordsCommand() throws Exception {
        runOnFxThread(() -> assertFailedCommandRecall(command -> {
            throw new CommandException("Command failed");
        }));
    }

    @Test
    public void submit_blankInput_doesNotExecuteOrRecord() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            submit("");
            submit(" \t ");
            assertEquals(List.of("list"), executedCommands);
            pressKey(KeyCode.UP);
            assertEquals("list", commandTextField.getText());
        });
    }

    @Test
    public void navigate_modifiedArrows_doesNotRecallHistory() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            commandTextField.setText("draft");
            for (KeyCode code : List.of(KeyCode.UP, KeyCode.DOWN)) {
                commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                        true, false, false, false));
                commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                        false, true, false, false));
                commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                        false, false, true, false));
                commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                        false, false, false, true));
                assertEquals("draft", commandTextField.getText());
            }
            pressKey(KeyCode.UP);
            assertEquals("list", commandTextField.getText());
        });
    }

    @Test
    public void submit_repeatedFailure_keepsOneErrorStyleAndOneHistoryEntry() throws Exception {
        runOnFxThread(() -> {
            createCommandBox(command -> {
                executedCommands.add(command);
                throw new CommandException("Command failed");
            });
            submit("failed command");
            commandTextField.fireEvent(new ActionEvent());

            assertEquals(List.of("failed command", "failed command"), executedCommands);
            assertEquals("failed command", commandTextField.getText());
            assertEquals(1L, commandTextField.getStyleClass().stream()
                    .filter(CommandBox.ERROR_STYLE_CLASS::equals).count());
            commandTextField.setText("draft");
            pressKey(KeyCode.UP);
            pressKey(KeyCode.UP);
            assertEquals("failed command", commandTextField.getText());
            pressKey(KeyCode.DOWN);
            assertEquals("draft", commandTextField.getText());
        });
    }

    @Test
    public void navigate_modifiedDownWhileBrowsing_keepsPositionAndDraft() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            submit("find Alice");
            commandTextField.setText("draft");
            pressKey(KeyCode.UP);
            pressKey(KeyCode.UP);

            for (int modifier = 0; modifier < 4; modifier++) {
                commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN,
                        modifier == 0, modifier == 1, modifier == 2, modifier == 3));
                assertEquals("list", commandTextField.getText());
            }
            pressKey(KeyCode.DOWN);
            assertEquals("find Alice", commandTextField.getText());
            pressKey(KeyCode.DOWN);
            assertEquals("draft", commandTextField.getText());
        });
    }

    @Test
    public void navigate_otherControl_doesNotChangeCommandField() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            commandTextField.setText("draft");
            TextField otherField = new TextField("other text");
            StackPane root = (StackPane) commandBox.getRoot();
            root.getChildren().add(otherField);
            otherField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
                    false, false, false, false));
            assertEquals("draft", commandTextField.getText());
            assertEquals("other text", otherField.getText());
        });
    }

    @Test
    public void pressKey_leftArrow_preservesCaretMovement() throws Exception {
        runOnFxThread(() -> {
            submit("list");
            pressKey(KeyCode.UP);
            pressKey(KeyCode.LEFT);
            assertEquals("list", commandTextField.getText());
            assertEquals(3, commandTextField.getCaretPosition());
        });
    }

    /**
     * Checks that a failed submission stays editable and can be recalled later.
     */
    private void assertFailedCommandRecall(CommandBox.CommandExecutor executor) {
        createCommandBox(executor);
        submit("failed command");
        assertEquals("failed command", commandTextField.getText());
        assertTrue(commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));

        commandTextField.setText("new draft");
        assertFalse(commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        pressKey(KeyCode.UP);
        assertEquals("failed command", commandTextField.getText());
        pressKey(KeyCode.DOWN);
        assertEquals("new draft", commandTextField.getText());
    }

    /**
     * Loads the command field and initializes its normal JavaFX keyboard behaviour.
     */
    private void createCommandBox(CommandBox.CommandExecutor executor) {
        commandBox = new CommandBox(executor);
        Scene scene = new Scene(commandBox.getRoot());
        commandBox.getRoot().applyCss();
        commandTextField = (TextField) scene.lookup("#commandTextField");
    }

    /**
     * Replaces the input and triggers the action used by Enter.
     */
    private void submit(String command) {
        commandTextField.setText(command);
        commandTextField.fireEvent(new ActionEvent());
    }

    /**
     * Sends an unmodified key press through the command field's event handlers.
     */
    private void pressKey(KeyCode code) {
        commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                false, false, false, false));
    }

    /**
     * Runs UI assertions on the application thread with a bounded wait.
     */
    private void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
