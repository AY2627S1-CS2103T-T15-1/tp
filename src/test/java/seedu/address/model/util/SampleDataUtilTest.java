package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;

public class SampleDataUtilTest {
    @Test
    public void getSamplePersons_containsActiveAppointmentToday() {
        LocalDate today = LocalDate.now();

        assertTrue(Arrays.stream(SampleDataUtil.getSamplePersons())
                .flatMap(person -> person.getAppointments().stream())
                .anyMatch(appointment -> appointment.getDate().equals(today) && !appointment.isCancelled()));
    }

    @Test
    public void getSamplePersons_preservesTrainingAndAppointmentExamples() {
        List<Person> persons = Arrays.asList(SampleDataUtil.getSamplePersons());

        assertTrue(persons.stream().anyMatch(person -> !person.getNote().isBlank()));
        assertTrue(persons.stream().anyMatch(person -> !person.getWorkoutPlan().isBlank()));
        assertTrue(persons.stream().anyMatch(person -> !person.getSessions().isEmpty()));
        assertTrue(persons.stream().flatMap(person -> person.getAppointments().stream())
                .anyMatch(appointment -> appointment.isCancelled()));
        Person charlotte = persons.stream().filter(person -> person.getId().value == 3).findFirst().orElseThrow();
        assertEquals(charlotte.getAppointments().get(0).getDate().plusWeeks(1).plusDays(1),
                charlotte.getAppointments().get(1).getDate());
    }
}
