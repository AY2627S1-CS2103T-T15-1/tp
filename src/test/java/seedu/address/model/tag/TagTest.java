package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid tag names
        assertFalse(Tag.isValidTagName("")); // empty string
        assertFalse(Tag.isValidTagName(" ")); // spaces only
        assertFalse(Tag.isValidTagName("rehab!")); // non-alphanumeric character
        assertFalse(Tag.isValidTagName(" weight loss")); // leading space
        assertFalse(Tag.isValidTagName("weight loss ")); // trailing space
        assertFalse(Tag.isValidTagName("weight  loss")); // more than one space between words
        assertFalse(Tag.isValidTagName("weight--loss")); // more than one hyphen between words
        assertFalse(Tag.isValidTagName("-loss")); // leading hyphen
        assertFalse(Tag.isValidTagName("weight-")); // trailing hyphen
        assertFalse(Tag.isValidTagName("weight - loss")); // hyphen with spaces around it

        // valid tag names
        assertTrue(Tag.isValidTagName("morning")); // letters only
        assertTrue(Tag.isValidTagName("batch2")); // letters and digits
        assertTrue(Tag.isValidTagName("weight loss")); // words separated by a space
        assertTrue(Tag.isValidTagName("weight-loss")); // words separated by a hyphen
        assertTrue(Tag.isValidTagName("post-op knee rehab 2")); // spaces and hyphens
    }

}
