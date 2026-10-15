package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

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
            ModelManager model = new ModelManager();
            model.addPerson(ALICE);
            LogicManager logic = new LogicManager(model, storage);
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
                StackPane list = (StackPane) stage.getScene().lookup("#personListPanelPlaceholder");
                assertFalse(details.isVisible());
                assertTrue(list.isVisible());
                assertTrue(stage.getScene().lookup("#menuBar").isVisible());
                assertTrue(stage.getScene().lookup("#statusbarPlaceholder").isVisible());

                TextField command = (TextField) stage.getScene().lookup("#commandTextField");
                TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
                command.setText("view 1");
                command.fireEvent(new ActionEvent());
                assertEquals("", command.getText());
                assertEquals("Viewing person: Alice Pauline; Phone: 94351253", feedback.getText());
                assertTrue(details.isVisible());
                assertFalse(list.isVisible());
                assertEquals("Alice Pauline", ((Label) details.lookup("#name")).getText());
                assertEquals("94351253", ((Label) details.lookup("#phone")).getText());

                command.setText("view 99");
                command.fireEvent(new ActionEvent());
                assertTrue(details.isVisible());
                assertEquals("Alice Pauline", ((Label) details.lookup("#name")).getText());

                command.setText("list");
                command.fireEvent(new ActionEvent());
                assertEquals("Listed 1 client", feedback.getText());
                assertEquals("", command.getText());
                assertTrue(list.isVisible());
                assertTrue(stage.getScene().lookup("#menuBar").isVisible());
                assertTrue(stage.getScene().lookup("#statusbarPlaceholder").isVisible());
                assertFalse(details.isVisible());

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
