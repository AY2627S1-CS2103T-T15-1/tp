package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Sets or replaces the workout plan of a client chosen by ID or by name.
 */
public class PlanCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "plan";
    public static final String MESSAGE_PLAN_SUCCESS = "Updated workout plan for client: %1$s";

    private final String workoutPlan;

    /**
     * Creates a command that replaces the chosen client's workout plan.
     * Both arguments must not be null. An empty plan clears the existing plan.
     */
    public PlanCommand(PersonSelector selector, String workoutPlan) {
        super(selector);
        this.workoutPlan = requireNonNull(workoutPlan);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        Person updated = new Person(target.getId(), target.getName(), target.getPhone(), workoutPlan);
        model.setPerson(target, updated);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_PLAN_SUCCESS, Messages.format(updated)));
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PlanCommand otherCommand)) {
            return false;
        }
        return selector.equals(otherCommand.selector)
                && workoutPlan.equals(otherCommand.workoutPlan);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("selector", selector)
                .add("workoutPlan", workoutPlan)
                .toString();
    }
}
