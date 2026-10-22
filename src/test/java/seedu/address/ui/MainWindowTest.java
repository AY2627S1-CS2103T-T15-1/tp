package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.session.Session;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

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
            String longNote = "A long training note that should wrap and remain accessible. ".repeat(100);
            Person aliceWithSessions = new PersonBuilder(ALICE).withSessions(
                    new Session(LocalDate.of(2026, 8, 1), "This older session should stay hidden"),
                    new Session(LocalDate.of(2026, 10, 22), "Worked on squat form"),
                    new Session(LocalDate.of(2026, 12, 31), longNote),
                    new Session(LocalDate.of(2026, 9, 10), "Improved mobility"),
                    new Session(LocalDate.of(2026, 11, 15), ""))
                    .withNote("Client note")
                    .withWorkoutPlan("Squats and rest")
                    .build();
            Person clientWithoutSessions = new PersonBuilder()
                    .withId(2)
                    .withName("Client Without Sessions")
                    .withPhone("87654321")
                    .build();
            model.addPerson(aliceWithSessions);
            model.addPerson(clientWithoutSessions);
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
                command.setText("view --id 1");
                command.fireEvent(new ActionEvent());
                assertEquals("", command.getText());
                assertEquals("Viewing client: Alice Pauline (ID: 1); Phone: 94351253", feedback.getText());
                assertTrue(details.isVisible());
                assertFalse(list.isVisible());
                assertEquals("Alice Pauline", ((Label) details.lookup("#name")).getText());
                assertEquals("94351253", ((Label) details.lookup("#phone")).getText());
                assertEquals("#1", ((Label) details.lookup("#id")).getText());
                assertEquals("Client note", ((Label) details.lookup("#note")).getText());
                assertTrue(details.lookup("#noteSection").isVisible());
                assertEquals("Squats and rest", ((Label) details.lookup("#workoutPlanText")).getText());
                assertTrue(details.lookup("#workoutPlanCard").isVisible());
                VBox sessionList = (VBox) details.lookup("#sessionList");
                assertEquals(4, sessionList.getChildren().size());
                assertSessionRow((VBox) sessionList.getChildren().get(0), "2026-12-31", longNote);
                assertSessionRow((VBox) sessionList.getChildren().get(1), "2026-11-15", "No note");
                assertSessionRow((VBox) sessionList.getChildren().get(2), "2026-10-22", "Worked on squat form");
                assertSessionRow((VBox) sessionList.getChildren().get(3), "2026-09-10", "Improved mobility");
                stage.setWidth(500);
                stage.setHeight(600);
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                ScrollPane sessionScroll = (ScrollPane) details.lookup("#sessionScrollPane");
                assertTrue(sessionScroll.isFitToWidth());
                assertTrue(sessionScroll.getContent().getLayoutBounds().getHeight()
                        > sessionScroll.getViewportBounds().getHeight());
                sessionScroll.setVvalue(sessionScroll.getVmax());
                stage.getScene().getRoot().layout();
                assertEquals(sessionScroll.getVmax(), sessionScroll.getVvalue());

                command.setText("view --id 99");
                command.fireEvent(new ActionEvent());
                assertTrue(details.isVisible());
                assertEquals("Alice Pauline", ((Label) details.lookup("#name")).getText());

                command.setText("view --id 2");
                command.fireEvent(new ActionEvent());
                assertEquals(1, sessionList.getChildren().size());
                assertEquals("No sessions recorded", ((Label) sessionList.getChildren().get(0)).getText());
                assertFalse(details.lookup("#noteSection").isVisible());
                assertFalse(details.lookup("#workoutPlanCard").isVisible());

                command.setText("list");
                command.fireEvent(new ActionEvent());
                assertEquals("Listed 2 clients", feedback.getText());
                assertEquals("", command.getText());
                assertTrue(list.isVisible());
                assertTrue(stage.getScene().lookup("#menuBar").isVisible());
                assertTrue(stage.getScene().lookup("#statusbarPlaceholder").isVisible());
                assertFalse(details.isVisible());

                // Recall must leave the real command result and displayed client list unchanged.
                command.setText("unfinished command");
                command.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
                        false, false, false, false));
                assertEquals("list", command.getText());
                command.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
                        false, false, false, false));
                assertEquals("view --id 2", command.getText());
                assertEquals("Listed 2 clients", feedback.getText());
                assertTrue(list.isVisible());
                assertFalse(details.isVisible());
                command.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN,
                        false, false, false, false));
                assertEquals("list", command.getText());
                command.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN,
                        false, false, false, false));
                assertEquals("unfinished command", command.getText());

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

    private static void assertSessionRow(VBox row, String expectedDate, String expectedNote) {
        assertEquals(expectedDate, ((Label) row.getChildren().get(0)).getText());
        assertEquals(expectedNote, ((Label) row.getChildren().get(1)).getText());
        assertTrue(((Label) row.getChildren().get(1)).isWrapText());
    }

}
