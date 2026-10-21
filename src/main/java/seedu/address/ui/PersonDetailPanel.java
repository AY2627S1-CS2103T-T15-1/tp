package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.model.LongFormText;
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

    /**
     * Creates an empty {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
    }

    /**
     * Displays the client's contact details and their workout plan, if present.
     */
    public void showPerson(Person person) {
        requireNonNull(person);
        personHeader.getChildren().setAll(new PersonCard(person).getRoot());
        LongFormText plan = person.getWorkoutPlan();
        boolean hasPlan = !plan.isBlank();
        workoutPlanText.setText(hasPlan ? plan.value : "");
        workoutPlanCard.setVisible(hasPlan);
        workoutPlanCard.setManaged(hasPlan);
    }
}
