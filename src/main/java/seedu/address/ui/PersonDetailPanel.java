package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.model.LongFormText;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";

    @FXML
    private StackPane personHeader;

    @FXML
    private VBox workoutPlanCard;

    @FXML
    private Label workoutPlanText;

    @FXML
    private VBox appointmentsCard;

    @FXML
    private Label appointmentsText;

    @FXML
    private VBox noteSection;

    @FXML
    private Label note;

    /**
     * Creates an empty {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
    }

    /**
     * Displays the client's contact details, and their workout plan and appointments, if present.
     */
    public void showPerson(Person person) {
        requireNonNull(person);
        personHeader.getChildren().setAll(new PersonCard(person).getRoot());
        boolean hasNote = !person.getNote().isBlank();
        note.setText(person.getNote().value);
        noteSection.setVisible(hasNote);
        noteSection.setManaged(hasNote);
        LongFormText plan = person.getWorkoutPlan();
        boolean hasPlan = !plan.isBlank();
        workoutPlanText.setText(hasPlan ? plan.value : "");
        workoutPlanCard.setVisible(hasPlan);
        workoutPlanCard.setManaged(hasPlan);
        List<Appointment> appointments = person.getAppointments();
        boolean hasAppointments = !appointments.isEmpty();
        appointmentsText.setText(IntStream.range(0, appointments.size())
                .mapToObj(i -> (i + 1) + ". " + appointments.get(i).getDisplayText())
                .collect(Collectors.joining("\n")));
        appointmentsCard.setVisible(hasAppointments);
        appointmentsCard.setManaged(hasAppointments);
    }
}
