package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("123456")); // fewer than 7 digits
        assertFalse(Phone.isValidPhone("1234567890123456")); // more than 15 digits
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("65+91234567")); // plus is not leading
        assertFalse(Phone.isValidPhone("++6591234567")); // more than one plus
        assertFalse(Phone.isValidPhone("9123.4567")); // unsupported separator
        assertFalse(Phone.isValidPhone("9123\t4567")); // control character

        // valid phone numbers
        assertTrue(Phone.isValidPhone("6123 4567"));
        assertTrue(Phone.isValidPhone("+65 9123 4567"));
        assertTrue(Phone.isValidPhone("+1 (202) 555-0123"));
        assertTrue(Phone.isValidPhone("1234567")); // 7 digits
        assertTrue(Phone.isValidPhone("+123456789012345")); // 15 digits
    }

    @Test
    public void equals() {
        Phone phone = new Phone("99999999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("99999999")));

        // formatting separators are ignored
        assertTrue(phone.equals(new Phone("9999 9999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("99999995")));
    }
}
