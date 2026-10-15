package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    public static final String NO_PERSON_MESSAGE = "No client selected.\nUse view INDEX to see a client's details.";

    private static final String FXML = "PersonDetailPanel.fxml";

    @FXML
    private Label placeholderMessage;

    /**
     * Creates an empty {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
        placeholderMessage.setText(NO_PERSON_MESSAGE);
    }
}
