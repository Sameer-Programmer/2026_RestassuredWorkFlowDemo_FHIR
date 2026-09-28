package api.test;

import api.endPoints.PatientEndpoints;
import api.payload.PatientData;
import api.payload.PayLoadPatient;
import api.utilities.ConfigReader;
import api.utilities.FhirValidatorUtil;

import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

public class PatientTest {

    Properties prop;

    @BeforeClass
    public void setup() throws IOException {
        prop = ConfigReader.initProperties();
    }

    // ============================================================
    // CREATE PATIENT - POST
    // ============================================================

    @Test
    public void createPatient(ITestContext context) throws IOException {

        // --------------------------------------------------------
        // 1. Get POST URL
        // --------------------------------------------------------

        String url = prop.getProperty("post_url");

        // --------------------------------------------------------
        // 2. Generate Patient test data + JSON payload
        // --------------------------------------------------------

        PatientData patientData =
                PayLoadPatient.getPatientPayload();

        String payload = patientData.getPayload();

        System.out.println("========== CREATE PATIENT ==========");
        System.out.println("\nRequest Payload:");
        System.out.println(payload);

        // --------------------------------------------------------
        // 3. FHIR R4 validation - REQUEST
        // --------------------------------------------------------

        System.out.println("\n========== FHIR REQUEST VALIDATION ==========");

        FhirValidatorUtil.validate(payload);

        System.out.println("FHIR request validation passed.");

        // --------------------------------------------------------
        // 4. Send POST request
        // --------------------------------------------------------

        Response response =
                PatientEndpoints.createPatient(url, payload);

        // --------------------------------------------------------
        // 5. Print response
        // --------------------------------------------------------

        System.out.println("\nResponse:");
        System.out.println(response.asPrettyString());

        // --------------------------------------------------------
        // 6. HTTP status code validation
        // --------------------------------------------------------

        Assert.assertEquals(
                response.statusCode(),
                201,
                "Patient creation failed. Expected HTTP 201."
        );

        // --------------------------------------------------------
        // 7. FHIR R4 validation - RESPONSE
        // --------------------------------------------------------

        System.out.println("\n========== FHIR RESPONSE VALIDATION ==========");

        FhirValidatorUtil.validate(
                response.asString()
        );

        System.out.println("FHIR response validation passed.");

        // --------------------------------------------------------
        // 8. Validate FHIR resource type
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("resourceType"),
                "Patient",
                "Resource type is not Patient"
        );

        // --------------------------------------------------------
        // 9. Get Patient ID
        // --------------------------------------------------------

        String patientId =
                response.jsonPath().getString("id");

        // --------------------------------------------------------
        // 10. Validate Patient ID
        // --------------------------------------------------------

        Assert.assertNotNull(
                patientId,
                "Patient ID should not be null"
        );

        Assert.assertFalse(
                patientId.isBlank(),
                "Patient ID should not be empty"
        );

        // --------------------------------------------------------
        // 11. Validate Patient name
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("name[0].family"),
                patientData.getLastName(),
                "Family name mismatch"
        );

        Assert.assertEquals(
                response.jsonPath().getString("name[0].given[0]"),
                patientData.getFirstName(),
                "First name mismatch"
        );

        // --------------------------------------------------------
        // 12. Validate Telecom
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("telecom[0].value"),
                patientData.getPhone(),
                "Phone number mismatch"
        );

        // --------------------------------------------------------
        // 13. Validate Gender
        // --------------------------------------------------------

        String gender =
                response.jsonPath().getString("gender");

        Assert.assertEquals(
                gender,
                patientData.getGender(),
                "Gender mismatch"
        );

        // FHIR Patient.gender allowed values
        Assert.assertTrue(
                gender.equals("male")
                        || gender.equals("female")
                        || gender.equals("other")
                        || gender.equals("unknown"),
                "Invalid FHIR Patient.gender value: " + gender
        );

        // --------------------------------------------------------
        // 14. Validate Birth Date
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("birthDate"),
                patientData.getBirthDate(),
                "Birth date mismatch"
        );

        // --------------------------------------------------------
        // 15. Validate Address
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("address[0].line[0]"),
                patientData.getStreet(),
                "Street mismatch"
        );

        Assert.assertEquals(
                response.jsonPath().getString("address[0].city"),
                patientData.getCity(),
                "City mismatch"
        );

        Assert.assertEquals(
                response.jsonPath().getString("address[0].state"),
                patientData.getState(),
                "State mismatch"
        );

        Assert.assertEquals(
                response.jsonPath().getString("address[0].postalCode"),
                patientData.getPostalCode(),
                "Postal code mismatch"
        );

        // --------------------------------------------------------
        // 16. Store Patient ID for next test
        // --------------------------------------------------------

        context.setAttribute(
                "patientId",
                patientId
        );

        System.out.println(
                "\nCreated Patient ID: " + patientId
        );

        System.out.println(
                "\n========== CREATE PATIENT VALIDATION PASSED =========="
        );
    }


    // ============================================================
    // GET PATIENT - GET
    // ============================================================

    @Test(dependsOnMethods = "createPatient")
    public void getPatient(ITestContext context) {

        // --------------------------------------------------------
        // 1. Get Patient ID created by POST
        // --------------------------------------------------------

        String patientId =
                (String) context.getAttribute("patientId");

        Assert.assertNotNull(
                patientId,
                "Patient ID was not available from createPatient test"
        );

        // --------------------------------------------------------
        // 2. Get GET URL from config
        // --------------------------------------------------------

        String url = prop.getProperty("get_url")
                .replace(
                        "{patientId}",
                        patientId
                );

        // --------------------------------------------------------
        // 3. Send GET request
        // --------------------------------------------------------

        Response response =
                PatientEndpoints.getPatient(url);

        // --------------------------------------------------------
        // 4. Print response
        // --------------------------------------------------------

        System.out.println(
                "========== GET PATIENT =========="
        );

        System.out.println(
                response.asPrettyString()
        );

        // --------------------------------------------------------
        // 5. Validate HTTP status
        // --------------------------------------------------------

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Failed to retrieve Patient"
        );

        // --------------------------------------------------------
        // 6. FHIR R4 validation
        // --------------------------------------------------------

        System.out.println(
                "\n========== FHIR GET RESPONSE VALIDATION =========="
        );

        FhirValidatorUtil.validate(
                response.asString()
        );

        System.out.println(
                "FHIR GET response validation passed."
        );

        // --------------------------------------------------------
        // 7. Validate resource type
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("resourceType"),
                "Patient",
                "Resource type is not Patient"
        );

        // --------------------------------------------------------
        // 8. Validate Patient ID
        // --------------------------------------------------------

        Assert.assertEquals(
                response.jsonPath().getString("id"),
                patientId,
                "Patient ID mismatch"
        );

        // --------------------------------------------------------
        // 9. Validate basic Patient structure
        // --------------------------------------------------------

        Assert.assertNotNull(
                response.jsonPath().getList("name"),
                "Patient name should be present"
        );

        Assert.assertNotNull(
                response.jsonPath().getString("gender"),
                "Patient gender should be present"
        );

        Assert.assertNotNull(
                response.jsonPath().getString("birthDate"),
                "Patient birthDate should be present"
        );

        System.out.println(
                "\n========== GET PATIENT VALIDATION PASSED =========="
        );
    }
}