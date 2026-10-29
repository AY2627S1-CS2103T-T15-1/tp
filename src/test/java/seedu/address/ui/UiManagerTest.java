package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

public class UiManagerTest {
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
    public void showDataLoadingError_displaysFileAndRecoveryInstructions() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Path dataFile = Path.of("data", "cindy.json").toAbsolutePath();
            FutureTask<Void> inspectDialog = new FutureTask<>(() -> {
                DialogPane dialog = Window.getWindows().stream()
                        .filter(Window::isShowing)
                        .map(window -> window.getScene().getRoot())
                        .filter(root -> root instanceof DialogPane)
                        .map(root -> (DialogPane) root)
                        .findFirst().orElseThrow();
                try {
                    assertTrue(dialog.getContentText().contains(dataFile.toString()));
                    assertTrue(dialog.getContentText().contains("file has not been changed"));
                    assertTrue(dialog.getContentText().contains("repair it or restore a valid backup"));
                } finally {
                    Button closeButton = (Button) dialog.lookupButton(ButtonType.OK);
                    closeButton.fire();
                }
                return null;
            });
            Platform.runLater(inspectDialog);
            UiManager.showDataLoadingError(dataFile);
            inspectDialog.get(10, TimeUnit.SECONDS);
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
