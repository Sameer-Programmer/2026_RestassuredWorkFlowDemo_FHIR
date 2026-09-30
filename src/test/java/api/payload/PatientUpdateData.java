package api.payload;

import api.utilities.FakerUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Builds a complete FHIR Patient resource for PUT /Patient/{patientId}.
 */
public class PatientUpdateData {
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String gender;
    private final String birthDate;
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String payload;

    private PatientUpdateData(String firstName, String lastName, String phone,
                              String gender, String birthDate, String street,
                              String city, String state, String postalCode,
                              String payload) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = birthDate;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.payload = payload;
    }

    public static PatientUpdateData create(String patientId) throws IOException {
        var faker = FakerUtil.getFaker();
        String firstName = "Updated" + faker.name().firstName();
        String lastName = faker.name().lastName();
        String phone = faker.phoneNumber().cellPhone();
        String gender = "male";
        String birthDate = "1991-06-13";
        String street = faker.address().streetAddress();
        String city = faker.address().city();
        String state = faker.address().stateAbbr();
        String postalCode = faker.address().zipCode();

        Path templatePath = Path.of("TestData", "patient.json");
        String payload = Files.readString(templatePath)
                .replace("{{firstName}}", firstName)
                .replace("{{lastName}}", lastName)
                .replace("{{phone}}", phone)
                .replace("{{gender}}", gender)
                .replace("{{birthDate}}", birthDate)
                .replace("{{street}}", street)
                .replace("{{city}}", city)
                .replace("{{state}}", state)
                .replace("{{postalCode}}", postalCode);

        JSONObject patient = new JSONObject(payload);
        patient.put("id", patientId);

        return new PatientUpdateData(
                firstName, lastName, phone, gender, birthDate,
                street, city, state, postalCode, patient.toString()
        );
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPayload() {
        return payload;
    }
}
