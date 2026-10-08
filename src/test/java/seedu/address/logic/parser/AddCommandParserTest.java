package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_LONG_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_LONG_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.LONG_NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.LONG_NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.LONG_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.LONG_PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class AddCommandParserTest {
    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_requiredFieldsPresent_success() {
        Person expectedPerson = new Person(BOB.getName(), BOB.getPhone());

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + LONG_NAME_DESC_BOB + LONG_PHONE_DESC_BOB,
                new AddCommand(expectedPerson));

        // order of fields does not matter
        assertParseSuccess(parser, LONG_PHONE_DESC_BOB + LONG_NAME_DESC_BOB, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_valuesWithExtraWhitespace_success() {
        Person localClient = new Person(new Name("John Doe"), new Phone("61234567"));
        assertParseSuccess(parser, " --name \"John Doe\" --phone \"6123 4567\"", new AddCommand(localClient));

        Person internationalClient = new Person(new Name("John Doe"), new Phone("+6591234567"));
        assertParseSuccess(parser, " --name \"John Doe\" --phone \"+65 9123 4567\"",
                new AddCommand(internationalClient));

        Person normalizedClient = new Person(new Name("Bob Choo"), new Phone("+651234567"));
        assertParseSuccess(parser, " --name \"  Bob   Choo  \" --phone \"+65 (123)-4567\"",
                new AddCommand(normalizedClient));
    }

    @Test
    public void parse_valuesWithoutQuotes_success() {
        Person expectedPerson = new Person(new Name("John Doe"), new Phone("61234567"));
        assertParseSuccess(parser, " --name John Doe --phone 6123 4567", new AddCommand(expectedPerson));

        Person singleCharacterName = new Person(new Name("A"), new Phone("6123456"));
        assertParseSuccess(parser, " --name A --phone 6123456", new AddCommand(singleCharacterName));
    }

    @Test
    public void parse_valueWithOnlyOpeningQuote_failure() {
        assertParseFailure(parser, " --name \"John Doe --phone 61234567", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = LONG_NAME_DESC_BOB + LONG_PHONE_DESC_BOB;

        // multiple names
        assertParseFailure(parser, LONG_NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG));

        // multiple phones
        assertParseFailure(parser, LONG_PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_LONG));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + LONG_PHONE_DESC_AMY + LONG_NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG, PREFIX_PHONE_LONG));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_LONG_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG));

        // invalid phone
        assertParseFailure(parser, INVALID_LONG_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_LONG));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_LONG_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME_LONG));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_LONG_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_LONG));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + LONG_PHONE_DESC_BOB, expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, LONG_NAME_DESC_BOB + VALID_PHONE_BOB, expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_LONG_NAME_DESC + LONG_PHONE_DESC_BOB, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, LONG_NAME_DESC_BOB + INVALID_LONG_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + LONG_NAME_DESC_BOB + LONG_PHONE_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
