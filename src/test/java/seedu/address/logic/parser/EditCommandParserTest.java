package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TO_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TO_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.TO_NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.TO_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.TO_PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
    private static final String ID_DESC = " --id 2";
    private static final PersonSelector SELECT_ID = PersonSelector.byId(new PersonId(2));

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no client chosen
        assertParseFailure(parser, TO_NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, ID_DESC, withUsage(EditCommand.MESSAGE_NOT_EDITED));

        // no client chosen and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidClientChoice_failure() {
        // list position instead of ID
        assertParseFailure(parser, "1" + TO_NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid IDs
        assertParseFailure(parser, " --id -5" + TO_NAME_DESC_AMY, withUsage(PersonId.MESSAGE_CONSTRAINTS));
        assertParseFailure(parser, " --id 0" + TO_NAME_DESC_AMY, withUsage(PersonId.MESSAGE_CONSTRAINTS));
        assertParseFailure(parser, " --id abc" + TO_NAME_DESC_AMY, withUsage(PersonId.MESSAGE_CONSTRAINTS));

        // both ID and name given
        assertParseFailure(parser, ID_DESC + " --name Alice" + TO_NAME_DESC_AMY,
                withUsage(String.format(ParserUtil.MESSAGE_CONFLICTING_SELECTORS, "--id or --name")));
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, ID_DESC + INVALID_TO_NAME_DESC, withUsage(Name.MESSAGE_CONSTRAINTS)); // invalid name
        assertParseFailure(parser, ID_DESC + INVALID_TO_PHONE_DESC, // invalid phone
                withUsage(Phone.MESSAGE_CONSTRAINTS));

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, ID_DESC + INVALID_TO_NAME_DESC + TO_PHONE_DESC_AMY,
                withUsage(Name.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = ID_DESC + TO_PHONE_DESC_BOB + TO_NAME_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).build();
        EditCommand expectedCommand = new EditCommand(SELECT_ID, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_clientChosenByName_success() {
        // quotes around values are optional
        String userInput = " --name \"Alice  Pauline\"" + " --to-phone \"9123 4567\"";

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("91234567").build();
        EditCommand expectedCommand = new EditCommand(PersonSelector.byName("Alice Pauline"), descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        String userInput = ID_DESC + TO_NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(SELECT_ID, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = ID_DESC + TO_PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(SELECT_ID, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // valid followed by invalid
        String userInput = ID_DESC + INVALID_TO_PHONE_DESC + TO_PHONE_DESC_BOB;
        assertParseFailure(parser, userInput, withUsage(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO_PHONE)));

        // invalid followed by valid
        userInput = ID_DESC + TO_PHONE_DESC_BOB + INVALID_TO_PHONE_DESC;
        assertParseFailure(parser, userInput, withUsage(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO_PHONE)));

        // multiple valid fields repeated
        userInput = ID_DESC + TO_PHONE_DESC_AMY + TO_PHONE_DESC_AMY + TO_PHONE_DESC_BOB;
        assertParseFailure(parser, userInput, withUsage(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO_PHONE)));

        // ID repeated
        assertParseFailure(parser, ID_DESC + ID_DESC + TO_PHONE_DESC_AMY,
                withUsage(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ID)));
    }

    /**
     * Returns {@code message} followed by the usage message of the edit command.
     */
    private static String withUsage(String message) {
        return ParserUtil.withUsage(message, EditCommand.MESSAGE_USAGE);
    }
}
