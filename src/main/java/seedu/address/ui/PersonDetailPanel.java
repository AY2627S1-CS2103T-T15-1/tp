package seedu.address.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    public static final String NO_PERSON_MESSAGE = "No client selected.\nUse view INDEX to see a client's details.";

    private static final String FXML = "PersonDetailPanel.fxml";

    @FXML
    private Label placeholderMessage;
    @FXML
    private VBox details;
    @FXML
    private Label name;
    @FXML
    private Label phone;

    /**
     * Creates a {@code PersonDetailPanel} that shows the details of {@code viewedPerson}
     * and updates whenever {@code viewedPerson} changes.
     */
    public PersonDetailPanel(ObservableValue<Person> viewedPerson) {
        super(FXML);
        placeholderMessage.setText(NO_PERSON_MESSAGE);
        showPerson(viewedPerson.getValue());
        viewedPerson.addListener((observable, oldPerson, newPerson) -> showPerson(newPerson));
    }

    /**
     * Shows the details of {@code person}, or the placeholder message if {@code person} is null.
     */
    private void showPerson(Person person) {
        boolean hasPerson = person != null;
        setShown(details, hasPerson);
        setShown(placeholderMessage, !hasPerson);

        if (hasPerson) {
            name.setText(person.getName().fullName);
            phone.setText(person.getPhone().value);
        }
    }

    /**
     * Shows or hides {@code region}, and removes it from the layout while it is hidden.
     */
    private static void setShown(Region region, boolean isShown) {
        region.setVisible(isShown);
        region.setManaged(isShown);
    }
}
