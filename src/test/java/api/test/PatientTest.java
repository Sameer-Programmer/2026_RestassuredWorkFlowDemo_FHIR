package api.test;

import api.endPoints.ObservationEndpoints;
import api.endPoints.PatientEndpoints;
import api.payload.ObservationData;
import api.payload.PatientData;
import api.payload.PayLoadPatient;
import api.payload.PatientUpdateData;
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

    // ============================================================
    // CREATE OBSERVATION - POST
    // ============================================================

    @Test(dependsOnMethods = "getPatient")
    public void createObservation(ITestContext context) throws IOException {
        String patientId = (String) context.getAttribute("patientId");
        Assert.assertNotNull(patientId, "Patient ID was not available for Observation creation");

        ObservationData observationData = ObservationData.create(patientId);
        String payload = observationData.getPayload();
        FhirValidatorUtil.validate(payload);

        String url = prop.getProperty("observation_post_url");
        Response response = ObservationEndpoints.createObservation(url, payload);
        System.out.println("========== CREATE OBSERVATION ==========");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 201,
                "Observation creation failed. Expected HTTP 201.");
        FhirValidatorUtil.validate(response.asString());
        Assert.assertEquals(response.jsonPath().getString("resourceType"), "Observation",
                "Resource type is not Observation");
        Assert.assertEquals(response.jsonPath().getString("status"), "final",
                "Observation status mismatch");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].code"), "4548-4",
                "Observation LOINC code mismatch");
        Assert.assertEquals(response.jsonPath().getDouble("valueQuantity.value"), observationData.getValue(),
                "Observation value mismatch");
        Assert.assertEquals(response.jsonPath().getString("subject.reference"), "Patient/" + patientId,
                "Observation subject mismatch");

        String observationId = response.jsonPath().getString("id");
        Assert.assertNotNull(observationId, "Observation ID should not be null");
        Assert.assertFalse(observationId.isBlank(), "Observation ID should not be empty");
        context.setAttribute("observationId", observationId);
        context.setAttribute("observationData", observationData);
    }

    // ============================================================
    // GET OBSERVATION - GET
    // ============================================================

    @Test(dependsOnMethods = "createObservation")
    public void getObservation(ITestContext context) {
        String observationId = (String) context.getAttribute("observationId");
        Assert.assertNotNull(observationId, "Observation ID was not available from createObservation");

        String url = prop.getProperty("observation_get_url")
                .replace("{observationId}", observationId);
        Response response = ObservationEndpoints.getObservation(url);
        System.out.println("========== GET OBSERVATION ==========");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Failed to retrieve Observation");
        FhirValidatorUtil.validate(response.asString());
        Assert.assertEquals(response.jsonPath().getString("resourceType"), "Observation",
                "Resource type is not Observation");
        Assert.assertEquals(response.jsonPath().getString("id"), observationId,
                "Observation ID mismatch");
        Assert.assertNotNull(response.jsonPath().getString("valueQuantity.value"),
                "Observation value should be present");
        Assert.assertNotNull(response.jsonPath().getString("subject.reference"),
                "Observation subject should be present");
    }

    // ============================================================
    // UPDATE PATIENT - PUT
    // ============================================================

    @Test(dependsOnMethods = "getObservation")
    public void updatePatient(ITestContext context) throws IOException {

        String patientId = (String) context.getAttribute("patientId");
        Assert.assertNotNull(
                patientId,
                "Patient ID was not available from getPatient test"
        );

        String url = prop.getProperty("update_url")
                .replace("{patientId}", patientId);
        PatientUpdateData updateData = PatientUpdateData.create(patientId);
        String payload = updateData.getPayload();

        System.out.println("========== UPDATE PATIENT ==========");
        System.out.println("\nUpdate Payload:");
        System.out.println(payload);

        System.out.println("\n========== FHIR UPDATE REQUEST VALIDATION ==========");
        FhirValidatorUtil.validate(payload);

        Response response = PatientEndpoints.updatePatient(url, payload);
        System.out.println("\nUpdate Response:");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Patient update failed. Expected HTTP 200."
        );

        System.out.println("\n========== FHIR UPDATE RESPONSE VALIDATION ==========");
        FhirValidatorUtil.validate(response.asString());

        Assert.assertEquals(
                response.jsonPath().getString("resourceType"),
                "Patient",
                "Resource type is not Patient"
        );
        Assert.assertEquals(
                response.jsonPath().getString("id"),
                patientId,
                "Updated Patient ID mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("name[0].family"),
                updateData.getLastName(),
                "Updated family name mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("name[0].given[0]"),
                updateData.getFirstName(),
                "Updated first name mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("telecom[0].value"),
                updateData.getPhone(),
                "Updated phone number mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("birthDate"),
                updateData.getBirthDate(),
                "Updated birth date mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("address[0].city"),
                updateData.getCity(),
                "Updated city mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("address[0].state"),
                updateData.getState(),
                "Updated state mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("address[0].postalCode"),
                updateData.getPostalCode(),
                "Updated postal code mismatch"
        );

        context.setAttribute("updatedPatientData", updateData);
        System.out.println("\n========== UPDATE PATIENT VALIDATION PASSED ==========");
    }

    // ============================================================
    // GET UPDATED PATIENT - GET
    // ============================================================

    @Test(dependsOnMethods = "updatePatient")
    public void getUpdatedPatient(ITestContext context) {

        String patientId = (String) context.getAttribute("patientId");
        PatientUpdateData updateData =
                (PatientUpdateData) context.getAttribute("updatedPatientData");

        Assert.assertNotNull(
                patientId,
                "Patient ID was not available after update"
        );
        Assert.assertNotNull(
                updateData,
                "Updated Patient data was not available"
        );

        String url = prop.getProperty("get_url")
                .replace("{patientId}", patientId);
        Response response = PatientEndpoints.getPatient(url);

        System.out.println("========== GET UPDATED PATIENT ==========");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Failed to retrieve updated Patient"
        );
        FhirValidatorUtil.validate(response.asString());
        Assert.assertEquals(
                response.jsonPath().getString("resourceType"),
                "Patient",
                "Resource type is not Patient"
        );
        Assert.assertEquals(
                response.jsonPath().getString("id"),
                patientId,
                "Updated Patient ID mismatch after GET"
        );
        Assert.assertEquals(
                response.jsonPath().getString("name[0].family"),
                updateData.getLastName(),
                "Persisted family name mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("name[0].given[0]"),
                updateData.getFirstName(),
                "Persisted first name mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("telecom[0].value"),
                updateData.getPhone(),
                "Persisted phone number mismatch"
        );
        Assert.assertEquals(
                response.jsonPath().getString("address[0].city"),
                updateData.getCity(),
                "Persisted city mismatch"
        );

        System.out.println("\n========== GET UPDATED PATIENT VALIDATION PASSED ==========");
    }

    // ============================================================
    // UPDATE OBSERVATION - PUT
    // ============================================================

    @Test(dependsOnMethods = "getUpdatedPatient")
    public void updateObservation(ITestContext context) throws IOException {
        String patientId = (String) context.getAttribute("patientId");
        String observationId = (String) context.getAttribute("observationId");
        Assert.assertNotNull(patientId, "Patient ID was not available for Observation update");
        Assert.assertNotNull(observationId, "Observation ID was not available for update");

        ObservationData updateData = ObservationData.update(patientId, observationId);
        String url = prop.getProperty("observation_update_url")
                .replace("{observationId}", observationId);
        String payload = updateData.getPayload();
        FhirValidatorUtil.validate(payload);

        Response response = ObservationEndpoints.updateObservation(url, payload);
        System.out.println("========== UPDATE OBSERVATION ==========");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200,
                "Observation update failed. Expected HTTP 200.");
        FhirValidatorUtil.validate(response.asString());
        Assert.assertEquals(response.jsonPath().getString("resourceType"), "Observation",
                "Resource type is not Observation");
        Assert.assertEquals(response.jsonPath().getString("id"), observationId,
                "Updated Observation ID mismatch");
        Assert.assertEquals(response.jsonPath().getDouble("valueQuantity.value"), updateData.getValue(),
                "Updated Observation value mismatch");
        Assert.assertEquals(response.jsonPath().getString("subject.reference"), "Patient/" + patientId,
                "Updated Observation subject mismatch");
    }

    // ============================================================
    // DELETE OBSERVATION - DELETE
    // ============================================================

    @Test(dependsOnMethods = "updateObservation")
    public void deleteObservation(ITestContext context) {
        String observationId = (String) context.getAttribute("observationId");
        Assert.assertNotNull(observationId, "Observation ID was not available before delete");

        String url = prop.getProperty("observation_delete_url")
                .replace("{observationId}", observationId);
        Response response = ObservationEndpoints.deleteObservation(url);
        System.out.println("========== DELETE OBSERVATION ==========");
        System.out.println("Delete response status: " + response.statusCode());

        Assert.assertTrue(response.statusCode() == 200 || response.statusCode() == 204,
                "Observation deletion failed. Expected HTTP 200 or 204, but received "
                        + response.statusCode());
    }

    // ============================================================
    // VERIFY OBSERVATION DELETION - GET
    // ============================================================

    @Test(dependsOnMethods = "deleteObservation")
    public void verifyObservationDeleted(ITestContext context) {
        String observationId = (String) context.getAttribute("observationId");
        Assert.assertNotNull(observationId, "Deleted Observation ID was not available for verification");

        String url = prop.getProperty("observation_get_url")
                .replace("{observationId}", observationId);
        Response response = ObservationEndpoints.getObservation(url);
        System.out.println("========== VERIFY OBSERVATION DELETION ==========");
        System.out.println("Verification response status: " + response.statusCode());

        Assert.assertTrue(response.statusCode() == 404 || response.statusCode() == 410,
                "Deleted Observation should not be retrievable. Expected HTTP 404 or 410, but received "
                        + response.statusCode());
    }

    // ============================================================
    // DELETE PATIENT - DELETE
    // ============================================================

    @Test(dependsOnMethods = "verifyObservationDeleted")
    public void deletePatient(ITestContext context) {

        String patientId = (String) context.getAttribute("patientId");
        Assert.assertNotNull(
                patientId,
                "Patient ID was not available before delete"
        );

        String url = prop.getProperty("delete_url")
                .replace("{patientId}", patientId);
        Response response = PatientEndpoints.deletePatient(url);

        System.out.println("========== DELETE PATIENT ==========");
        System.out.println("Delete response status: " + response.statusCode());

        Assert.assertTrue(
                response.statusCode() == 200 || response.statusCode() == 204,
                "Patient deletion failed. Expected HTTP 200 or 204, but received "
                        + response.statusCode()
        );

        context.setAttribute("deletedPatientId", patientId);
        System.out.println("\n========== DELETE PATIENT VALIDATION PASSED ==========");
    }

    // ============================================================
    // VERIFY PATIENT DELETION - GET
    // ============================================================

    @Test(dependsOnMethods = "deletePatient")
    public void verifyPatientDeleted(ITestContext context) {

        String patientId = (String) context.getAttribute("deletedPatientId");
        Assert.assertNotNull(
                patientId,
                "Deleted Patient ID was not available for verification"
        );

        String url = prop.getProperty("get_url")
                .replace("{patientId}", patientId);
        Response response = PatientEndpoints.getPatient(url);

        System.out.println("========== VERIFY PATIENT DELETION ==========");
        System.out.println("Verification response status: " + response.statusCode());

        Assert.assertTrue(
                response.statusCode() == 404 || response.statusCode() == 410,
                "Deleted Patient should not be retrievable. Expected HTTP 404 or 410, but received "
                        + response.statusCode()
        );
        System.out.println("\n========== PATIENT DELETION VERIFIED ==========");
    }
}
