package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.testutil.AddressBookBuilder;

/**
 * Runs in its own Gradle test process and working directory because production shutdown exits JavaFX.
 */
public class MainAppStartupTest {
    @Test
    public void init_validData_enablesCommandsAndSavesPreferences() throws Exception {
        Path dataFile = Path.of("data/cindy.json").toAbsolutePath();
        AddressBook saved = new AddressBookBuilder().withPerson(ALICE).build();
        new JsonAddressBookStorage(dataFile).saveAddressBook(saved);
        MainApp app = new MainApp();

        app.init();

        assertEquals(saved, app.model.getAddressBook());
        assertEquals("Listed 1 client", app.logic.execute(ListCommand.COMMAND_WORD).getFeedbackToUser());
        app.stop();
        assertEquals(app.model.getUserPrefs(), app.storage.readUserPrefs().orElseThrow());
        assertEquals(saved, app.storage.readAddressBook().orElseThrow());
    }

    @Test
    public void startup_nullRootData_showsRecoveryAndPreservesFile() throws Exception {
        Path dataFile = Path.of("data/cindy.json").toAbsolutePath();
        Files.createDirectories(dataFile.getParent());
        String unreadableData = "null";
        Files.writeString(dataFile, unreadableData);
        MainApp app = new MainApp();

        app.init();

        assertNull(app.model, "Unreadable data must not produce a replacement model");
        assertNull(app.logic, "Commands must not be enabled after loading fails");
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Stage primaryStage = new Stage();
            FutureTask<Void> inspectDialog = new FutureTask<>(() -> {
                DialogPane dialog = Window.getWindows().stream()
                        .filter(Window::isShowing)
                        .map(window -> window.getScene().getRoot())
                        .filter(root -> root instanceof DialogPane)
                        .map(root -> (DialogPane) root)
                        .findFirst().orElseThrow();
                try {
                    assertTrue(dialog.getContentText().contains(dataFile.toString()));
                    assertTrue(dialog.getContentText().contains("repair it or restore a valid backup"));
                    assertFalse(primaryStage.isShowing(), "The command window must remain closed");
                } finally {
                    Button close = (Button) dialog.lookupButton(ButtonType.OK);
                    close.fire();
                }
                return null;
            });
            Platform.runLater(inspectDialog);
            app.start(primaryStage);
            inspectDialog.get(10, TimeUnit.SECONDS);
            app.stop();
            return null;
        });
        Platform.startup(() -> Platform.setImplicitExit(false));
        // Open the modal dialog from the event queue, after native toolkit initialization returns.
        Platform.runLater(startup);
        startup.get(15, TimeUnit.SECONDS);

        assertEquals(unreadableData, Files.readString(dataFile));
    }
}
