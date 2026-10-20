package seedu.address.testutil;

import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    private PersonId id;
    private Name name;
    private Phone phone;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        id = new PersonId(DEFAULT_ID);
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        id = personToCopy.getId();
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
    }

    /**
     * Sets the {@code PersonId} of the {@code Person} that we are building.
     */
    public PersonBuilder withId(int id) {
        this.id = new PersonId(id);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    public Person build() {
        return new Person(id, name, phone);
    }

}
