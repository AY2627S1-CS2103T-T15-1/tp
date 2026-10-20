package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

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
