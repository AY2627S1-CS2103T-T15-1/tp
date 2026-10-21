package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks that workout plans and upstream client details survive each other's commands.
 */
public class WorkoutPlanIntegrationTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_plan_preservesNoteStatusTagsAndContactDetails() throws Exception {
        Person original = detailedClient();
        assertUpdate("plan --id 1 --note \"New plan\\nRest\"", original,
                new PersonBuilder(original).withWorkoutPlan("New plan\nRest").build());
    }

    @Test
    public void execute_edit_preservesAllAdditionalDetails() throws Exception {
        Person original = detailedClient();
        assertUpdate("edit --id 1 --to-name Alice Tan --to-phone 81234567", original,
                new PersonBuilder(original).withName("Alice Tan").withPhone("81234567").build());
    }

    @Test
    public void execute_noteTagAndStatus_preserveWorkoutPlanAndOtherDetails() throws Exception {
        Person original = detailedClient();
        assertUpdate("note --id 1 --note \"New note\"", original,
                new PersonBuilder(original).withNote("New note").build());
        assertUpdate("tag --id 1 --type personal", original,
                new PersonBuilder(original).withTags("personal").build());
        assertUpdate("active --id 1", original, new PersonBuilder(original).withActive(true).build());
        Person active = new PersonBuilder(original).withActive(true).build();
        assertUpdate("inactive --id 1", active, original);
    }

    @Test
    public void execute_ambiguousPlan_retryPreservesNewlinesAndOtherClients() throws Exception {
        Person original = detailedClient();
        Person other = new PersonBuilder(original).withId(2).withPhone("81234567").build();
        Model model = new ModelManager();
        model.addPerson(original);
        model.addPerson(other);

        CommandResult result = parser.parseCommand("plan --name Alice --note \"Squats\\nRest\"").execute(model);
        String example = "plan --id 1 --note \"Squats\\nRest\"";
        assertTrue(result.getFeedbackToUser().endsWith(example));
        assertEquals(List.of(original, other), model.getAddressBook().getPersonList());

        CommandResult retry = parser.parseCommand(example).execute(model);
        Person updated = new PersonBuilder(original).withWorkoutPlan("Squats\nRest").build();
        assertEquals(updated, retry.getPersonToView().orElseThrow());
        assertEquals(List.of(updated, other), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_ambiguousNote_retryPreservesNewlinesAndOtherDetails() throws Exception {
        Person original = detailedClient();
        Person other = new PersonBuilder(original).withId(2).withPhone("81234567").build();
        Model model = new ModelManager();
        model.addPerson(original);
        model.addPerson(other);

        CommandResult result = parser.parseCommand("note --name Alice --note \"Injury\\nRest\"").execute(model);
        String example = "note --id 1 --note \"Injury\\nRest\"";
        assertTrue(result.getFeedbackToUser().endsWith(example));
        assertEquals(List.of(original, other), model.getAddressBook().getPersonList());

        CommandResult retry = parser.parseCommand(example).execute(model);
        Person updated = new PersonBuilder(original).withNote("Injury\nRest").build();
        assertEquals(updated, retry.getPersonToView().orElseThrow());
        assertEquals(List.of(updated, other), model.getAddressBook().getPersonList());
    }

    /**
     * Creates a client with every optional detail populated to expose accidental resets.
     */
    private Person detailedClient() {
        return new PersonBuilder(ALICE).withNote("Knee injury").withActive(false).withTags("gym")
                .withWorkoutPlan("Squats\nRest").build();
    }

    /**
     * Executes a parsed command and checks the complete resulting client, including unchanged fields.
     */
    private void assertUpdate(String input, Person original, Person expected) throws Exception {
        Model model = new ModelManager();
        model.addPerson(original);
        parser.parseCommand(input).execute(model);
        assertEquals(List.of(expected), model.getAddressBook().getPersonList());
    }
}
