package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final int INVALID_ID = 0;
    private static final int VALID_ID = BENSON.getId().value;
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();

    @Test
    public void toModelType_absentWorkoutPlan_defaultsToEmpty() throws Exception {
        String fields = "\"id\": 1, \"name\": \"Alice\", \"phone\": \"91234567\"";
        for (String suffix : new String[] {"", ", \"workoutPlan\": null", ", \"workoutPlan\": \"\""}) {
            JsonAdaptedPerson person = JsonUtil.fromJsonString("{" + fields + suffix + "}", JsonAdaptedPerson.class);
            assertEquals("", person.toModelType().getWorkoutPlan().value);
        }
    }

    @Test
    public void toModelType_absentNote_defaultsToEmptyAndPreservesPlan() throws Exception {
        String fields = "\"id\": 1, \"name\": \"Alice\", \"phone\": \"91234567\","
                + " \"workoutPlan\": \"Squats\"";
        for (String suffix : new String[] {"", ", \"note\": null", ", \"note\": \"\""}) {
            JsonAdaptedPerson adapted = JsonUtil.fromJsonString("{" + fields + suffix + "}", JsonAdaptedPerson.class);
            Person person = adapted.toModelType();
            assertEquals("", person.getNote().value);
            assertEquals("Squats", person.getWorkoutPlan().value);
        }
    }

    @Test
    public void toModelType_workoutPlanOnlyConstructor_preservesPlanAndDefaultsOtherDetails() throws Exception {
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, "Squats\nRest");
        assertEquals(new PersonBuilder(BENSON).withWorkoutPlan("Squats\nRest").build(), adapted.toModelType());
    }

    @Test
    public void toModelType_nullWorkoutPlanConstructor_defaultsToEmpty() throws Exception {
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, (String) null);
        assertEquals(BENSON, adapted.toModelType());
    }

    @Test
    public void getId_missingSavedId_throwsAssertionError() {
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(null, VALID_NAME, VALID_PHONE);
        assertThrows(AssertionError.class, adapted::getId);
    }

    @Test
    public void toModelType_tagListChangedAfterConstruction_preservesSavedDetails() throws Exception {
        List<JsonAdaptedTag> tags = new ArrayList<>(List.of(new JsonAdaptedTag("gym")));
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, "Injury", false,
                tags, "Squats\nRest");

        tags.clear();
        tags.add(new JsonAdaptedTag("personal"));

        assertEquals(new PersonBuilder(BENSON).withNote("Injury").withActive(false).withTags("gym")
                .withWorkoutPlan("Squats\nRest").build(), adapted.toModelType());
    }

    @Test
    public void withId_personWithWorkoutPlan_preservesPlan() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_NAME, VALID_PHONE, "Knee injury", false,
                List.of(new JsonAdaptedTag("gym")), "Squats: 3 sets of 12");
        assertEquals(new PersonBuilder(BENSON).withId(5).withNote("Knee injury").withActive(false).withTags("gym")
                .withWorkoutPlan("Squats: 3 sets of 12").build(),
                person.withId(5).toModelType());
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_personWithAllDetails_returnsPerson() throws Exception {
        Person inactiveTaggedPerson = new PersonBuilder(BENSON).withNote("Knee injury").withActive(false)
                .withTags("gym").withWorkoutPlan("Squats\nRest").build();
        assertEquals(inactiveTaggedPerson, new JsonAdaptedPerson(inactiveTaggedPerson).toModelType());
    }

    @Test
    public void toModelType_missingNoteActiveAndTags_returnsActivePersonWithoutNoteOrTags() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, null, null, null);
        assertEquals(new PersonBuilder(BENSON).withNote("").withActive(true).withTags().build(),
                person.toModelType());
    }

    @Test
    public void toModelType_longMultilineNote_preservesText() throws Exception {
        String text = "  " + "a".repeat(501) + "\nRest\\nLiteral  ";
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, text, true, null);
        assertEquals(text, person.toModelType().getNote().value);
    }

    @Test
    public void toModelType_nullTag_throwsIllegalValueException() {
        List<JsonAdaptedTag> tagsWithNull = new ArrayList<>();
        tagsWithNull.add(null);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, "", true, tagsWithNull);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Tag.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTag_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, VALID_PHONE, "", true,
                List.of(new JsonAdaptedTag("#gym")));
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, INVALID_NAME, VALID_PHONE);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, null, VALID_PHONE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, INVALID_PHONE);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_ID, VALID_NAME, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidId_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_ID, VALID_NAME, VALID_PHONE);
        assertThrows(IllegalValueException.class, PersonId.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_NAME, VALID_PHONE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "ID");
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void withId_personWithoutId_returnsPersonWithGivenId() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_NAME, VALID_PHONE);
        assertEquals(new PersonBuilder(BENSON).withId(5).build(), person.withId(5).toModelType());
    }
}
