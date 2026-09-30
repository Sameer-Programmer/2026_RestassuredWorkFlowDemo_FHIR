package api.payload;

import api.utilities.FakerUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PayLoadPatient {

    public static PatientData getPatientPayload() throws IOException {

        var faker = FakerUtil.getFaker();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String phone = faker.phoneNumber().cellPhone();
        String gender = "male";
        String birthDate = "1990-05-12";
        String street = faker.address().streetAddress();
        String city = faker.address().city();
        String state = faker.address().stateAbbr();
        String postalCode = faker.address().zipCode();

        Path patientData = Path.of(
                System.getProperty("user.dir"),
                "TestData",
                "patient.json"
        );

        String payload = Files.readString(patientData);

        payload = payload.replace("{{firstName}}", firstName);
        payload = payload.replace("{{lastName}}", lastName);
        payload = payload.replace("{{phone}}", phone);
        payload = payload.replace("{{gender}}", gender);
        payload = payload.replace("{{birthDate}}", birthDate);
        payload = payload.replace("{{street}}", street);
        payload = payload.replace("{{city}}", city);
        payload = payload.replace("{{state}}", state);
        payload = payload.replace("{{postalCode}}", postalCode);

        return new PatientData(
                firstName,
                lastName,
                phone,
                gender,
                birthDate,
                street,
                city,
                state,
                postalCode,
                payload
        );
    }
}
