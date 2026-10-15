package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Checks that the main layout supports startup and command entry together.
 */
public class MainWindowTest {
    @TempDir
    public Path testFolder;

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

    @Test
    public void fillInnerParts_validLayout_displaysDetailsAndAcceptsCommands() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Path dataFile = testFolder.resolve("addressbook.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                    new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
            LogicManager logic = new LogicManager(new ModelManager(), storage);
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, dataFile);
                window.show();
                window.fillInnerParts();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                StackPane details = (StackPane) stage.getScene().lookup("#personDetailPanelPlaceholder");
                assertNotNull(details);
                assertFalse(details.getChildren().isEmpty());
                Label message = (Label) details.lookup("#placeholderMessage");
                assertEquals("No client selected.\nUse view INDEX to see a client's details.", message.getText());
                assertTrue(details.isVisible());
                assertTrue(details.getWidth() > 0);
                assertTrue(details.getHeight() > 0);

                TextField command = (TextField) stage.getScene().lookup("#commandTextField");
                TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
                command.setText("list");
                command.fireEvent(new ActionEvent());
                assertEquals("No clients yet. Use `add` to add your first client.", feedback.getText());
                assertEquals("", command.getText());

                command.setText("exit");
                command.fireEvent(new ActionEvent());
                assertEquals("Exiting Address Book as requested ...", feedback.getText());
                assertFalse(stage.isShowing());
            } finally {
                stage.hide();
            }
        }, null);
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
