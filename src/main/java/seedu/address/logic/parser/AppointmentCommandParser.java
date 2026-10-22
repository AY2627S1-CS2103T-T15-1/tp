package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.time.LocalDate;
import java.time.LocalTime;

import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of the {@code appointment} command, choosing the form of the command from its flag.
 */
public class AppointmentCommandParser implements Parser<Command> {

    /** Usage of every form of the {@code appointment} command, shown when the input matches none of them. */
    public static final String MESSAGE_USAGE = AddAppointmentCommand.MESSAGE_USAGE;

    private static final Prefix[] ALL_PREFIXES = {PREFIX_ADD, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_DATE, PREFIX_TIME};

    /**
     * Parses {@code args} into the form of the {@code appointment} command given by its flag.
     *
     * @throws ParseException if no form matches, or the arguments of the chosen form are missing or invalid.
     */
    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);
        if (argMultimap.getValue(PREFIX_ADD).isPresent()) {
            return parseAdd(argMultimap);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    /**
     * Parses the arguments of {@code appointment --add}.
     */
    private static AddAppointmentCommand parseAdd(ArgumentMultimap argMultimap) throws ParseException {
        String usage = AddAppointmentCommand.MESSAGE_USAGE;
        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, usage, PREFIX_ID, PREFIX_NAME_LONG);
        boolean hasValueAfterFlag = argMultimap.getAllValues(PREFIX_ADD).stream().anyMatch(value -> !value.isEmpty());
        if (hasValueAfterFlag || argMultimap.getValue(PREFIX_DATE).isEmpty()
                || argMultimap.getValue(PREFIX_TIME).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ADD, PREFIX_DATE, PREFIX_TIME);
            LocalDate firstDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get());
            LocalTime time = ParserUtil.parseTime(argMultimap.getValue(PREFIX_TIME).get());
            return new AddAppointmentCommand(selector, firstDate, time);
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), usage), pe);
        }
    }
}
