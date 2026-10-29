package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.PersonBuilder;

public class PersonListPanelTest {
    private static final PseudoClass FIRST_CONTACT = PseudoClass.getPseudoClass("first-contact");
    private static final PseudoClass LAST_CONTACT = PseudoClass.getPseudoClass("last-contact");

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
    public void layout_bottomOfList_keepsAdjacentCardsSeparate() throws Exception {
        runOnFxThread(() -> {
            for (int width : new int[] {410, 686}) {
                PersonListPanel panel = new PersonListPanel(
                        FXCollections.observableArrayList(SampleDataUtil.getSamplePersons()));
                Region root = panel.getRoot();
                Scene scene = new Scene(root, width, 300);
                for (String stylesheet : List.of("DarkTheme.css", "Extensions.css", "ContactTheme.css")) {
                    scene.getStylesheets().add(
                            PersonListPanel.class.getResource("/view/" + stylesheet).toExternalForm());
                }
                ListView<Person> view = getListView(panel);
                for (int height : new int[] {200, 300, 420, 500}) {
                    root.resize(width, height);
                    root.applyCss();
                    root.layout();
                    view.scrollTo(view.getItems().size() - 1);
                    root.applyCss();
                    root.layout();
                    List<ListCell<?>> cells = view.lookupAll(".list-cell").stream()
                            .<ListCell<?>>map(node -> (ListCell<?>) node)
                            .filter(cell -> !cell.isEmpty())
                            .sorted(Comparator.comparingInt(ListCell::getIndex)).toList();
                    assertTrue(cells.stream().anyMatch(cell -> cell.getIndex() == view.getItems().size() - 1));
                    for (int i = 1; i < cells.size(); i++) {
                        Node previous = cells.get(i - 1).getGraphic();
                        Node current = cells.get(i).getGraphic();
                        Bounds previousBounds = previous.localToScene(previous.getBoundsInLocal());
                        Bounds currentBounds = current.localToScene(current.getBoundsInLocal());
                        assertTrue(previousBounds.getMaxY() <= currentBounds.getMinY() + 1,
                                "Cards " + cells.get(i - 1).getIndex() + " and " + cells.get(i).getIndex()
                                        + " overlap at " + width + "x" + height);
                    }
                }
            }
        });
    }

    @Test
    public void layout_longInactiveName_wrapsWithoutHidingIdOrStatus() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder(ALICE).withActive(false)
                    .withName("Muhammad Abdul Rahman bin Abdullah Hassan Ibrahim Ismail Jonathan Tan Wei Ming").build();
            for (int width : new int[] {410, 686}) {
                assertCardFitsViewport(person, width, true);
            }
        });
    }

    @Test
    public void layout_shortNameAtMinimumWindowWidth_keepsIdVisible() throws Exception {
        runOnFxThread(() -> assertCardFitsViewport(ALICE, 410, false));
    }

    private void assertCardFitsViewport(Person person, int width, boolean wraps) {
        PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(person));
        Region root = panel.getRoot();
        Scene scene = new Scene(root, width, 500);
        for (String stylesheet : List.of("DarkTheme.css", "Extensions.css", "ContactTheme.css")) {
            scene.getStylesheets().add(PersonListPanel.class.getResource("/view/" + stylesheet).toExternalForm());
        }
        root.resize(width, 500);
        root.applyCss();
        root.layout();
        ListView<Person> view = getListView(panel);
        Node viewport = view.lookup(".clipped-container");
        Bounds visible = viewport.localToScene(viewport.getBoundsInLocal());
        for (String selector : List.of("#name", "#id", "#inactiveStatus")) {
            Label label = (Label) view.lookup(selector);
            if (!label.isVisible()) {
                continue;
            }
            Bounds bounds = label.localToScene(label.getBoundsInLocal());
            assertTrue(bounds.getMinX() >= visible.getMinX() - 1, selector + " is outside the left edge");
            assertTrue(bounds.getMaxX() <= visible.getMaxX() + 1, selector + " is outside the right edge");
            assertTrue(bounds.getMaxY() <= visible.getMaxY() + 1, selector + " is outside the bottom edge");
        }
        Label name = (Label) view.lookup("#name");
        assertEquals(person.getName().fullName, name.getText());
        assertTrue(name.getHeight() >= name.prefHeight(name.getWidth()) - 1, "Name must not be clipped");
        ListCell<?> cell = view.lookupAll(".list-cell").stream()
                .map(node -> (ListCell<?>) node).filter(item -> !item.isEmpty())
                .findFirst().orElseThrow();
        Bounds cardBounds = cell.getGraphic().localToScene(cell.getGraphic().getBoundsInLocal());
        Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
        assertTrue(cardBounds.getMaxY() <= cellBounds.getMaxY() + 1, "Row must grow to fit the card");
        Label phone = (Label) view.lookup("#phone");
        assertTrue(phone.localToScene(phone.getBoundsInLocal()).getMinY()
                >= name.localToScene(name.getBoundsInLocal()).getMaxY(), "Phone must appear below the name");
        if (wraps) {
            assertTrue(name.getHeight() > name.prefHeight(-1), "Long name must wrap onto multiple lines");
            assertTrue(view.lookup("#inactiveStatus").isVisible());
        }
        assertEquals("#" + person.getId(), ((Label) view.lookup("#id")).getText());
        for (Node node : view.lookupAll(".scroll-bar")) {
            ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation() == Orientation.HORIZONTAL) {
                assertFalse(bar.isVisible(), "Client list must fit without horizontal scrolling");
            }
        }
    }

    @Test
    public void constructor_personList_usesObservableItems() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE);
            PersonListPanel panel = new PersonListPanel(persons);
            ListView<Person> view = getListView(panel);

            assertSame(persons, view.getItems());
            assertTrue(view.getCellFactory().call(view) instanceof PersonListPanel.PersonListViewCell);
        });
    }

    @Test
    public void updateItem_person_displaysDetailsAndId() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(BENSON, ALICE));
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            // Benson is first in the list, but the card shows his ID rather than his position
            cell.updateIndex(0);
            cell.updateItem(BENSON, false);

            assertEquals("Benson Meier", ((Label) cell.getGraphic().lookup("#name")).getText());
            assertEquals("98765432", ((Label) cell.getGraphic().lookup("#phone")).getText());
            assertEquals("#" + BENSON.getId(), ((Label) cell.getGraphic().lookup("#id")).getText());
        });
    }

    @Test
    public void updateItem_multiplePersons_marksOnlyOuterCorners() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(ALICE, BENSON, CARL));
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();

            cell.updateIndex(0);
            cell.updateItem(ALICE, false);
            assertTrue(cell.getPseudoClassStates().contains(FIRST_CONTACT));
            assertFalse(cell.getPseudoClassStates().contains(LAST_CONTACT));

            cell.updateIndex(1);
            cell.updateItem(BENSON, false);
            assertFalse(cell.getPseudoClassStates().contains(FIRST_CONTACT));
            assertFalse(cell.getPseudoClassStates().contains(LAST_CONTACT));

            cell.updateIndex(2);
            cell.updateItem(CARL, false);
            assertFalse(cell.getPseudoClassStates().contains(FIRST_CONTACT));
            assertTrue(cell.getPseudoClassStates().contains(LAST_CONTACT));
        });
    }

    @Test
    public void updateItem_singlePerson_marksBothOuterCorners() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(ALICE));
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateIndex(0);
            cell.updateItem(ALICE, false);

            assertTrue(cell.getPseudoClassStates().contains(FIRST_CONTACT));
            assertTrue(cell.getPseudoClassStates().contains(LAST_CONTACT));
        });
    }

    @Test
    public void updateItem_emptyCell_clearsPreviousContentAndCorners() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(ALICE));
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateIndex(0);
            cell.updateItem(ALICE, false);
            cell.setText("previous cell text");

            cell.updateItem(null, true);

            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            assertFalse(cell.getPseudoClassStates().contains(FIRST_CONTACT));
            assertFalse(cell.getPseudoClassStates().contains(LAST_CONTACT));
        });
    }

    @Test
    public void updateItem_nullPerson_clearsPreviousContent() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(ALICE));
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateItem(ALICE, false);
            cell.setText("previous cell text");

            cell.updateItem(null, false);

            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }

    @Test
    public void personList_addAndRemove_updatesVisibleEndCards() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BENSON);
            PersonListPanel panel = new PersonListPanel(persons);
            Region root = panel.getRoot();
            new Scene(root, 400, 500);
            root.resize(400, 500);
            assertVisibleCorners(root, 2);

            persons.add(CARL);
            assertVisibleCorners(root, 3);

            persons.remove(CARL);
            assertVisibleCorners(root, 2);
        });
    }

    private void assertVisibleCorners(Region root, int expectedCount) {
        root.applyCss();
        root.layout();
        List<ListCell<?>> cells = root.lookupAll(".list-cell").stream()
                .<ListCell<?>>map(node -> (ListCell<?>) node)
                .filter(cell -> !cell.isEmpty())
                .toList();
        assertEquals(expectedCount, cells.size());
        for (ListCell<?> cell : cells) {
            assertEquals(cell.getIndex() == 0, cell.getPseudoClassStates().contains(FIRST_CONTACT));
            assertEquals(cell.getIndex() == expectedCount - 1, cell.getPseudoClassStates().contains(LAST_CONTACT));
        }
    }

    @SuppressWarnings("unchecked")
    private ListView<Person> getListView(PersonListPanel panel) {
        return (ListView<Person>) panel.getRoot().lookup("#personListView");
    }

    private void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
