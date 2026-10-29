package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullDetails_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null, ALICE.getPhone()));
        assertThrows(NullPointerException.class, () -> new AddCommand(ALICE.getName(), null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = addCommandFor(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_allClientIdsUsed_throwsCommandException() {
        ModelStubWithNoIdsLeft modelStub = new ModelStubWithNoIdsLeft();
        AddCommand addCommand = addCommandFor(new PersonBuilder().build());

        assertThrows(CommandException.class, AddCommand.MESSAGE_NO_IDS_LEFT, () -> addCommand.execute(modelStub));
        assertTrue(modelStub.personsAdded.isEmpty());
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = addCommandFor(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_phoneAlreadyInUse_addSuccessfulWithWarning() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person existingPerson = new PersonBuilder().withName("Alice").withPhone("91234567").build();
        Person personWithReusedPhone = new PersonBuilder().withId(2).withName("Bob").withPhone("9123 4567")
                .build();
        modelStub.addPerson(existingPerson);

        CommandResult commandResult = addCommandFor(personWithReusedPhone).execute(modelStub);

        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithReusedPhone))
                + "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
        assertEquals(List.of(existingPerson, personWithReusedPhone), modelStub.personsAdded);
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = addCommandFor(alice);
        AddCommand addBobCommand = addCommandFor(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = addCommandFor(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different name -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));

        // different phone -> returns false
        assertFalse(addAliceCommand.equals(addCommandFor(new PersonBuilder(alice).withPhone("91234567").build())));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = addCommandFor(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone() + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * Returns an {@code AddCommand} that adds a client with the name and phone of {@code person}.
     */
    private static AddCommand addCommandFor(Person person) {
        return new AddCommand(person.getName(), person.getPhone());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPersonWithPhone(Phone phone) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public PersonId getNextPersonId() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate, Comparator<Person> comparator) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasGroup(Tag group) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addGroup(Tag group) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteGroup(Tag group) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public PersonId getNextPersonId() {
            return person.getId().next();
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }
    }

    /**
     * A Model stub that always accepts the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public PersonId getNextPersonId() {
            return new PersonId(personsAdded.size() + 1);
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public boolean hasPersonWithPhone(Phone phone) {
            requireNonNull(phone);
            return personsAdded.stream().anyMatch(person -> person.getPhone().equals(phone));
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    /**
     * A Model stub whose client IDs have all been used.
     */
    private class ModelStubWithNoIdsLeft extends ModelStubAcceptingPersonAdded {
        @Override
        public PersonId getNextPersonId() {
            return new PersonId(Integer.MAX_VALUE);
        }
    }

}
