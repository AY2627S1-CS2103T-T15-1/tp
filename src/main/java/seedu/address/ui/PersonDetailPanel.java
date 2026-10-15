package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.address.model.person.Person;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";

    @FXML
    private StackPane personHeader;

    /**
     * Creates an empty {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
    }

    /**
     * Displays the client's contact details using the same header as the list cards.
     */
    public void showPerson(Person person, int displayedIndex) {
        personHeader.getChildren().setAll(new PersonCard(person, displayedIndex).getRoot());
    }
}
