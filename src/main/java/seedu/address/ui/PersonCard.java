package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final String INACTIVE_STYLE_CLASS = "inactive-contact";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label inactiveStatus;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} that displays the given {@code Person}, including their ID,
     * their tags and whether they are inactive.
     */
    public PersonCard(Person person) {
        super(FXML);
        this.person = person;
        id.setText("#" + person.getId());
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);

        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        setShown(tags, !person.getTags().isEmpty());

        setShown(inactiveStatus, !person.isActive());
        if (!person.isActive()) {
            cardPane.getStyleClass().add(INACTIVE_STYLE_CLASS);
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
