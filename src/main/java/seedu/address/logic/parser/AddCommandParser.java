package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_LONG;
import static seedu.address.logic.parser.ParserUtil.removeOptionalQuotes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter for add: %1$s.";
    public static final String MESSAGE_UNEXPECTED_TEXT = "Unexpected text after %1$s: %2$s.";
    private static final Pattern LONG_FLAG = Pattern.compile("(?<!\\S)--[^\\s\"]+");
    private static final Pattern PHONE_WITH_EXTRA_TEXT = Pattern.compile("^([+0-9 ()-]+)\\s+([^+0-9 ()-].*)$");

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        verifyKnownParameters(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME_LONG, PREFIX_PHONE_LONG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME_LONG, PREFIX_PHONE_LONG)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME_LONG, PREFIX_PHONE_LONG);
        String nameValue = argMultimap.getValue(PREFIX_NAME_LONG).get();
        String phoneValue = argMultimap.getValue(PREFIX_PHONE_LONG).get();
        verifyNoTextAfterQuotes(nameValue, PREFIX_NAME_LONG);
        verifyNoTextAfterQuotes(phoneValue, PREFIX_PHONE_LONG);
        Name name = ParserUtil.parseName(removeOptionalQuotes(nameValue));
        Matcher extraPhoneText = PHONE_WITH_EXTRA_TEXT.matcher(phoneValue);
        if (!Phone.isValidPhone(phoneValue) && extraPhoneText.matches()
                && Phone.isValidPhone(extraPhoneText.group(1))) {
            throw unexpectedText(PREFIX_PHONE_LONG, extraPhoneText.group(2));
        }
        Phone phone = ParserUtil.parsePhone(removeOptionalQuotes(phoneValue));

        return new AddCommand(name, phone);
    }

    /** Rejects unsupported flags while letting the tokenizer ignore flags inside quoted values. */
    private static void verifyKnownParameters(String args) throws ParseException {
        List<Prefix> candidates = LONG_FLAG.matcher(args).results()
                .map(match -> new Prefix(match.group())).distinct().toList();
        Prefix[] prefixes = Stream.concat(Stream.of(PREFIX_NAME_LONG, PREFIX_PHONE_LONG), candidates.stream())
                .distinct().toArray(Prefix[]::new);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, prefixes);
        for (Prefix prefix : candidates) {
            if (!prefix.equals(PREFIX_NAME_LONG) && !prefix.equals(PREFIX_PHONE_LONG)
                    && arguments.getValue(prefix).isPresent()) {
                throw new ParseException(ParserUtil.withUsage(
                        String.format(MESSAGE_UNKNOWN_PARAMETER, prefix), AddCommand.MESSAGE_USAGE));
            }
        }
    }

    /** A closing quote ends a value; any following text is an extra argument. */
    private static void verifyNoTextAfterQuotes(String value, Prefix prefix) throws ParseException {
        if (value.startsWith("\"")) {
            int closingQuote = value.indexOf('"', 1);
            if (closingQuote >= 0 && !value.substring(closingQuote + 1).isBlank()) {
                throw unexpectedText(prefix, value.substring(closingQuote + 1).trim());
            }
        }
    }

    private static ParseException unexpectedText(Prefix prefix, String text) {
        return new ParseException(ParserUtil.withUsage(
                String.format(MESSAGE_UNEXPECTED_TEXT, prefix, text), AddCommand.MESSAGE_USAGE));
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
