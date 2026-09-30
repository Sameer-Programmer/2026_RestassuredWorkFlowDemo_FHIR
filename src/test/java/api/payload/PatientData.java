package api.payload;

import api.utilities.FakerUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PatientData {

    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String birthDate;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String payload;

    public PatientData() throws IOException {
        var faker = FakerUtil.getFaker();

        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        phone = faker.phoneNumber().cellPhone();
        gender = "male";
        birthDate = "1990-05-12";
        street = faker.address().streetAddress();
        city = faker.address().city();
        state = faker.address().stateAbbr();
        postalCode = faker.address().zipCode();

        Path patientData = Path.of(
                System.getProperty("user.dir"),
                "TestData",
                "patient.json"
        );

        payload = Files.readString(patientData);
        payload = payload.replace("{{firstName}}", firstName);
        payload = payload.replace("{{lastName}}", lastName);
        payload = payload.replace("{{phone}}", phone);
        payload = payload.replace("{{gender}}", gender);
        payload = payload.replace("{{birthDate}}", birthDate);
        payload = payload.replace("{{street}}", street);
        payload = payload.replace("{{city}}", city);
        payload = payload.replace("{{state}}", state);
        payload = payload.replace("{{postalCode}}", postalCode);
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
