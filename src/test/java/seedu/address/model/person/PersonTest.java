package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.LongFormText;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void constructor_absentWorkoutPlan_defaultsToEmpty() {
        assertEquals("", new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone()).getWorkoutPlan().value);
        assertEquals("", new PersonBuilder(ALICE).withWorkoutPlan(null).build().getWorkoutPlan().value);
        assertEquals("", new PersonBuilder(ALICE).withWorkoutPlan("").build().getWorkoutPlan().value);
    }

    @Test
    public void constructor_workoutPlanOnly_preservesPlanAndDefaultsOtherDetails() {
        Person person = new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone(), new LongFormText("Squats\nRest"));
        assertEquals(new PersonBuilder(ALICE).withWorkoutPlan("Squats\nRest").build(), person);
        assertTrue(person.getNote().isBlank());
        assertTrue(person.isActive());
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void constructor_noteStatusAndTagsWithoutPlan_defaultsPlanToEmpty() {
        Person person = new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone(), new LongFormText("Injury"),
                false, Set.of(new Tag("gym")));
        assertEquals(new PersonBuilder(ALICE).withNote("Injury").withActive(false).withTags("gym").build(), person);
        assertTrue(person.getWorkoutPlan().isBlank());
    }

    @Test
    public void constructor_tagsChangedAfterConstruction_preservesOriginalDetails() {
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("gym")));
        Person person = new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone(),
                new LongFormText("Injury"), false, tags, new LongFormText("Squats\nRest"));

        tags.clear();
        tags.add(new Tag("personal"));

        assertEquals(new PersonBuilder(ALICE).withNote("Injury").withActive(false).withTags("gym")
                .withWorkoutPlan("Squats\nRest").build(), person);
    }

    @Test
    public void withText_nullValue_throwsWithoutChangingOriginal() {
        Person original = new PersonBuilder(ALICE).withNote("Injury").withWorkoutPlan("Squats").build();
        assertThrows(NullPointerException.class, () -> original.withNote(null));
        assertThrows(NullPointerException.class, () -> original.withWorkoutPlan(null));
        assertEquals("Injury", original.getNote().value);
        assertEquals("Squats", original.getWorkoutPlan().value);
    }

    @Test
    public void constructor_nullRequiredField_throwsNullPointerException() {
        LongFormText plan = new LongFormText("Squats");
        assertThrows(NullPointerException.class, () -> new Person(null, ALICE.getName(), ALICE.getPhone(), plan));
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getId(), null, ALICE.getPhone(), plan));
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getId(), ALICE.getName(), null, plan));
        assertThrows(NullPointerException.class, ()
                -> new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone(), null));
    }

    @Test
    public void equals_differentWorkoutPlans_notEqualButSamePerson() {
        Person withPlan = new PersonBuilder(ALICE).withWorkoutPlan("Squats: 3 sets of 12").build();
        Person samePlan = new PersonBuilder(withPlan).build();
        assertEquals("Squats: 3 sets of 12", withPlan.getWorkoutPlan().value);
        assertEquals(withPlan, samePlan);
        assertEquals(withPlan.hashCode(), samePlan.hashCode());
        assertFalse(ALICE.equals(withPlan));
        assertTrue(ALICE.isSamePerson(withPlan));
    }



    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // same name and phone, different ID -> returns true
        assertTrue(ALICE.isSamePerson(new PersonBuilder(ALICE).withId(99).build()));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, different phone -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // phone has formatting separators, all other attributes same -> returns true
        editedBob = new PersonBuilder(BOB).withPhone("2222 2222").build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true after trimming
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different ID -> returns false
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withId(99).build()));

        // different note -> returns false
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withNote("Knee injury").build()));

        // different active status -> returns false
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withActive(false).build()));

        // different tags -> returns false
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withTags("gym").build()));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{id=" + ALICE.getId() + ", name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone() + ", note=" + ALICE.getNote() + ", isActive=" + ALICE.isActive()
                + ", tags=" + ALICE.getTags() + ", workoutPlan=" + ALICE.getWorkoutPlan() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void constructor_withoutDetails_createsActivePersonWithoutNoteOrTags() {
        Person person = new Person(ALICE.getId(), ALICE.getName(), ALICE.getPhone());
        assertTrue(person.getNote().isBlank());
        assertTrue(person.isActive());
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void withMethods_changeOnlyTheGivenDetail() {
        Person person = new PersonBuilder(ALICE).withNote("Old note").withTags("gym").withWorkoutPlan("Squats").build();

        assertEquals(new PersonBuilder(person).withNote("New note").build(),
                person.withNote(new LongFormText("New note")));
        assertEquals(new PersonBuilder(person).withActive(false).build(), person.withActive(false));
        assertEquals(new PersonBuilder(person).withTags("personal").build(),
                person.withTags(Set.of(new Tag("personal"))));
    }

    @Test
    public void getTags_modifyTags_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().withTags("gym").build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().clear());
    }
}
