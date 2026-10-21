package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code TagCommand}.
 */
public class TagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_clientWithoutType_addsType() {
        Person gymAlice = new PersonBuilder(ALICE).withTags("gym").build();
        TagCommand command = new TagCommand(PersonSelector.byId(ALICE.getId()), Tag.GYM);

        String expectedMessage = String.format(TagCommand.MESSAGE_TYPE_SET, "gym", Messages.format(gymAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, gymAlice);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientWithOtherType_replacesTypeAndKeepsOtherTags() {
        Person gymAlice = new PersonBuilder(ALICE).withTags("gym", "beginners").build();
        model.setPerson(ALICE, gymAlice);
        Person personalAlice = new PersonBuilder(ALICE).withTags("personal", "beginners").build();
        TagCommand command = new TagCommand(PersonSelector.byName("alice"), Tag.PERSONAL);

        String expectedMessage = String.format(TagCommand.MESSAGE_TYPE_SET, "personal",
                Messages.format(personalAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(gymAlice, personalAlice);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientAlreadyOfType_noChange() {
        Person gymAlice = new PersonBuilder(ALICE).withTags("gym").build();
        model.setPerson(ALICE, gymAlice);
        TagCommand command = new TagCommand(PersonSelector.byId(ALICE.getId()), Tag.GYM);

        String expectedMessage = String.format(TagCommand.MESSAGE_TYPE_UNCHANGED, "gym", Messages.format(gymAlice));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameMatchingSeveralClients_listsMatchesWithoutChanges() {
        PersonSelector selector = PersonSelector.byName("Meier");
        TagCommand command = new TagCommand(selector, Tag.GYM);

        // the example is a complete tag command, including the type
        String expectedMessage = selector.getMultipleMatchesMessage(List.of(BENSON, DANIEL), "tag --id 2 --type gym");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        TagCommand command = new TagCommand(PersonSelector.byId(new PersonId(999)), Tag.GYM);
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void equals() {
        TagCommand command = new TagCommand(PersonSelector.byId(ALICE.getId()), Tag.GYM);

        // same values -> returns true
        assertTrue(command.equals(new TagCommand(PersonSelector.byId(ALICE.getId()), Tag.GYM)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // different client -> returns false
        assertFalse(command.equals(new TagCommand(PersonSelector.byId(BENSON.getId()), Tag.GYM)));

        // different client type -> returns false
        assertFalse(command.equals(new TagCommand(PersonSelector.byId(ALICE.getId()), Tag.PERSONAL)));
    }

    @Test
    public void toStringMethod() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        TagCommand command = new TagCommand(selector, Tag.PERSONAL);
        String expected = TagCommand.class.getCanonicalName() + "{selector=" + selector + ", clientType="
                + Tag.PERSONAL + "}";
        assertEquals(expected, command.toString());
    }
}
