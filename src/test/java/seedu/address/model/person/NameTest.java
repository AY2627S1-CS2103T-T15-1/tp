package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName(".-'")); // allowed punctuation but no letter
        assertFalse(Name.isValidName("Peter2")); // digits are not allowed
        assertFalse(Name.isValidName("peter*")); // unsupported punctuation
        assertFalse(Name.isValidName("Peter\tTan")); // control character
        assertFalse(Name.isValidName("a".repeat(101))); // more than 100 characters

        // valid name
        assertTrue(Name.isValidName("A")); // one character
        assertTrue(Name.isValidName("a".repeat(100))); // 100 characters
        assertTrue(Name.isValidName("José O'Connor-Smith Jr.")); // supported punctuation and Unicode letters
        assertTrue(Name.isValidName("李 小龍")); // international name
        assertTrue(Name.isValidName("  Peter   Tan  ")); // spaces are normalized
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // comparison ignores case
        assertTrue(name.equals(new Name("valid name")));
        assertEquals(name.hashCode(), new Name("valid name").hashCode());

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
