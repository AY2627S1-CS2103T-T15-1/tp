package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.session.Session;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";
    private static final int MAX_DISPLAYED_SESSIONS = 4;
    private static final String NO_NOTE_TEXT = "No note";
    private static final String NO_SESSIONS_TEXT = "No sessions recorded";

    @FXML
    private StackPane personHeader;
    @FXML
    private VBox sessionList;

    /**
     * Creates an empty {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
    }

    /**
     * Displays the client's contact details using the same header as the list cards.
     */
    public void showPerson(Person person) {
        personHeader.getChildren().setAll(new PersonCard(person).getRoot());
        showSessions(person);
    }

    private void showSessions(Person person) {
        sessionList.getChildren().clear();

        person.getSessions().stream()
                .sorted(Comparator.comparing(Session::getDate).reversed())
                .limit(MAX_DISPLAYED_SESSIONS)
                .map(this::createSessionRow)
                .forEach(sessionList.getChildren()::add);

        if (sessionList.getChildren().isEmpty()) {
            Label emptyState = new Label(NO_SESSIONS_TEXT);
            emptyState.getStyleClass().add("session-empty");
            sessionList.getChildren().add(emptyState);
        }
    }

    private VBox createSessionRow(Session session) {
        Label date = new Label(session.getDate().toString());
        date.getStyleClass().add("session-date");

        String noteText = session.getNote().isBlank() ? NO_NOTE_TEXT : session.getNote();
        Label note = new Label(noteText);
        note.setWrapText(true);
        note.setMaxWidth(Double.MAX_VALUE);
        note.getStyleClass().add("session-note");

        VBox row = new VBox(4, date, note);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().add("session-row");
        return row;
    }
}
