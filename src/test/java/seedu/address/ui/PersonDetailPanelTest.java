package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.appointment.Appointment;
import seedu.address.testutil.PersonBuilder;

public class PersonDetailPanelTest {

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
    public void showPerson_withPlan_displaysCardAndContactDetails() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(new PersonBuilder(ALICE).withWorkoutPlan("Squats: 3 sets of 12").build());

            Region content = (Region) ((ScrollPane) panel.getRoot()).getContent();
            Region card = (Region) content.lookup("#workoutPlanCard");
            assertTrue(card.isVisible());
            assertTrue(card.isManaged());
            assertEquals("Squats: 3 sets of 12", getPlanLabel(panel).getText());
            assertEquals("Alice Pauline", ((Label) content.lookup("#name")).getText());
            assertEquals(ALICE.getPhone().value, ((Label) content.lookup("#phone")).getText());
        });
    }

    @Test
    public void showPerson_switchOrClearPlan_hidesCardWithoutLeavingStaleText() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            for (String emptyPlan : new String[] {"", "   "}) {
                panel.showPerson(new PersonBuilder(ALICE).withWorkoutPlan("Existing plan").build());
                panel.showPerson(new PersonBuilder(BENSON).withWorkoutPlan(emptyPlan).build());

                Region content = (Region) ((ScrollPane) panel.getRoot()).getContent();
                Region card = (Region) content.lookup("#workoutPlanCard");
                assertFalse(card.isVisible());
                assertFalse(card.isManaged());
                assertEquals("", getPlanLabel(panel).getText());
                assertEquals("Benson Meier", ((Label) content.lookup("#name")).getText());
            }
        });
    }

    @Test
    public void showPerson_replacedPlan_displaysNewText() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(new PersonBuilder(ALICE).withWorkoutPlan("Old plan").build());
            panel.showPerson(new PersonBuilder(ALICE).withWorkoutPlan("New plan").build());
            assertEquals("New plan", getPlanLabel(panel).getText());
        });
    }

    @Test
    public void showPerson_longPlan_wrapsAndScrollsInNarrowWindow() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(new PersonBuilder(ALICE).withWorkoutPlan("Squats: 3 sets of 12. ".repeat(100)).build());
            ScrollPane scroll = (ScrollPane) panel.getRoot();
            new Scene(scroll, 360, 300);
            scroll.resize(360, 300);
            scroll.applyCss();
            scroll.layout();

            Label text = getPlanLabel(panel);
            assertTrue(text.isWrapText());
            assertTrue(text.getWidth() <= scroll.getViewportBounds().getWidth());
            assertTrue(text.getHeight() > scroll.getViewportBounds().getHeight());
            assertTrue(scroll.getContent().getBoundsInLocal().getHeight() > scroll.getViewportBounds().getHeight());
        });
    }

    @Test
    public void showPerson_nullPerson_throwsNullPointerException() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            assertThrows(NullPointerException.class, () -> panel.showPerson(null));
        });
    }

    @Test
    public void showPerson_blankNote_hidesSectionWhilePreservingWorkoutPlan() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            for (String text : new String[] {"", " \t\n"}) {
                panel.showPerson(new PersonBuilder(ALICE).withNote("Previous note").build());
                panel.showPerson(new PersonBuilder(ALICE).withNote(text).withWorkoutPlan("Squats").build());
                Region content = (Region) ((ScrollPane) panel.getRoot()).getContent();
                assertFalse(content.lookup("#noteSection").isVisible());
                assertFalse(content.lookup("#noteSection").isManaged());
                assertTrue(content.lookup("#workoutPlanCard").isVisible());
                assertEquals("Squats", getPlanLabel(panel).getText());
            }
        });
    }

    @Test
    public void showPerson_longMultilineNote_wrapsAndScrolls() throws Exception {
        runOnFxThread(() -> {
            String text = "Training preferences. ".repeat(100) + "\nRest";
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(new PersonBuilder(ALICE).withNote(text).build());
            ScrollPane scroll = (ScrollPane) panel.getRoot();
            new Scene(scroll, 360, 300);
            scroll.resize(360, 300);
            scroll.applyCss();
            scroll.layout();

            Label label = (Label) scroll.getContent().lookup("#note");
            assertEquals(text, label.getText());
            assertTrue(label.isWrapText());
            assertTrue(label.getWidth() <= scroll.getViewportBounds().getWidth());
            assertTrue(label.getHeight() > scroll.getViewportBounds().getHeight());
        });
    }

    @Test
    public void showPerson_allDetails_displaysNoteTagsStatusAndPlan() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(new PersonBuilder(ALICE).withNote("Knee injury").withActive(false).withTags("gym")
                    .withWorkoutPlan("Squats\nRest").build());
            Region content = (Region) ((ScrollPane) panel.getRoot()).getContent();
            assertTrue(content.lookup("#noteSection").isVisible());
            assertEquals("Knee injury", ((Label) content.lookup("#note")).getText());
            assertTrue(content.lookup("#inactiveStatus").isVisible());
            FlowPane tags = (FlowPane) content.lookup("#tags");
            assertEquals("gym", ((Label) tags.getChildren().get(0)).getText());
            assertEquals("Squats\nRest", getPlanLabel(panel).getText());

            panel.showPerson(new PersonBuilder(ALICE).withNote("Note only").build());
            assertTrue(content.lookup("#noteSection").isVisible());
            assertFalse(content.lookup("#workoutPlanCard").isManaged());

            panel.showPerson(new PersonBuilder(ALICE).withWorkoutPlan("Plan only").build());
            assertFalse(content.lookup("#noteSection").isManaged());
            assertEquals("", ((Label) content.lookup("#note")).getText());
            assertTrue(content.lookup("#workoutPlanCard").isVisible());
        });
    }

    @Test
    public void showPerson_withAppointments_listsThemNumbered() throws Exception {
        runOnFxThread(() -> {
            List<Appointment> series = new ArrayList<>(
                    Appointment.weeklySeries(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0)).subList(0, 2));
            series.set(1, series.get(1).cancel());
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(ALICE.withAppointments(series));

            Region content = (Region) ((ScrollPane) panel.getRoot()).getContent();
            assertTrue(content.lookup("#appointmentsCard").isVisible());
            assertTrue(content.lookup("#appointmentsCard").isManaged());
            Label appointmentsText = (Label) content.lookup("#appointmentsText");
            assertEquals("1. Mon 2026-10-26 09:00\n2. Mon 2026-11-02 09:00 (Cancelled)",
                    appointmentsText.getText());
        });
    }

    @Test
    public void showPerson_noAppointments_hidesCardWithoutStaleText() throws Exception {
        runOnFxThread(() -> {
            PersonDetailPanel panel = new PersonDetailPanel();
            panel.showPerson(ALICE.withAppointments(
                    Appointment.weeklySeries(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0))));
            panel.showPerson(BENSON);

            Region content = (Region) ((ScrollPane) panel.getRoot()).getContent();
            assertFalse(content.lookup("#appointmentsCard").isVisible());
            assertFalse(content.lookup("#appointmentsCard").isManaged());
            assertEquals("", ((Label) content.lookup("#appointmentsText")).getText());
        });
    }

    private Label getPlanLabel(PersonDetailPanel panel) {
        return (Label) ((ScrollPane) panel.getRoot()).getContent().lookup("#workoutPlanText");
    }

    private void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
