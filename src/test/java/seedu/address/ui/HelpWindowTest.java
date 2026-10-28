package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Checks the real help layout, its command reference, and its existing guide controls.
 */
public class HelpWindowTest {
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
    public void constructor_commandCatalog_displaysEverySummaryInOrder() throws Exception {
        runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                new HelpWindow(stage);
                ScrollPane scroll = (ScrollPane) stage.getScene().lookup("#commandScroll");
                assertNotNull(scroll);
                VBox summaries = (VBox) scroll.getContent().lookup("#commandSummaries");
                assertNotNull(summaries);
                List<Node> cards = summaries.getChildren().stream()
                        .filter(node -> node.getStyleClass().contains("help-command-card")).toList();
                assertEquals(HelpContent.getCommands().size(), cards.size());
                for (int i = 0; i < cards.size(); i++) {
                    CommandSummary summary = HelpContent.getCommands().get(i);
                    Parent card = (Parent) cards.get(i);
                    String title = summary.getCommandWord() + (summary.isAvailable() ? "" : " (coming soon)");
                    assertEquals(title, ((Label) card.lookup(".help-command-name")).getText());
                    assertEquals(summary.getDescription(), ((Label) card.lookup(".help-description")).getText());
                    assertEquals("Format: " + summary.getSyntax(), ((Label) card.lookup(".help-syntax")).getText());
                    assertEquals("Example: " + summary.getExample(), ((Label) card.lookup(".help-example")).getText());
                }
                assertEquals(8, summaries.getChildren().stream()
                        .filter(node -> node.getStyleClass().contains("help-category")).count());
                assertEquals(27, cards.size());
                Parent todayListCard = (Parent) cards.stream()
                        .filter(card -> ((Label) card.lookup(".help-syntax"))
                                .getText().equals("Format: list --today"))
                        .findFirst().orElseThrow();
                assertEquals("list", ((Label) todayListCard.lookup(".help-command-name")).getText());
            } finally {
                stage.hide();
            }
        });
    }

    @Test
    public void show_smallWindow_wrapsCommandsAndKeepsGuideOutsideScrollArea() throws Exception {
        runOnFxThread(() -> {
            HelpWindow window = new HelpWindow();
            Stage stage = window.getRoot();
            try {
                window.show();
                assertTrue(stage.isResizable());
                Region root = (Region) stage.getScene().getRoot();
                root.resize(480, 420);
                root.applyCss();
                root.layout();
                ScrollPane scroll = (ScrollPane) root.lookup("#commandScroll");
                assertNotNull(scroll);
                assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scroll.getHbarPolicy());
                assertTrue(scroll.getContent().getBoundsInLocal().getHeight() > scroll.getViewportBounds().getHeight());
                assertTrue(scroll.getContent().getBoundsInLocal().getWidth() <= scroll.getViewportBounds().getWidth());
                for (Node node : root.lookupAll(".help-syntax")) {
                    Label label = (Label) node;
                    assertTrue(label.isWrapText());
                    assertTrue(label.getWidth() <= scroll.getViewportBounds().getWidth());
                    assertTrue(label.getHeight() >= label.prefHeight(label.getWidth()) - 1);
                }
                Button copy = (Button) root.lookup("#copyButton");
                Bounds before = copy.localToScene(copy.getBoundsInLocal());
                scroll.setVvalue(1);
                root.layout();
                assertEquals(before, copy.localToScene(copy.getBoundsInLocal()));
                Node shortcuts = root.lookup("#keyboardShortcuts");
                Bounds visible = scroll.lookup(".viewport").localToScene(scroll.lookup(".viewport").getBoundsInLocal());
                Bounds last = shortcuts.localToScene(shortcuts.getBoundsInLocal());
                assertTrue(last.getMinY() >= visible.getMinY() - 1);
                assertTrue(last.getMaxY() <= visible.getMaxY() + 1);
            } finally {
                stage.hide();
            }
        });
    }

    @Test
    public void copyUrl_buttonPressed_copiesUserGuideUrl() throws Exception {
        runOnFxThread(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> originalContent = new HashMap<>();
            for (DataFormat format : clipboard.getContentTypes()) {
                originalContent.put(format, clipboard.getContent(format));
            }
            Stage stage = new Stage();
            try {
                new HelpWindow(stage);
                Button copyButton = (Button) stage.getScene().lookup("#copyButton");
                copyButton.fire();
                assertEquals(HelpWindow.USERGUIDE_URL, clipboard.getString());
                assertEquals(HelpWindow.HELP_MESSAGE, ((Label) stage.getScene().lookup("#helpMessage")).getText());
            } finally {
                clipboard.setContent(originalContent);
                stage.hide();
            }
        });
    }

    @Test
    public void show_hideAndReopen_reusesWindowWithoutDuplicatingCommands() throws Exception {
        runOnFxThread(() -> {
            HelpWindow window = new HelpWindow();
            Stage stage = window.getRoot();
            try {
                window.show();
                assertTrue(window.isShowing());
                int originalSize = ((VBox) stage.getScene().lookup("#commandSummaries")).getChildren().size();
                window.hide();
                assertFalse(window.isShowing());
                window.show();
                window.focus();
                assertTrue(window.isShowing());
                assertEquals(stage, window.getRoot());
                assertEquals(originalSize, ((VBox) stage.getScene().lookup("#commandSummaries")).getChildren().size());
            } finally {
                stage.hide();
            }
        });
    }

    private void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
