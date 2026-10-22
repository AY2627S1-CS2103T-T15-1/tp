package seedu.address.model.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.address.model.AddressBook;
import seedu.address.model.LongFormText;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.session.Session;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    /**
     * Returns sample training clients, with dates relative to the day the samples are created.
     * Once saved, these dates are loaded from storage like any other client data.
     */
    public static Person[] getSamplePersons() {
        LocalDate today = LocalDate.now();
        Tag morning = new Tag("morning");
        Tag evening = new Tag("evening");
        Tag strength = new Tag("strength");

        List<Appointment> berniceAppointments = new ArrayList<>(
                Appointment.weeklySeries(today.plusDays(2), LocalTime.of(18, 0)));
        berniceAppointments.set(1, berniceAppointments.get(1).cancel());

        List<Appointment> charlotteAppointments = new ArrayList<>(
                Appointment.weeklySeries(today.plusDays(3), LocalTime.of(9, 0)));
        Appointment movedAppointment = charlotteAppointments.get(1);
        charlotteAppointments.set(1,
                movedAppointment.rescheduleTo(movedAppointment.getDate().plusDays(1), LocalTime.of(10, 0)));

        return new Person[] {
            new Person(new PersonId(1), new Name("Alex Yeoh"), new Phone("87438807"),
                    new LongFormText("Building strength for his first pull-up. Prefers early morning sessions."),
                    true, Set.of(Tag.GYM, morning, strength),
                    new LongFormText("Warm-up: 5 minutes rowing\nSquats: 3 sets of 8\n"
                            + "Assisted pull-ups: 3 sets of 6\nPlank: 3 sets of 30 seconds"),
                    List.of(new Session(today.minusWeeks(2), "Practised squat technique and assisted pull-ups."),
                            new Session(today.minusWeeks(1), "Completed all squat sets; reduced pull-up assistance.")),
                    Appointment.weeklySeries(today.plusDays(1), LocalTime.of(7, 0))),
            new Person(new PersonId(2), new Name("Bernice Yu"), new Phone("99272758"),
                    new LongFormText("Training for a 5 km run. Away during the second scheduled week."),
                    true, Set.of(Tag.PERSONAL, evening),
                    new LongFormText("Easy jog: 15 minutes\nStep-ups: 3 sets of 10\n"
                            + "Calf raises: 3 sets of 12\nCool-down: 5 minutes walking"),
                    List.of(new Session(today.minusDays(5), "Completed a comfortable 15-minute jog.")),
                    berniceAppointments),
            new Person(new PersonId(3), new Name("Charlotte Oliveiro"), new Phone("93210283"),
                    new LongFormText("Prefers low-impact workouts. Second appointment moved to the following day."),
                    true, Set.of(Tag.PERSONAL, morning),
                    new LongFormText("Stationary bike: 10 minutes\nGlute bridges: 3 sets of 12\n"
                            + "Resistance band rows: 3 sets of 10\nGentle stretching: 5 minutes"),
                    List.of(new Session(today.minusDays(4), "Completed low-impact circuit at a steady pace.")),
                    charlotteAppointments),
            new Person(new PersonId(4), new Name("David Li"), new Phone("91031282"),
                    new LongFormText("Training paused while overseas. Review goals when he returns."),
                    false, Set.of(Tag.GYM, evening, strength),
                    new LongFormText("Goblet squats: 3 sets of 10\nDumbbell rows: 3 sets of 10\n"
                            + "Push-ups: 3 sets of 8"),
                    List.of(new Session(today.minusWeeks(4), "Completed final strength session before travel."))),
            new Person(new PersonId(5), new Name("Irfan Ibrahim"), new Phone("92492021"),
                    new LongFormText("Working on consistent technique before increasing weights."),
                    true, Set.of(Tag.GYM, evening, strength),
                    new LongFormText("Deadlifts: 3 sets of 5\nDumbbell press: 3 sets of 8\n"
                            + "Seated rows: 3 sets of 10"),
                    List.of(new Session(today.minusWeeks(2), "Practised hip hinge with light weights."),
                            new Session(today.minusDays(3), "Maintained consistent form across all deadlift sets.")),
                    Appointment.weeklySeries(today.plusDays(1), LocalTime.of(19, 0))),
            new Person(new PersonId(6), new Name("Roy Balakrishnan"), new Phone("92624417"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }
}
