package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonId;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        List<PersonSelector> firstFilters = List.of(PersonSelector.byName("first"));
        List<PersonSelector> secondFilters = List.of(PersonSelector.byName("second"));

        FindCommand findFirstCommand = new FindCommand(firstFilters);
        FindCommand findSecondCommand = new FindCommand(secondFilters);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(List.of(PersonSelector.byName("first")));
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different filters -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_nameMatchingNoClient_noClientFound() {
        assertFindResult(List.of(PersonSelector.byName("Zack")), FindCommand.MESSAGE_NO_MATCHES);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameMatchingOneClient_oneClientFound() {
        assertFindResult(List.of(PersonSelector.byName("pauline")), FindCommand.MESSAGE_ONE_MATCH);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_partOfName_multipleClientsFound() {
        // "Me" is part of several names, in any case; the list stays sorted by name
        assertFindResult(List.of(PersonSelector.byName("Me")), String.format(FindCommand.MESSAGE_MATCHES, 3));
        assertEquals(List.of(BENSON, DANIEL, ELLE), model.getFilteredPersonList());
    }

    @Test
    public void execute_partOfPhone_matchingClientsFound() {
        // ELLE, FIONA and GEORGE have phone numbers starting with 9482
        assertFindResult(List.of(PersonSelector.byPhone("9482")), String.format(FindCommand.MESSAGE_MATCHES, 3));
        assertEquals(List.of(ELLE, FIONA, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void execute_id_clientWithIdFound() {
        assertFindResult(List.of(PersonSelector.byId(DANIEL.getId())), FindCommand.MESSAGE_ONE_MATCH);
        assertEquals(List.of(DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_unknownId_noClientFound() {
        assertFindResult(List.of(PersonSelector.byId(new PersonId(999))), FindCommand.MESSAGE_NO_MATCHES);
    }

    @Test
    public void execute_nameAndPhone_onlyClientsMatchingBothFound() {
        // "Meier" matches BENSON and DANIEL, but only the phone number of DANIEL contains 2533
        assertFindResult(List.of(PersonSelector.byName("Meier"), PersonSelector.byPhone("2533")),
                FindCommand.MESSAGE_ONE_MATCH);
        assertEquals(List.of(DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        List<PersonSelector> filters = List.of(PersonSelector.byName("keyword"));
        FindCommand findCommand = new FindCommand(filters);
        String expected = FindCommand.class.getCanonicalName() + "{filters=" + filters + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Executes a {@code FindCommand} with {@code filters} and checks that it shows {@code expectedMessage}
     * and lists exactly the clients that match all {@code filters}.
     */
    private void assertFindResult(List<PersonSelector> filters, String expectedMessage) {
        expectedModel.updateFilteredPersonList(person -> filters.stream().allMatch(filter -> filter.matches(person)));
        assertCommandSuccess(new FindCommand(filters), model, expectedMessage, expectedModel);
    }
}
