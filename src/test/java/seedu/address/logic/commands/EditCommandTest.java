package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecified_success() {
        Person editedPerson = new PersonBuilder().withId(ALICE.getId().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byId(ALICE.getId()), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecified_success() {
        Person editedPerson = new PersonBuilder(GEORGE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byId(GEORGE.getId()), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(GEORGE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecified_success() {
        EditCommand editCommand = new EditCommand(PersonSelector.byId(ALICE.getId()), new EditPersonDescriptor());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(ALICE));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personChosenByName_success() {
        Person editedPerson = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byName("alice"),
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_showsAllPersonsAfterEdit() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person editedPerson = new PersonBuilder(GEORGE).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byId(GEORGE.getId()),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(GEORGE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameMatchingSeveralPersons_listsMatchesWithoutEditing() {
        PersonSelector selector = PersonSelector.byName("Meier");
        EditCommand editCommand = new EditCommand(selector,
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build());

        // the example is a complete edit command, including the new phone number
        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL),
                "edit --id 2 --to-phone \"" + VALID_PHONE_BOB + "\"");

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePerson_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(ALICE).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byId(BENSON.getId()), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_phoneAlreadyInUse_successWithWarning() {
        Person editedPerson = new PersonBuilder(ALICE).withPhone(BENSON.getPhone().value).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byId(ALICE.getId()),
                new EditPersonDescriptorBuilder().withPhone(BENSON.getPhone().value).build());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(PersonSelector.byId(new PersonId(999)), descriptor);

        assertCommandFailure(editCommand, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void execute_editedPerson_keepsId() throws Exception {
        EditCommand editCommand = new EditCommand(PersonSelector.byId(ALICE.getId()),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());
        editCommand.execute(model);

        Person editedAlice = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equals(VALID_NAME_BOB)).findFirst().get();
        assertEquals(ALICE.getId(), editedAlice.getId());
    }

    @Test
    public void equals() {
        PersonSelector selectAlice = PersonSelector.byId(ALICE.getId());
        final EditCommand standardCommand = new EditCommand(selectAlice, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(PersonSelector.byId(ALICE.getId()), copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different client -> returns false
        assertFalse(standardCommand.equals(new EditCommand(PersonSelector.byId(BENSON.getId()), DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(selectAlice, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(new PersonId(1));
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(selector, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{selector=" + selector + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
