package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_invalidNote_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Note.MESSAGE_CONSTRAINTS, () -> new Note("a".repeat(501)));
    }

    @Test
    public void constructor_validNote_removesSurroundingSpaces() {
        assertEquals("Knee injury", new Note("  Knee injury  ").value);
    }

    @Test
    public void isValidNote() {
        // valid notes
        assertTrue(Note.isValidNote("")); // no note
        assertTrue(Note.isValidNote("Prefers morning sessions; knee injury (left), avoid lunges!"));
        assertTrue(Note.isValidNote("a".repeat(Note.MAX_LENGTH)));
        assertTrue(Note.isValidNote("  " + "a".repeat(Note.MAX_LENGTH) + "  ")); // spaces around are not counted

        // invalid notes
        assertFalse(Note.isValidNote("a".repeat(Note.MAX_LENGTH + 1))); // too long
        assertFalse(Note.isValidNote("first line" + System.lineSeparator() + "second line")); // two lines
    }

    @Test
    public void isEmpty() {
        assertTrue(Note.EMPTY.isEmpty());
        assertTrue(new Note("   ").isEmpty());
        assertFalse(new Note("Knee injury").isEmpty());
    }

    @Test
    public void equals() {
        Note note = new Note("Knee injury");

        // same values -> returns true
        assertTrue(note.equals(new Note("Knee injury")));

        // same object -> returns true
        assertTrue(note.equals(note));

        // null -> returns false
        assertFalse(note.equals(null));

        // different types -> returns false
        assertFalse(note.equals(5.0f));

        // different values -> returns false
        assertFalse(note.equals(new Note("Back pain")));
    }

    @Test
    public void hashCodeAndToString() {
        assertEquals(new Note("Knee injury").hashCode(), new Note("Knee injury").hashCode());
        assertEquals("Knee injury", new Note("Knee injury").toString());
    }
}
