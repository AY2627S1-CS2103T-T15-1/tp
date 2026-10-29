package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PersonIdTest {

    @Test
    public void constructor_invalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, PersonId.MESSAGE_CONSTRAINTS, () -> new PersonId(0));
        assertThrows(IllegalArgumentException.class, PersonId.MESSAGE_CONSTRAINTS, () -> new PersonId(-1));
    }

    @Test
    public void isValidId() {
        // invalid IDs
        assertFalse(PersonId.isValidId(0));
        assertFalse(PersonId.isValidId(-5));

        // valid IDs
        assertTrue(PersonId.isValidId(1));
        assertTrue(PersonId.isValidId(Integer.MAX_VALUE));
    }

    @Test
    public void isValidClientId() {
        // invalid client IDs
        assertFalse(PersonId.isValidClientId(0));
        assertFalse(PersonId.isValidClientId(Integer.MAX_VALUE));

        // valid client IDs
        assertTrue(PersonId.isValidClientId(1));
        assertTrue(PersonId.isValidClientId(PersonId.MAX_CLIENT_ID));
    }

    @Test
    public void isClientId() {
        assertTrue(new PersonId(PersonId.MAX_CLIENT_ID).isClientId());
        assertFalse(new PersonId(Integer.MAX_VALUE).isClientId());
    }

    @Test
    public void next_validId_returnsFollowingId() {
        assertEquals(new PersonId(8), new PersonId(7).next());
    }

    @Test
    public void next_largestClientId_returnsIdThatIsNotAClientId() {
        PersonId afterLargest = new PersonId(PersonId.MAX_CLIENT_ID).next();
        assertEquals(new PersonId(Integer.MAX_VALUE), afterLargest);
        assertFalse(afterLargest.isClientId());
    }

    @Test
    public void next_idThatIsNotAClientId_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new PersonId(Integer.MAX_VALUE).next());
    }

    @Test
    public void compareTo() {
        assertTrue(new PersonId(1).compareTo(new PersonId(2)) < 0);
        assertEquals(0, new PersonId(2).compareTo(new PersonId(2)));
        assertTrue(new PersonId(3).compareTo(new PersonId(2)) > 0);
    }

    @Test
    public void equals() {
        PersonId id = new PersonId(3);

        // same values -> returns true
        assertTrue(id.equals(new PersonId(3)));

        // same object -> returns true
        assertTrue(id.equals(id));

        // null -> returns false
        assertFalse(id.equals(null));

        // different types -> returns false
        assertFalse(id.equals(3));

        // different values -> returns false
        assertFalse(id.equals(new PersonId(4)));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new PersonId(3).hashCode(), new PersonId(3).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("3", new PersonId(3).toString());
    }
}
