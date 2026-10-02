package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Remark;

public class RemarkCommandTest {

    @Test
    public void execute_addAndRemoveRemark_updatesPerson() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        RemarkCommand add = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes to swim"));
        add.execute(model);
        assertEquals(new Remark("Likes to swim"), model.getFilteredPersonList().get(0).getRemark());

        RemarkCommand remove = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        remove.execute(model);
        assertEquals(new Remark(""), model.getFilteredPersonList().get(0).getRemark());
    }
}
