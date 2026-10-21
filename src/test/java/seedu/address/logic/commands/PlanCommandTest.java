package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.LongFormText;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests workout plan updates using the real model and client selection flow.
 */
public class PlanCommandTest {

    private static final String WORKOUT_PLAN = "Squats: 3 sets of 12";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void toStringMethod_includesSelectorAndWorkoutPlan() {
        PersonSelector selector = PersonSelector.byId(ALICE.getId());
        PlanCommand command = new PlanCommand(selector, new LongFormText("Squats\nRest"));
        String expected = PlanCommand.class.getCanonicalName() + "{selector=" + selector
                + ", workoutPlan=Squats\nRest}";
        assertEquals(expected, command.toString());
    }

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PlanCommand(null, new LongFormText(WORKOUT_PLAN)));
        assertThrows(NullPointerException.class, () -> new PlanCommand(PersonSelector.byId(ALICE.getId()), null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        PlanCommand command = new PlanCommand(PersonSelector.byId(ALICE.getId()), new LongFormText(WORKOUT_PLAN));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_clientWithoutPlan_setsPlan() {
        assertPlanSuccess(PersonSelector.byId(ALICE.getId()), ALICE, WORKOUT_PLAN);
    }

    @Test
    public void execute_clientWithPlan_replacesEntirePlan() {
        Person withPlan = new PersonBuilder(ALICE).withWorkoutPlan("Push-ups: 3 sets of 10").build();
        model.setPerson(ALICE, withPlan);
        assertPlanSuccess(PersonSelector.byId(ALICE.getId()), withPlan, WORKOUT_PLAN);
    }

    @Test
    public void execute_emptyPlan_clearsPlan() {
        Person withPlan = new PersonBuilder(ALICE).withWorkoutPlan(WORKOUT_PLAN).build();
        model.setPerson(ALICE, withPlan);
        assertPlanSuccess(PersonSelector.byId(ALICE.getId()), withPlan, "",
                "Removed workout plan for client: %1$s");
    }

    @Test
    public void execute_whitespaceOnlyPlan_reportsRemoval() {
        Person withPlan = new PersonBuilder(ALICE).withWorkoutPlan(WORKOUT_PLAN).build();
        model.setPerson(ALICE, withPlan);
        assertPlanSuccess(PersonSelector.byId(ALICE.getId()), withPlan, " \t\n",
                "Removed workout plan for client: %1$s");
    }

    @Test
    public void execute_samePlan_succeeds() {
        Person withPlan = new PersonBuilder(ALICE).withWorkoutPlan(WORKOUT_PLAN).build();
        model.setPerson(ALICE, withPlan);
        assertPlanSuccess(PersonSelector.byId(ALICE.getId()), withPlan, WORKOUT_PLAN);
    }

    @Test
    public void execute_partialNameIgnoringCase_setsPlan() {
        assertPlanSuccess(PersonSelector.byName("ALICE p"), ALICE, WORKOUT_PLAN);
    }

    @Test
    public void execute_clientHiddenByFilter_updatesClientAndShowsAll() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertPlanSuccess(PersonSelector.byId(CARL.getId()), CARL, WORKOUT_PLAN);
    }

    @Test
    public void execute_multipleNameMatches_listsMatchesWithoutUpdatingPlans() {
        Person withPlan = new PersonBuilder(BENSON).withWorkoutPlan("Existing plan").build();
        model.setPerson(BENSON, withPlan);
        PersonSelector selector = PersonSelector.byName("meier");
        PlanCommand command = new PlanCommand(selector, new LongFormText(WORKOUT_PLAN));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(selector::matches);
        String message = "2 clients have a name containing \"meier\". They are listed below.\n"
                + "Use --id to choose one of them, e.g. plan --id 2 --plan \"Squats: 3 sets of 12\"";

        assertCommandSuccess(command, model, message, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandExceptionWithoutChanges() {
        PlanCommand command = new PlanCommand(PersonSelector.byId(new PersonId(999)), new LongFormText(WORKOUT_PLAN));
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_ID, 999));
    }

    @Test
    public void execute_unknownName_throwsCommandExceptionWithoutChanges() {
        PlanCommand command = new PlanCommand(PersonSelector.byName("Zack"), new LongFormText(WORKOUT_PLAN));
        assertCommandFailure(command, model, String.format(PersonSelector.MESSAGE_NO_PERSON_WITH_NAME, "Zack"));
    }

    @Test
    public void equals_comparesSelectorAndPlan() {
        LongFormText plan = new LongFormText(WORKOUT_PLAN);
        PlanCommand command = new PlanCommand(PersonSelector.byId(ALICE.getId()), plan);
        assertTrue(command.equals(command));
        assertEquals(command, new PlanCommand(PersonSelector.byId(ALICE.getId()), new LongFormText(WORKOUT_PLAN)));
        assertFalse(command.equals(new PlanCommand(PersonSelector.byId(BENSON.getId()), plan)));
        PlanCommand differentPlan = new PlanCommand(PersonSelector.byId(ALICE.getId()), new LongFormText("Other plan"));
        assertFalse(command.equals(differentPlan));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
    }

    private void assertPlanSuccess(PersonSelector selector, Person target, String plan) {
        assertPlanSuccess(selector, target, plan, PlanCommand.MESSAGE_PLAN_SUCCESS);
    }

    private void assertPlanSuccess(PersonSelector selector, Person target, String plan, String messageFormat) {
        Person updated = new PersonBuilder(target).withWorkoutPlan(plan).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(target, updated);
        String message = String.format(messageFormat, Messages.format(updated));

        assertCommandSuccess(new PlanCommand(selector, new LongFormText(plan)), model,
                new CommandResult(message, updated), expectedModel);
    }
}
