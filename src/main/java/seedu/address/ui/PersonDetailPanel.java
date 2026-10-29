package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.model.LongFormText;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.session.Session;

/**
 * Panel that shows the full details of a single person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    static final String COPY_NOTE_TEXT = "Copy note";

    private static final String FXML = "PersonDetailPanel.fxml";
    private static final int MAX_DISPLAYED_SESSIONS = 4;
    private static final String NO_NOTE_TEXT = "No note";
    private static final String NO_SESSIONS_TEXT = "No sessions recorded";
    private static final String COPY_NOTE_HINT = "Right-click to copy the note";

    private PersonId displayedPersonId;

    @FXML
    private ScrollPane sessionScrollPane;

    @FXML
    private VBox sessionList;

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

    /** The note of the client shown, which "Copy note" copies. */
    private LongFormText shownNote = new LongFormText("");

    /**
     * Creates an empty {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
        MenuItem copyNoteItem = new MenuItem(COPY_NOTE_TEXT);
        copyNoteItem.setOnAction(event -> copyNote());
        note.setContextMenu(new ContextMenu(copyNoteItem));
        note.setTooltip(new Tooltip(COPY_NOTE_HINT));
    }

    /**
     * Copies the note of the client shown to the clipboard, as it would be typed in a {@code note} command,
     * so that it can be pasted into a new {@code note} command and changed.
     */
    void copyNote() {
        ClipboardContent content = new ClipboardContent();
        content.putString(shownNote.toTypedText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * Displays the client's contact details, and their workout plan and appointments, if present.
     * Starts a different client's profile at the top; updates to the same client keep their scroll position.
     */
    public void showPerson(Person person) {
        requireNonNull(person);
        personHeader.getChildren().setAll(new PersonCard(person).getRoot());
        showSessions(person);
        shownNote = person.getNote();
        boolean hasNote = !shownNote.isBlank();
        note.setText(shownNote.value);
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
        if (!person.getId().equals(displayedPersonId)) {
            sessionScrollPane.setVvalue(sessionScrollPane.getVmin());
        }
        displayedPersonId = person.getId();
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
