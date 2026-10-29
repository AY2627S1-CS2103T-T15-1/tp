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

    /**
     * Returns an {@code AddCommand} that adds a client with the name and phone of {@code person}.
     */
    private static AddCommand addCommandFor(Person person) {
        return new AddCommand(person.getName(), person.getPhone());
    }

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().withId(model.getNextPersonId().value).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(addCommandFor(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)), expectedModel);
    }

    @Test
    public void execute_afterLastPersonDeleted_doesNotReuseId() {
        Person lastPerson = model.getAddressBook().getPersonList().stream()
                .max((first, second) -> first.getId().compareTo(second.getId())).get();
        model.deletePerson(lastPerson);
        Person validPerson = new PersonBuilder().withId(lastPerson.getId().value + 1).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(addCommandFor(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)), expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(addCommandFor(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameAndSingaporePhoneVariants_throwsDuplicatePerson() {
        for (String existingPhone : new String[] {"81234567", "+65 8123 4567"}) {
            model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            Person bob = new PersonBuilder().withId(model.getNextPersonId().value)
                    .withName("Bob Tan").withPhone(existingPhone).build();
            model.addPerson(bob);
            String alternatePhone = existingPhone.startsWith("+") ? "81234567" : "+65 8123 4567";
            Person duplicate = new PersonBuilder(bob).withPhone(alternatePhone).build();

            assertCommandFailure(addCommandFor(duplicate), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
        }
    }

    @Test
    public void execute_differentNameAndSingaporePhoneVariants_successWithWarning() {
        for (String existingPhone : new String[] {"81234567", "+65 8123 4567"}) {
            model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            Person bob = new PersonBuilder().withId(model.getNextPersonId().value)
                    .withName("Bob Tan").withPhone(existingPhone).build();
            model.addPerson(bob);
            String alternatePhone = existingPhone.startsWith("+") ? "81234567" : "+65 8123 4567";
            Person otherClient = new PersonBuilder(bob).withId(model.getNextPersonId().value)
                    .withName("Amy Tan").withPhone(alternatePhone).build();
            Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
            expectedModel.addPerson(otherClient);
            String message = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(otherClient))
                    + "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;

            assertCommandSuccess(addCommandFor(otherClient), model, message, expectedModel);
        }
    }

    @Test
    public void execute_sameNameAndDifferentPhone_success() {
        Person sameNameDifferentPhone = new PersonBuilder(ALICE).withId(model.getNextPersonId().value)
                .withPhone("81234567").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(sameNameDifferentPhone);

        assertCommandSuccess(addCommandFor(sameNameDifferentPhone), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(sameNameDifferentPhone)), expectedModel);
    }

    @Test
    public void execute_phoneAlreadyInUse_successWithWarning() {
        Person personWithReusedPhone = new PersonBuilder().withId(model.getNextPersonId().value)
                .withName("Alicia").withPhone(ALICE.getPhone().value).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithReusedPhone);
        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithReusedPhone))
                + "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;

        assertCommandSuccess(addCommandFor(personWithReusedPhone), model, expectedMessage, expectedModel);
    }

}
