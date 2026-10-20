package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_CONFLICTING_SELECTORS =
            "Choose the client using either " + PREFIX_ID + " or " + PREFIX_NAME_LONG + ", not both.";
    public static final String MESSAGE_BLANK_NAME_KEYWORD =
            "Enter part of the client's name after " + PREFIX_NAME_LONG + ".";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code id} into a {@code PersonId} and returns it.
     * Leading and trailing whitespaces and optional surrounding quotes will be removed.
     *
     * @throws ParseException if the given {@code id} is not a positive whole number.
     */
    public static PersonId parseId(String id) throws ParseException {
        requireNonNull(id);
        String trimmedId = removeOptionalQuotes(id.trim()).trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedId)) {
            throw new ParseException(PersonId.MESSAGE_CONSTRAINTS);
        }
        return new PersonId(Integer.parseInt(trimmedId));
    }

    /**
     * Parses the {@code --id} or {@code --name} argument in {@code argMultimap} into a {@code PersonSelector}.
     * Exactly one of the two must be given, and nothing may come before them.
     *
     * @param messageUsage The usage message of the command, shown if neither argument is given.
     * @throws ParseException if neither or both arguments are given, either is given more than once,
     *     or the given value is invalid.
     */
    public static PersonSelector parsePersonSelector(ArgumentMultimap argMultimap, String messageUsage)
            throws ParseException {
        requireNonNull(argMultimap);
        requireNonNull(messageUsage);
        boolean hasId = argMultimap.getValue(PREFIX_ID).isPresent();
        boolean hasName = argMultimap.getValue(PREFIX_NAME_LONG).isPresent();

        if (!argMultimap.getPreamble().isEmpty() || (!hasId && !hasName)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
        if (hasId && hasName) {
            throw new ParseException(MESSAGE_CONFLICTING_SELECTORS);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID, PREFIX_NAME_LONG);

        if (hasId) {
            return PersonSelector.byId(parseId(argMultimap.getValue(PREFIX_ID).get()));
        }
        String nameKeyword = removeOptionalQuotes(argMultimap.getValue(PREFIX_NAME_LONG).get().trim());
        if (nameKeyword.isBlank()) {
            throw new ParseException(MESSAGE_BLANK_NAME_KEYWORD);
        }
        return PersonSelector.byName(nameKeyword);
    }

    /**
     * Returns {@code value} without the double quotes around it, if it starts and ends with one.
     */
    public static String removeOptionalQuotes(String value) {
        requireNonNull(value);
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing spaces will be trimmed, and repeated spaces will be collapsed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        if (!Name.isValidName(name)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(name);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Readable separators will be removed by {@link Phone}.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        if (!Phone.isValidPhone(phone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
