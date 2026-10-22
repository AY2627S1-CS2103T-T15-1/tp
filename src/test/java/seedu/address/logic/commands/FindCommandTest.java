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
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private static final Tag MORNING = new Tag("morning");

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

        // same filters and group -> returns true
        FindCommand findMorningCommand = new FindCommand(firstFilters, MORNING);
        assertTrue(findMorningCommand.equals(new FindCommand(firstFilters, new Tag("morning"))));

        // group and no group -> returns false
        assertFalse(findFirstCommand.equals(findMorningCommand));

        // different groups -> returns false
        assertFalse(findMorningCommand.equals(new FindCommand(firstFilters, Tag.GYM)));
    }

    @Test
    public void execute_groupOnly_clientsInGroupFound() {
        Person morningAlice = tag(ALICE, "morning");
        Person morningBenson = tag(BENSON, "morning", "rehab");
        tag(DANIEL, "rehab");

        assertGroupFindResult(List.of(), MORNING, String.format(FindCommand.MESSAGE_MATCHES, 2));
        assertEquals(List.of(morningAlice, morningBenson), model.getFilteredPersonList());
    }

    @Test
    public void execute_clientTypeAsGroup_clientsWithLabelFound() {
        Person gymElle = tag(ELLE, "gym");
        tag(FIONA, "personal");

        assertGroupFindResult(List.of(), Tag.GYM, FindCommand.MESSAGE_ONE_MATCH);
        assertEquals(List.of(gymElle), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagWithDifferentCase_clientFound() {
        Person upperCaseAlice = tag(ALICE, "Morning");

        assertGroupFindResult(List.of(), MORNING, FindCommand.MESSAGE_ONE_MATCH);
        assertEquals(List.of(upperCaseAlice), model.getFilteredPersonList());
    }

    @Test
    public void execute_groupWithNoClients_noClientFound() {
        assertGroupFindResult(List.of(), MORNING, FindCommand.MESSAGE_NO_MATCHES);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_groupAndName_onlyClientsMatchingBothFound() {
        // BENSON and DANIEL are both named Meier, but only DANIEL is in the group
        tag(BENSON, "rehab");
        Person morningDaniel = tag(DANIEL, "morning");

        assertGroupFindResult(List.of(PersonSelector.byName("Meier")), MORNING, FindCommand.MESSAGE_ONE_MATCH);
        assertEquals(List.of(morningDaniel), model.getFilteredPersonList());
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
        String expected = FindCommand.class.getCanonicalName() + "{filters=" + filters + ", group=null}";
        assertEquals(expected, findCommand.toString());

        FindCommand findGroupCommand = new FindCommand(filters, MORNING);
        String expectedWithGroup = FindCommand.class.getCanonicalName() + "{filters=" + filters + ", group="
                + MORNING + "}";
        assertEquals(expectedWithGroup, findGroupCommand.toString());
    }

    /**
     * Gives {@code person} the tags {@code tagNames} in both models, and returns the tagged person.
     */
    private Person tag(Person person, String... tagNames) {
        Person taggedPerson = new PersonBuilder(person).withTags(tagNames).build();
        model.setPerson(person, taggedPerson);
        expectedModel.setPerson(person, taggedPerson);
        return taggedPerson;
    }

    /**
     * Executes a {@code FindCommand} with {@code filters} and {@code group}, and checks that it shows
     * {@code expectedMessage} and lists exactly the clients in {@code group} that match all {@code filters}.
     */
    private void assertGroupFindResult(List<PersonSelector> filters, Tag group, String expectedMessage) {
        expectedModel.updateFilteredPersonList(person -> filters.stream().allMatch(filter -> filter.matches(person))
                && person.getTags().stream().anyMatch(tag -> tag.tagName.equalsIgnoreCase(group.tagName)));
        assertCommandSuccess(new FindCommand(filters, group), model, expectedMessage, expectedModel);
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
