package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";

    @FXML
    private StackPane personHeader;
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
     * Displays the client's contact details using the same header as the list cards,
     * followed by the client's note if they have one.
     */
    public void showPerson(Person person) {
        personHeader.getChildren().setAll(new PersonCard(person).getRoot());
        boolean hasNote = !person.getNote().isEmpty();
        note.setText(person.getNote().value);
        noteSection.setVisible(hasNote);
        noteSection.setManaged(hasNote);
    }
}
