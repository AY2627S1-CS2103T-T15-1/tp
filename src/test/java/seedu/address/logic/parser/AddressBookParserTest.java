package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.AddToGroupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MoveGroupCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.PlanCommand;
import seedu.address.logic.commands.SetActiveCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.LongFormText;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.TypicalPersons;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_plan() throws Exception {
        assertEquals(new PlanCommand(PersonSelector.byId(new PersonId(2)), new LongFormText("Squats")),
                parser.parseCommand("plan --id 2 --plan \"Squats\""));
    }

    @Test
    public void parseCommand_appointmentAdd() throws Exception {
        assertEquals(new AddAppointmentCommand(PersonSelector.byId(new PersonId(2)), LocalDate.of(2026, 10, 26),
                LocalTime.of(9, 0)), parser.parseCommand("appointment --add --id 2 --date 2026-10-26 --time 09:00"));
    }

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person.getName(), person.getPhone()), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
    }

    @Test
    public void parseCommand_clearInvalidArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parseCommand("clear 3"));
    }

    @Test
    public void parseCommand_clearConfirmed_clearsClientsAndResetsNextId() throws Exception {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        assertEquals(ClearCommand.MESSAGE_SUCCESS,
                parser.parseCommand("clear --confirm").execute(model).getFeedbackToUser());

        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertEquals(new PersonId(1), model.getNextPersonId());
    }

    @Test
    public void parseCommand_clear_preservesClientsAndNextId() throws Exception {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        parser.parseCommand(ClearCommand.COMMAND_WORD).execute(model);

        assertEquals(expectedModel, model);
        assertEquals(expectedModel.getNextPersonId(), model.getNextPersonId());
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(DeleteCommand.COMMAND_WORD + " --id 1");
        assertEquals(new DeleteCommand(PersonSelector.byId(new PersonId(1))), command);
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(ViewCommand.COMMAND_WORD + " --name Alice");
        assertEquals(new ViewCommand(PersonSelector.byName("Alice")), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " --id 1 "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(PersonSelector.byId(new PersonId(1)), descriptor), command);
    }

    @Test
    public void parseCommand_note() throws Exception {
        NoteCommand command = (NoteCommand) parser.parseCommand(NoteCommand.COMMAND_WORD
                + " --id 1 --note \"Knee injury\"");
        assertEquals(new NoteCommand(PersonSelector.byId(new PersonId(1)), new LongFormText("Knee injury")), command);
    }

    @Test
    public void parseCommand_activeAndInactive() throws Exception {
        assertEquals(new SetActiveCommand(PersonSelector.byId(new PersonId(1)), true),
                parser.parseCommand(SetActiveCommand.COMMAND_WORD_ACTIVE + " --id 1"));
        assertEquals(new SetActiveCommand(PersonSelector.byPhone("9123"), false),
                parser.parseCommand(SetActiveCommand.COMMAND_WORD_INACTIVE + " --phone 9123"));
    }

    @Test
    public void parseCommand_tag() throws Exception {
        assertEquals(new TagCommand(PersonSelector.byId(new PersonId(1)), Tag.GYM),
                parser.parseCommand(TagCommand.COMMAND_WORD + " --id 1 --type gym"));
        assertEquals(new AddToGroupCommand(PersonSelector.byId(new PersonId(1)), new Tag("morning")),
                parser.parseCommand(TagCommand.COMMAND_WORD + " --id 1 --group morning"));
        assertEquals(new UntagCommand(PersonSelector.byId(new PersonId(1)), new Tag("morning")),
                parser.parseCommand(TagCommand.COMMAND_WORD + " --id 1 --untag morning"));
        assertEquals(new DeleteGroupCommand(new Tag("morning")),
                parser.parseCommand(TagCommand.COMMAND_WORD + " --delete --group morning"));
    }

    @Test
    public void parseCommand_group() throws Exception {
        assertEquals(new GroupCommand(new Tag("morning")),
                parser.parseCommand(GroupCommand.COMMAND_WORD + " --name morning"));
        assertEquals(new MoveGroupCommand(PersonSelector.byId(new PersonId(1)), new Tag("morning"),
                new Tag("evening")),
                parser.parseCommand(GroupCommand.COMMAND_WORD + " --move --id 1 --from morning --to evening"));
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD + " --name foo bar");
        assertEquals(new FindCommand(List.of(PersonSelector.byName("foo bar"))), command);

        assertEquals(new FindCommand(List.of(), new Tag("morning")),
                parser.parseCommand(FindCommand.COMMAND_WORD + " --group morning"));
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_sessionAdd() throws Exception {
        AddSessionCommand command = (AddSessionCommand) parser.parseCommand(
                "session --add --id 1 --date 2026-10-21 --note Worked on squat form");
        assertEquals(new AddSessionCommand(PersonSelector.byId(new PersonId(1)),
                LocalDate.of(2026, 10, 21), "Worked on squat form"), command);
    }

    @Test
    public void parseCommand_sessionEdit() throws Exception {
        EditSessionCommand command = (EditSessionCommand) parser.parseCommand(
                "session --edit --id 1 --date 2026-10-21 --note Improved squat depth");
        assertEquals(new EditSessionCommand(PersonSelector.byId(new PersonId(1)),
                LocalDate.of(2026, 10, 21), "Improved squat depth"), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
