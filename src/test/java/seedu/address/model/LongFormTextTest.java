package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LongFormTextTest {

    @Test
    public void constructor_nullText_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LongFormText(null));
    }

    @Test
    public void constructor_text_preservesWhitespaceAndLiteralEscapes() {
        String input = "  Monday\nSquats: 3 sets\\nRest  ";
        LongFormText text = new LongFormText(input);
        assertEquals(input, text.value);
        assertEquals(input, text.toString());
    }

    @Test
    public void isBlank_whitespaceOnly_returnsTrue() {
        assertTrue(new LongFormText("").isBlank());
        assertTrue(new LongFormText(" \t\n").isBlank());
        assertFalse(new LongFormText("Squats").isBlank());
    }

    @Test
    public void equals_comparesExactText() {
        LongFormText text = new LongFormText("Squats\nRest");
        LongFormText copy = new LongFormText("Squats\nRest");
        assertEquals(text, text);
        assertEquals(text, copy);
        assertEquals(text.hashCode(), copy.hashCode());
        assertFalse(text.equals(new LongFormText("Squats\\nRest")));
        assertFalse(text.equals(new LongFormText("squats\nRest")));
        assertFalse(text.equals("Squats\nRest"));
        assertFalse(text.equals(null));
    }
}
