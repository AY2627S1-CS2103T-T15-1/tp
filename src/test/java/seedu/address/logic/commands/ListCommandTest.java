package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.AddressBookBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_emptyAddressBook_showsNoClientsMessage() {
        Model emptyModel = new ModelManager();
        Model expectedEmptyModel = new ModelManager();
        assertCommandSuccess(new ListCommand(), emptyModel, ListCommand.MESSAGE_NO_CLIENTS, expectedEmptyModel);
    }

    @Test
    public void execute_oneClient_showsSingularMessage() {
        Model singleModel = new ModelManager(new AddressBookBuilder().withPerson(ALICE).build(), new UserPrefs());
        Model expectedSingleModel = new ModelManager(singleModel.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new ListCommand(), singleModel, "Listed 1 client", expectedSingleModel);
    }

    @Test
    public void execute_listIsNotFiltered_showsPluralMessage() {
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS_PLURAL, getTypicalPersons().size());
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientsAddedOutOfOrder_showsClientsSortedByName() {
        Model unsortedModel = new ModelManager(
                new AddressBookBuilder().withPerson(CARL).withPerson(ALICE).withPerson(BENSON).build(),
                new UserPrefs());
        new ListCommand().execute(unsortedModel);
        assertEquals(List.of(ALICE, BENSON, CARL), unsortedModel.getFilteredPersonList());
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS_PLURAL, getTypicalPersons().size());
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }
}
