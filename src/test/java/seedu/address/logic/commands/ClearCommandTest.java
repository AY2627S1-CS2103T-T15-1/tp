package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonId;

public class ClearCommandTest {

    @Test
    public void execute_confirmedEmptyAddressBook_resetsNextId() {
        AddressBook addressBook = new AddressBook();
        addressBook.setNextPersonId(new PersonId(10));
        Model model = new ModelManager(addressBook, new UserPrefs());
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(new PersonId(1), model.getNextPersonId());
    }

    @Test
    public void execute_confirmedNonEmptyAddressBook_clearsClientsAndResetsNextId() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(new PersonId(1), model.getNextPersonId());
    }

    @Test
    public void execute_unconfirmedEmptyAddressBook_preservesNextId() {
        AddressBook addressBook = new AddressBook();
        addressBook.setNextPersonId(new PersonId(10));
        Model model = new ModelManager(addressBook, new UserPrefs());
        Model expectedModel = new ModelManager(addressBook, new UserPrefs());

        assertCommandSuccess(new ClearCommand(false), model, ClearCommand.MESSAGE_CONFIRMATION, expectedModel);
        assertEquals(new PersonId(10), model.getNextPersonId());
    }

    @Test
    public void execute_unconfirmedNonEmptyAddressBook_preservesClientsAndNextId() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        assertCommandSuccess(new ClearCommand(false), model, ClearCommand.MESSAGE_CONFIRMATION, expectedModel);
        assertEquals(expectedModel.getNextPersonId(), model.getNextPersonId());
    }

}
