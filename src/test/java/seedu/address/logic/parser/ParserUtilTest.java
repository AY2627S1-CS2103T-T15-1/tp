package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "12345678";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";
    private static final String USAGE = "usage message";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = "  " + VALID_NAME + "  ";
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_repeatedWhitespace_returnsNormalizedName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName("Rachel   Walker"));
    }

    @Test
    public void parseName_controlCharacter_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName("Rachel\tWalker"));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = "  " + VALID_PHONE + "  ";
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parsePhone_groupedWithWhitespace_returnsNormalizedPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone("1234  5678"));
    }

    @Test
    public void parsePhone_internationalFormatting_returnsNormalizedPhone() throws Exception {
        Phone expectedPhone = new Phone("+6591234567");
        assertEquals(expectedPhone, ParserUtil.parsePhone("+65 (9123)-4567"));
    }

    @Test
    public void parsePhone_wrongNumberOfDigits_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone("123456"));
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone("1234567890123456"));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseId_validValue_returnsId() throws Exception {
        assertEquals(new PersonId(12), ParserUtil.parseId("12"));
        assertEquals(new PersonId(12), ParserUtil.parseId(WHITESPACE + "12" + WHITESPACE));
        assertEquals(new PersonId(12), ParserUtil.parseId("\"12\""));
    }

    @Test
    public void parseId_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId("0"));
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId("-1"));
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId("+1"));
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId("1 2"));
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId(""));
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId("abc"));

        // too big to be an ID
        assertThrows(ParseException.class, PersonId.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseId(Long.toString((long) Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parsePersonSelector_id_returnsSelectorById() throws Exception {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" --id 3", PREFIX_ID, PREFIX_NAME_LONG);
        assertEquals(PersonSelector.byId(new PersonId(3)), ParserUtil.parsePersonSelector(argMultimap, USAGE));
    }

    @Test
    public void parsePersonSelector_name_returnsSelectorByName() throws Exception {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" --name \"John  Doe\"", PREFIX_ID, PREFIX_NAME_LONG);
        assertEquals(PersonSelector.byName("John Doe"), ParserUtil.parsePersonSelector(argMultimap, USAGE));
    }

    @Test
    public void parsePersonSelector_invalidInput_throwsParseException() {
        String invalidFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, USAGE);

        // neither --id nor --name
        assertSelectorFailure("", invalidFormat);

        // text before the arguments
        assertSelectorFailure("1 --id 3", invalidFormat);

        // both --id and --name
        assertSelectorFailure(" --id 3 --name John", ParserUtil.MESSAGE_CONFLICTING_SELECTORS);

        // repeated --id
        assertSelectorFailure(" --id 3 --id 4", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));

        // blank name
        assertSelectorFailure(" --name \"  \"", ParserUtil.MESSAGE_BLANK_NAME_KEYWORD);
        assertSelectorFailure(" --name", ParserUtil.MESSAGE_BLANK_NAME_KEYWORD);
    }

    @Test
    public void removeOptionalQuotes() {
        assertEquals("John Doe", ParserUtil.removeOptionalQuotes("\"John Doe\""));
        assertEquals("John Doe", ParserUtil.removeOptionalQuotes("John Doe"));
        assertEquals("\"John Doe", ParserUtil.removeOptionalQuotes("\"John Doe"));
        assertEquals("", ParserUtil.removeOptionalQuotes("\"\""));
        assertEquals("\"", ParserUtil.removeOptionalQuotes("\""));
    }

    /**
     * Asserts that parsing {@code args} into a {@code PersonSelector} fails with {@code expectedMessage}.
     */
    private static void assertSelectorFailure(String args, String expectedMessage) {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_NAME_LONG);
        assertThrows(ParseException.class, expectedMessage, () -> ParserUtil.parsePersonSelector(argMultimap, USAGE));
    }
}
