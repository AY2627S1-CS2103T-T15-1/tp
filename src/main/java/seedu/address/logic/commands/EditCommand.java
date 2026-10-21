package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Edits the details of an existing client, chosen by ID or by name.
 */
public class EditCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of a client, "
            + "chosen by ID or by part of their name. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then "
            + "[" + PREFIX_TO_NAME + " NEW_NAME] "
            + "[" + PREFIX_TO_PHONE + " NEW_PHONE]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ID + " 1 "
            + PREFIX_TO_PHONE + " 91234567";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited client: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This client already exists in the address book.";

    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param selector chooses the client to edit
     * @param editPersonDescriptor details to edit the client with
     */
    public EditCommand(PersonSelector selector, EditPersonDescriptor editPersonDescriptor) {
        super(selector);
        requireNonNull(editPersonDescriptor);

        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    protected CommandResult executeOn(Person personToEdit, Model model) throws CommandException {
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        boolean isPhoneChanged = !personToEdit.getPhone().equals(editedPerson.getPhone());
        boolean isPhoneInUse = isPhoneChanged && model.hasPersonWithPhone(editedPerson.getPhone());
        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String successMessage = String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        if (isPhoneInUse) {
            successMessage += "\n" + Messages.MESSAGE_PHONE_IN_USE_WARNING;
        }
        return new CommandResult(successMessage);
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    protected String getArgumentsAfterSelector() {
        StringBuilder arguments = new StringBuilder();
        editPersonDescriptor.getName().ifPresent(name ->
                arguments.append(" ").append(PREFIX_TO_NAME).append(" ").append(quote(name.fullName)));
        editPersonDescriptor.getPhone().ifPresent(phone ->
                arguments.append(" ").append(PREFIX_TO_PHONE).append(" ").append(quote(phone.value)));
        return arguments.toString();
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());

        return new Person(personToEdit.getId(), updatedName, updatedPhone, personToEdit.getNote(),
                personToEdit.isActive(), personToEdit.getTags());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return selector.equals(otherEditCommand.selector)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .toString();
        }
    }
}
