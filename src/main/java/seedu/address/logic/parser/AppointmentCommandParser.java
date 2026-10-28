package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CANCEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RESCHEDULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.CancelAppointmentCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListAppointmentsCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.RescheduleAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of the {@code appointment} command, choosing the form of the command from its flag.
 */
public class AppointmentCommandParser implements Parser<Command> {

    /** Usage of every form of the {@code appointment} command, shown when the input matches none of them. */
    public static final String MESSAGE_USAGE = AddAppointmentCommand.MESSAGE_USAGE
            + "\n\n" + CancelAppointmentCommand.MESSAGE_USAGE
            + "\n\n" + RescheduleAppointmentCommand.MESSAGE_USAGE
            + "\n\n" + ListAppointmentsCommand.MESSAGE_USAGE;

    private static final Prefix[] ALL_PREFIXES = {
        PREFIX_ADD, PREFIX_CANCEL, PREFIX_RESCHEDULE,
        PREFIX_ID, PREFIX_NAME_LONG, PREFIX_INDEX, PREFIX_DATE, PREFIX_TIME, PREFIX_WEEK
    };

    private final Clock clock;

    /**
     * Creates a parser using the computer's local time zone.
     */
    public AppointmentCommandParser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser using the supplied clock to resolve today at command execution time.
     */
    AppointmentCommandParser(Clock clock) {
        this.clock = requireNonNull(clock);
    }

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
        if (argMultimap.getValue(PREFIX_CANCEL).isPresent()) {
            return parseCancel(argMultimap);
        }
        if (argMultimap.getValue(PREFIX_RESCHEDULE).isPresent()) {
            return parseReschedule(argMultimap);
        }
        if (argMultimap.getValue(PREFIX_DATE).isPresent() || argMultimap.getValue(PREFIX_WEEK).isPresent()) {
            return parseList(argMultimap);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    /**
     * Parses the arguments of {@code appointment --date} and {@code appointment --week}.
     * Exactly one of the two must be given, with nothing else.
     */
    private static ListAppointmentsCommand parseList(ArgumentMultimap argMultimap) throws ParseException {
        String usage = ListAppointmentsCommand.MESSAGE_USAGE;
        boolean hasDate = argMultimap.getValue(PREFIX_DATE).isPresent();
        boolean hasWeek = argMultimap.getValue(PREFIX_WEEK).isPresent();
        boolean hasOtherArgument = !argMultimap.getPreamble().isEmpty()
                || hasAny(argMultimap, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_TIME, PREFIX_INDEX);
        if (hasDate == hasWeek || hasOtherArgument) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE, PREFIX_WEEK);
            Prefix given = hasDate ? PREFIX_DATE : PREFIX_WEEK;
            LocalDate date = ParserUtil.parseDate(argMultimap.getValue(given).get());
            return hasDate ? ListAppointmentsCommand.forDay(date) : ListAppointmentsCommand.forWeek(date);
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), usage), pe);
        }
    }

    private static boolean hasAny(ArgumentMultimap argMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
    }

    private static boolean hasValueAfter(ArgumentMultimap argMultimap, Prefix flag) {
        return argMultimap.getAllValues(flag).stream().anyMatch(value -> !value.isEmpty());
    }

    /**
     * Parses the arguments of {@code appointment --add}.
     */
    private AddAppointmentCommand parseAdd(ArgumentMultimap argMultimap) throws ParseException {
        String usage = AddAppointmentCommand.MESSAGE_USAGE;
        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, usage, PREFIX_ID, PREFIX_NAME_LONG);
        if (hasValueAfter(argMultimap, PREFIX_ADD) || argMultimap.getValue(PREFIX_DATE).isEmpty()
                || argMultimap.getValue(PREFIX_TIME).isEmpty()
                || hasAny(argMultimap, PREFIX_WEEK, PREFIX_CANCEL, PREFIX_RESCHEDULE, PREFIX_INDEX)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ADD, PREFIX_DATE, PREFIX_TIME);
            LocalDate firstDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get());
            LocalTime time = ParserUtil.parseTime(argMultimap.getValue(PREFIX_TIME).get());
            return new AddAppointmentCommand(selector, firstDate, time, clock);
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), usage), pe);
        }
    }

    /**
     * Parses the arguments of {@code appointment --cancel}.
     */
    private CancelAppointmentCommand parseCancel(ArgumentMultimap argMultimap) throws ParseException {
        String usage = CancelAppointmentCommand.MESSAGE_USAGE;
        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, usage, PREFIX_ID, PREFIX_NAME_LONG);
        if (hasValueAfter(argMultimap, PREFIX_CANCEL) || argMultimap.getValue(PREFIX_INDEX).isEmpty()
                || hasAny(argMultimap, PREFIX_DATE, PREFIX_TIME, PREFIX_WEEK, PREFIX_RESCHEDULE)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CANCEL, PREFIX_INDEX);
            return new CancelAppointmentCommand(selector, parseAppointmentIndex(argMultimap), clock);
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), usage), pe);
        }
    }

    /**
     * Parses the arguments of {@code appointment --reschedule}.
     */
    private RescheduleAppointmentCommand parseReschedule(ArgumentMultimap argMultimap) throws ParseException {
        String usage = RescheduleAppointmentCommand.MESSAGE_USAGE;
        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, usage, PREFIX_ID, PREFIX_NAME_LONG);
        boolean isMissingArgument = argMultimap.getValue(PREFIX_INDEX).isEmpty()
                || argMultimap.getValue(PREFIX_DATE).isEmpty() || argMultimap.getValue(PREFIX_TIME).isEmpty();
        if (hasValueAfter(argMultimap, PREFIX_RESCHEDULE) || isMissingArgument
                || hasAny(argMultimap, PREFIX_WEEK)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_RESCHEDULE, PREFIX_INDEX, PREFIX_DATE, PREFIX_TIME);
            Index index = parseAppointmentIndex(argMultimap);
            LocalDate date = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get());
            LocalTime time = ParserUtil.parseTime(argMultimap.getValue(PREFIX_TIME).get());
            return new RescheduleAppointmentCommand(selector, index, date, time, clock);
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), usage), pe);
        }
    }

    /**
     * Parses the value of {@code --index}, which may be in double quotes.
     */
    private static Index parseAppointmentIndex(ArgumentMultimap argMultimap) throws ParseException {
        return ParserUtil.parseIndex(ParserUtil.removeOptionalQuotes(argMultimap.getValue(PREFIX_INDEX).get()));
    }
}
