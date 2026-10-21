package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_LONG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PLAN;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.LongFormText;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Sets or replaces the workout plan of a client chosen by ID or by name.
 */
public class PlanCommand extends PersonTargetedCommand {

    public static final String COMMAND_WORD = "plan";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sets or replaces a client's workout plan, chosen by ID or by part of their name.\n"
            + "Parameters: " + PREFIX_ID + " ID or " + PREFIX_NAME_LONG + " NAME, then " + PREFIX_PLAN + " \"TEXT\"\n"
            + "TEXT must be in double quotes. Use " + PREFIX_PLAN + " \"\" to clear the plan.\n"
            + "Use \\n inside TEXT for a line break.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ID + " 2 " + PREFIX_PLAN + " \"Squats: 3 sets of 12\"";

    public static final String MESSAGE_PLAN_SUCCESS = "Updated workout plan for client: %1$s";

    private final LongFormText workoutPlan;

    /**
     * Creates a command that replaces the chosen client's workout plan.
     * Both arguments must not be null. An empty plan clears the existing plan.
     */
    public PlanCommand(PersonSelector selector, LongFormText workoutPlan) {
        super(selector);
        this.workoutPlan = requireNonNull(workoutPlan);
    }

    @Override
    protected CommandResult executeOn(Person target, Model model) {
        Person updated = target.withWorkoutPlan(workoutPlan);
        model.setPerson(target, updated);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_PLAN_SUCCESS, Messages.format(updated)), updated);
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    /**
     * Keeps the plan and its line breaks in a complete single-line retry command.
     */
    @Override
    protected String getArgumentsAfterSelector() {
        return " " + PREFIX_PLAN + " " + quoteLongFormText(workoutPlan);
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
