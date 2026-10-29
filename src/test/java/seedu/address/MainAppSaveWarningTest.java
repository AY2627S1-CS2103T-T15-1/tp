package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Checks that the first-launch save failure is visible before the command window opens.
 */
public class MainAppSaveWarningTest {
    @TempDir
    public Path temporaryFolder;

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
            Platform.runLater(initialize);
        }
        assertTrue(started.await(10, TimeUnit.SECONDS), "JavaFX startup timed out");
    }

    @Test
    public void start_initialSaveFails_warnsBeforeOpeningUi() throws Exception {
        Path blockedParent = temporaryFolder.resolve("data");
        Files.writeString(blockedParent, "A file occupies the data directory path");
        Path dataFile = blockedParent.resolve("cindy.json");
        MainApp app = initializeApp(dataFile);
        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
        assertFalse(Files.exists(dataFile));
        AtomicBoolean warningSeen = new AtomicBoolean();
        AtomicBoolean uiStarted = new AtomicBoolean();

        FutureTask<Void> startup = new FutureTask<>(() -> {
            app.ui = stage -> {
                assertTrue(warningSeen.get(), "The save warning must appear before the command window");
                uiStarted.set(true);
            };
            FutureTask<Void> inspectDialog = new FutureTask<>(() -> {
                DialogPane dialog = Window.getWindows().stream()
                        .filter(Window::isShowing)
                        .map(window -> window.getScene().getRoot())
                        .filter(root -> root instanceof DialogPane)
                        .map(root -> (DialogPane) root)
                        .findFirst().orElseThrow();
                try {
                    assertTrue(dialog.getContentText().contains(dataFile.toString()));
                    assertTrue(dialog.getContentText().contains("not been saved"));
                    assertTrue(dialog.getContentText().contains("write permission"));
                    assertTrue(dialog.getContentText().contains("free space"));
                    assertFalse(uiStarted.get());
                    warningSeen.set(true);
                } finally {
                    Button closeButton = (Button) dialog.lookupButton(ButtonType.OK);
                    closeButton.fire();
                }
                return null;
            });
            Platform.runLater(inspectDialog);
            app.start(new Stage());
            inspectDialog.get(10, TimeUnit.SECONDS);
            assertTrue(uiStarted.get(), "Acknowledging the warning must allow browsing samples");
            return null;
        });
        Platform.runLater(startup);
        startup.get(15, TimeUnit.SECONDS);
        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
    }

    @Test
    public void start_initialSaveSucceeds_opensUiWithoutWarning() throws Exception {
        MainApp app = initializeApp(temporaryFolder.resolve("data/cindy.json"));

        assertStartsWithoutWarning(app);
        assertEquals(app.model.getAddressBook(), app.storage.readAddressBook().orElseThrow());
    }

    @Test
    public void start_existingData_opensUiWithoutWarning() throws Exception {
        Path dataFile = temporaryFolder.resolve("data/cindy.json");
        new JsonAddressBookStorage(dataFile).saveAddressBook(SampleDataUtil.getSampleAddressBook());
        MainApp app = initializeApp(dataFile);

        assertStartsWithoutWarning(app);
    }

    private MainApp initializeApp(Path dataFile) throws Exception {
        MainApp app = new MainApp();
        app.storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        app.model = app.initModelManager(app.storage, new UserPrefs());
        return app;
    }

    private void assertStartsWithoutWarning(MainApp app) throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            AtomicBoolean uiStarted = new AtomicBoolean();
            app.ui = stage -> uiStarted.set(true);
            app.start(new Stage());
            assertTrue(uiStarted.get());
            assertFalse(Window.getWindows().stream().filter(Window::isShowing)
                    .anyMatch(window -> window.getScene().getRoot() instanceof DialogPane));
            return null;
        });
        Platform.runLater(startup);
        startup.get(15, TimeUnit.SECONDS);
    }
}
