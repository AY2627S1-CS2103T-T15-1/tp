package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.LongFormText;
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
    public static final String MESSAGE_TEXT_NOT_QUOTED =
            "Put text in double quotes, e.g. \"TEXT\". Use \"\" to clear it.";
    public static final String MESSAGE_CONFLICTING_SELECTORS = "Choose the client using only one of %1$s.";
    public static final String MESSAGE_BLANK_NAME_KEYWORD =
            "Enter part of the client's name after " + PREFIX_NAME_LONG + ".";
    public static final String MESSAGE_INVALID_PHONE_KEYWORD = "Enter digits of the client's phone number after "
            + PREFIX_PHONE_LONG + ". Spaces, parentheses, hyphens and a leading + are allowed.";

    /**
     * Parses long-form command text, requiring outer double quotes and preserving the enclosed content.
     * Escape sequences are left for the command to interpret.
     *
     * @throws ParseException if the text is not surrounded by double quotes.
     */
    public static LongFormText parseLongFormText(String text) throws ParseException {
        requireNonNull(text);
        String trimmed = text.trim();
        if (trimmed.length() < 2 || !trimmed.startsWith("\"") || !trimmed.endsWith("\"")) {
            throw new ParseException(MESSAGE_TEXT_NOT_QUOTED);
        }
        return new LongFormText(trimmed.substring(1, trimmed.length() - 1));
    }

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
     * Parses the argument in {@code argMultimap} that chooses a client into a {@code PersonSelector}.
     * Exactly one of {@code selectorPrefixes} must be given, and nothing may come before it.
     *
     * @param messageUsage The usage message of the command, added to every error message.
     * @param selectorPrefixes The prefixes the command accepts for choosing a client, out of
     *     {@code --id}, {@code --name} and {@code --phone}.
     * @throws ParseException if none or more than one of the prefixes is given, a prefix is given more than once,
     *     or the given value is invalid.
     */
    public static PersonSelector parsePersonSelector(ArgumentMultimap argMultimap, String messageUsage,
            Prefix... selectorPrefixes) throws ParseException {
        requireNonNull(argMultimap);
        requireNonNull(messageUsage);
        assert selectorPrefixes.length > 0 : "A command must accept at least one way of choosing a client";
        List<Prefix> givenPrefixes = Stream.of(selectorPrefixes)
                .filter(prefix -> argMultimap.getValue(prefix).isPresent())
                .toList();

        if (!argMultimap.getPreamble().isEmpty() || givenPrefixes.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
        if (givenPrefixes.size() > 1) {
            throw new ParseException(withUsage(
                    String.format(MESSAGE_CONFLICTING_SELECTORS, describePrefixes(selectorPrefixes)), messageUsage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(selectorPrefixes);
            Prefix prefix = givenPrefixes.get(0);
            return parseSelectorValue(prefix, argMultimap.getValue(prefix).get());
        } catch (ParseException pe) {
            throw new ParseException(withUsage(pe.getMessage(), messageUsage), pe);
        }
    }

    /**
     * Parses all the arguments in {@code argMultimap} given with {@code filterPrefixes} into
     * {@code PersonSelector}s, one for each prefix given. At least one of the prefixes must be given,
     * and nothing may come before them.
     *
     * @param messageUsage The usage message of the command, added to every error message.
     * @throws ParseException if none of the prefixes is given, a prefix is given more than once,
     *     or a given value is invalid.
     */
    public static List<PersonSelector> parsePersonFilters(ArgumentMultimap argMultimap, String messageUsage,
            Prefix... filterPrefixes) throws ParseException {
        requireNonNull(argMultimap);
        requireNonNull(messageUsage);
        List<Prefix> givenPrefixes = Stream.of(filterPrefixes)
                .filter(prefix -> argMultimap.getValue(prefix).isPresent())
                .toList();

        if (!argMultimap.getPreamble().isEmpty() || givenPrefixes.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(filterPrefixes);
            List<PersonSelector> filters = new ArrayList<>();
            for (Prefix prefix : givenPrefixes) {
                filters.add(parseSelectorValue(prefix, argMultimap.getValue(prefix).get()));
            }
            return filters;
        } catch (ParseException pe) {
            throw new ParseException(withUsage(pe.getMessage(), messageUsage), pe);
        }
    }

    /**
     * Parses the {@code value} given with {@code prefix} into a {@code PersonSelector}.
     */
    private static PersonSelector parseSelectorValue(Prefix prefix, String value) throws ParseException {
        String trimmedValue = removeOptionalQuotes(value.trim()).trim();
        if (prefix.equals(PREFIX_ID)) {
            return PersonSelector.byId(parseId(trimmedValue));
        }
        if (prefix.equals(PREFIX_NAME_LONG)) {
            if (trimmedValue.isEmpty()) {
                throw new ParseException(MESSAGE_BLANK_NAME_KEYWORD);
            }
            return PersonSelector.byName(trimmedValue);
        }
        assert prefix.equals(PREFIX_PHONE_LONG) : "Clients can only be chosen by ID, name or phone number";
        if (!PersonSelector.isValidPhoneKeyword(trimmedValue)) {
            throw new ParseException(MESSAGE_INVALID_PHONE_KEYWORD);
        }
        return PersonSelector.byPhone(trimmedValue);
    }

    /**
     * Returns {@code message} followed by {@code messageUsage} on a new line.
     */
    public static String withUsage(String message, String messageUsage) {
        requireNonNull(message);
        requireNonNull(messageUsage);
        return message + "\n" + messageUsage;
    }

    /**
     * Returns {@code prefixes} as a list in words, e.g. "--id, --name or --phone".
     */
    private static String describePrefixes(Prefix... prefixes) {
        List<String> names = Stream.of(prefixes).map(Prefix::toString).toList();
        if (names.size() == 1) {
            return names.get(0);
        }
        return String.join(", ", names.subList(0, names.size() - 1)) + " or " + names.get(names.size() - 1);
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
