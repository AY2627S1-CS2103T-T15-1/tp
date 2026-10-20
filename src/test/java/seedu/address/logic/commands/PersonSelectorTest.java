package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;

public class PersonSelectorTest {

    @Test
    public void byName_blankName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PersonSelector.byName("   "));
    }

    @Test
    public void matches_byId() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        assertTrue(selector.matches(ALICE));
        assertFalse(selector.matches(BENSON));

        // same ID, different details -> still matches
        assertTrue(selector.matches(new PersonBuilder(BENSON).withId(ALICE.getId().value).build()));
    }

    @Test
    public void matches_byName() {
        // full name
        assertTrue(PersonSelector.byName("Alice Pauline").matches(ALICE));

        // part of the name, including the middle of a word
        assertTrue(PersonSelector.byName("lice").matches(ALICE));

        // different case and extra spaces
        assertTrue(PersonSelector.byName("  aLiCe    PAULINE ").matches(ALICE));

        // not part of the name
        assertFalse(PersonSelector.byName("Bob").matches(ALICE));
        assertFalse(PersonSelector.byName("Pauline Alice").matches(ALICE));
    }

    @Test
    public void findMatches_severalMatches_returnsMatchesInIdOrder() {
        List<Person> persons = List.of(DANIEL, ALICE, BENSON);
        assertEquals(List.of(BENSON, DANIEL), PersonSelector.byName("meier").findMatches(persons));
    }

    @Test
    public void findMatches_noMatch_returnsEmptyList() {
        assertEquals(List.of(), PersonSelector.byId(new PersonId(999)).findMatches(getTypicalPersons()));
    }

    @Test
    public void getNoMatchMessage() {
        assertEquals(String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 7),
                PersonSelector.byId(new PersonId(7)).getNoMatchMessage());
        assertEquals(String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_NAME, "Zack"),
                PersonSelector.byName(" Zack ").getNoMatchMessage());
    }

    @Test
    public void getMultipleMatchesMessage_severalMatches_suggestsIdOfFirstMatch() {
        String message = PersonSelector.byName("Meier").getMultipleMatchesMessage(List.of(BENSON, DANIEL), "view");
        assertEquals(String.format(PersonSelector.MESSAGE_MULTIPLE_MATCHES, 2, "Meier", "view", BENSON.getId()),
                message);
    }

    @Test
    public void equals() {
        PersonSelector selector = PersonSelector.byId(new PersonId(1));

        // same values -> returns true
        assertTrue(selector.equals(PersonSelector.byId(new PersonId(1))));
        assertTrue(PersonSelector.byName("Alice").equals(PersonSelector.byName(" Alice ")));

        // same object -> returns true
        assertTrue(selector.equals(selector));

        // null -> returns false
        assertFalse(selector.equals(null));

        // different types -> returns false
        assertFalse(selector.equals(1));

        // different ID -> returns false
        assertFalse(selector.equals(PersonSelector.byId(new PersonId(2))));

        // by name instead of by ID -> returns false
        assertFalse(selector.equals(PersonSelector.byName("1")));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        assertEquals(PersonSelector.byName("Alice").hashCode(), PersonSelector.byName("Alice").hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(new PersonId(3));
        assertEquals(PersonSelector.class.getCanonicalName() + "{id=3, nameKeyword=null}", selector.toString());
    }
}
