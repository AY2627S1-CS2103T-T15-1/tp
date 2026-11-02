package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.PersonSelector;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete --id 9";
        assertCommandException(deleteCommand, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 9));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_NO_CLIENTS, model);
    }

    @Test
    public void execute_longQuotedNote_savesFullText() throws Exception {
        model.addPerson(new PersonBuilder(AMY).withId(1).build());
        String text = "a".repeat(501);

        CommandResult result = logic.execute("note --id 1 --note \"" + text + "\"");

        assertEquals(text, result.getPersonToView().orElseThrow().getNote().value);
        JsonAddressBookStorage savedData =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(text, savedData.readAddressBook().orElseThrow().getPersonList().get(0).getNote().value);
    }

    @Test
    public void execute_noteWithNewlineEscape_displaysAndSavesActualNewline() throws Exception {
        model.addPerson(new PersonBuilder(AMY).withId(1).withWorkoutPlan("Existing plan").build());

        CommandResult result = logic.execute("note --id 1 --note \"Injury\\nRest\"");

        assertEquals("Injury\nRest", result.getPersonToView().orElseThrow().getNote().value);
        JsonAddressBookStorage savedData =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        Person saved = savedData.readAddressBook().orElseThrow().getPersonList().get(0);
        assertEquals("Injury\nRest", saved.getNote().value);
        assertEquals("Existing plan", saved.getWorkoutPlan().value);
    }

    @Test
    public void execute_planCommands_updatesAndSavesPlan() throws Exception {
        model.addPerson(new PersonBuilder(AMY).withId(1).build());
        JsonAddressBookStorage savedData =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));

        logic.execute("plan --id 1 --plan \"Squats: 3 sets of 12\"");
        assertEquals("Squats: 3 sets of 12", model.getAddressBook().getPersonList().get(0).getWorkoutPlan().value);
        assertEquals("Squats: 3 sets of 12",
                savedData.readAddressBook().get().getPersonList().get(0).getWorkoutPlan().value);

        logic.execute("plan --name Amy --plan \"Push-ups: 3 sets of 10\"");
        assertEquals("Push-ups: 3 sets of 10",
                savedData.readAddressBook().get().getPersonList().get(0).getWorkoutPlan().value);

        logic.execute("plan --id 1 --plan \"\"");
        assertEquals("", model.getAddressBook().getPersonList().get(0).getWorkoutPlan().value);
        assertEquals("", savedData.readAddressBook().get().getPersonList().get(0).getWorkoutPlan().value);
    }

    @Test
    public void execute_planWithNewlineEscape_displaysAndSavesActualNewline() throws Exception {
        model.addPerson(new PersonBuilder(AMY).withId(1).build());

        CommandResult result = logic.execute("plan --id 1 --plan \"Squats\\nPush-ups\"");

        assertEquals("Squats\nPush-ups", result.getPersonToView().get().getWorkoutPlan().value);
        JsonAddressBookStorage savedData =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals("Squats\nPush-ups",
                savedData.readAddressBook().get().getPersonList().get(0).getWorkoutPlan().value);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_listCommandsWhenSaveFails_stillSucceed() throws Exception {
        useStorageThatThrows(DUMMY_AD_EXCEPTION);
        assertEquals(ListCommand.MESSAGE_NO_CLIENTS, logic.execute(ListCommand.COMMAND_WORD).getFeedbackToUser());
        assertEquals(ListCommand.MESSAGE_NO_CLIENTS_TODAY,
                logic.execute(ListCommand.COMMAND_WORD + " --today").getFeedbackToUser());
    }

    @Test
    public void execute_viewWhenSaveFails_returnsPersonToView() throws Exception {
        model.addPerson(AMY);
        ReadOnlyAddressBook previousData = new AddressBook(model.getAddressBook());
        useStorageThatThrows(DUMMY_AD_EXCEPTION);

        assertEquals(AMY, logic.execute("view --id 10").getPersonToView().orElseThrow());
        assertEquals(AMY, logic.execute("view --name Amy").getPersonToView().orElseThrow());
        assertEquals(previousData, model.getAddressBook());
    }

    @Test
    public void execute_findWhenSaveFails_filtersClients() throws Exception {
        model.addPerson(AMY);
        model.addPerson(new PersonBuilder().withId(11).withName("Bob").build());
        ReadOnlyAddressBook previousData = new AddressBook(model.getAddressBook());
        useStorageThatThrows(DUMMY_AD_EXCEPTION);

        assertEquals("Found 1 client", logic.execute("find --name Amy").getFeedbackToUser());
        assertEquals(List.of(AMY), model.getFilteredPersonList());
        assertEquals(previousData, model.getAddressBook());
    }

    @Test
    public void execute_listGroupsWhenSaveFails_listsGroups() throws Exception {
        model.addGroup(new Tag("morning"));
        ReadOnlyAddressBook previousData = new AddressBook(model.getAddressBook());
        useStorageThatThrows(DUMMY_AD_EXCEPTION);

        assertEquals("Groups (1): morning", logic.execute("group --list").getFeedbackToUser());
        assertEquals(previousData, model.getAddressBook());
    }

    @Test
    public void execute_listAppointmentsWhenSaveFails_listsAppointmentsAndFiltersClients() throws Exception {
        Appointment appointment = new Appointment(LocalDate.of(2026, 10, 19), LocalTime.of(9, 0));
        Person amy = new PersonBuilder(AMY).withAppointments(appointment).build();
        model.addPerson(amy);
        model.addPerson(new PersonBuilder().withId(11).withName("Bob").build());
        ReadOnlyAddressBook previousData = new AddressBook(model.getAddressBook());
        useStorageThatThrows(DUMMY_AD_EXCEPTION);

        assertEquals("Appointments on Mon 2026-10-19:\n09:00 Amy Bee (ID: 10)",
                logic.execute("appointment --date 2026-10-19").getFeedbackToUser());
        assertEquals(List.of(amy), model.getFilteredPersonList());
        assertEquals("Appointments from Mon 2026-10-19 to Sun 2026-10-25:\n"
                + "Mon 2026-10-19 09:00 Amy Bee (ID: 10)",
                logic.execute("appointment --week 2026-10-22").getFeedbackToUser());
        assertEquals(List.of(amy), model.getFilteredPersonList());
        assertEquals(previousData, model.getAddressBook());
    }

    @Test
    public void execute_exitWhenSaveFails_returnsExitResult() throws Exception {
        model.addPerson(AMY);
        ReadOnlyAddressBook previousData = new AddressBook(model.getAddressBook());
        useStorageThatThrows(DUMMY_AD_EXCEPTION);

        assertTrue(logic.execute("exit").isExit());
        assertEquals(previousData, model.getAddressBook());
    }

    @Test
    public void execute_confirmedClearWhenSaveFails_restoresData() {
        model.addPerson(AMY);
        useStorageThatThrows(DUMMY_AD_EXCEPTION);

        assertCommandException("clear --confirm", String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        useStorageThatThrows(e);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = PersonUtil.getAddCommand(AMY);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage);
    }

    /**
     * Replaces {@code logic} with one whose Storage component throws {@code e} whenever it saves the address book.
     */
    private void useStorageThatThrows(IOException e) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);
    }
}
