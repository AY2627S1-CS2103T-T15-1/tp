package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.Optional;

import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Note;

/**
 * Parses input arguments and creates a new NoteCommand object
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    public static final String MESSAGE_NOTE_NOT_QUOTED = "Put the note in double quotes, e.g. "
            + PREFIX_NOTE + " \"Knee injury\". Use " + PREFIX_NOTE + " \"\" to remove the note.";

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public NoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_NAME_LONG, PREFIX_NOTE);

        PersonSelector selector = ParserUtil.parsePersonSelector(argMultimap, NoteCommand.MESSAGE_USAGE,
                PREFIX_ID, PREFIX_NAME_LONG);
        Optional<String> noteValue = argMultimap.getValue(PREFIX_NOTE);
        if (noteValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE);
            return new NoteCommand(selector, parseQuotedNote(noteValue.get()));
        } catch (ParseException pe) {
            throw new ParseException(ParserUtil.withUsage(pe.getMessage(), NoteCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses {@code quotedNote}, which must be surrounded by double quotes, into a {@code Note}.
     *
     * @throws ParseException if {@code quotedNote} is not surrounded by double quotes or the note is invalid.
     */
    private static Note parseQuotedNote(String quotedNote) throws ParseException {
        String trimmedNote = quotedNote.trim();
        boolean isQuoted = trimmedNote.length() >= 2 && trimmedNote.startsWith("\"") && trimmedNote.endsWith("\"");
        if (!isQuoted) {
            throw new ParseException(MESSAGE_NOTE_NOT_QUOTED);
        }

        String noteText = trimmedNote.substring(1, trimmedNote.length() - 1);
        if (!Note.isValidNote(noteText)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(noteText);
    }

}
