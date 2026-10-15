package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, validPerson.getName(), validPerson.getPhone()),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameAndDifferentPhone_success() {
        Person sameNameDifferentPhone = new PersonBuilder(ALICE).withPhone("81234567").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(sameNameDifferentPhone);

        assertCommandSuccess(new AddCommand(sameNameDifferentPhone), model,
                String.format(AddCommand.MESSAGE_SUCCESS, sameNameDifferentPhone.getName(),
                        sameNameDifferentPhone.getPhone()), expectedModel);
    }

    @Test
    public void execute_phoneAlreadyInUse_successWithWarning() {
        Person personWithReusedPhone = new PersonBuilder().withName("Alicia").withPhone(ALICE.getPhone().value).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithReusedPhone);
        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, personWithReusedPhone.getName(),
                personWithReusedPhone.getPhone()) + "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;

        assertCommandSuccess(new AddCommand(personWithReusedPhone), model, expectedMessage, expectedModel);
    }

}
