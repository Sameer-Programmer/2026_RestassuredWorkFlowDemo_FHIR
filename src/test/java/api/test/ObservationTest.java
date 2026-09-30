package api.test;

import api.endPoints.ObservationEndpoints;
import api.payload.ObservationData;
import api.utilities.ConfigReader;
import api.utilities.FhirValidatorUtil;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

public class ObservationTest {
    private Properties prop;

    @BeforeClass
    public void setup() throws IOException {
        prop = ConfigReader.initProperties();
    }

    @Test(dependsOnGroups = "patient-ready")
    public void createObservation(ITestContext context) throws IOException {
        String patientId = (String) context.getAttribute("patientId");
        Assert.assertNotNull(patientId, "Patient ID was not available for Observation creation");

        ObservationData observationData = new ObservationData(patientId);
        String payload = observationData.getPayload();
        FhirValidatorUtil.validate(payload);

        Response response = ObservationEndpoints.createObservation(
                prop.getProperty("observation_post_url"), payload);
        System.out.println("========== CREATE OBSERVATION ==========");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 201,
                "Observation creation failed. Expected HTTP 201.");
        FhirValidatorUtil.validate(response.asString());
        Assert.assertEquals(response.jsonPath().getString("resourceType"), "Observation",
                "Resource type is not Observation");
        Assert.assertEquals(response.jsonPath().getString("status"), observationData.getStatus(),
                "Observation status mismatch");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].system"), observationData.getCodingSystem(),
                "Observation coding system mismatch");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].code"), observationData.getCodingCode(),
                "Observation LOINC code mismatch");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].display"), observationData.getCodingDisplay(),
                "Observation coding display mismatch");
        Assert.assertEquals(response.jsonPath().getDouble("valueQuantity.value"), observationData.getValue(),
                "Observation value mismatch");
        Assert.assertEquals(response.jsonPath().getString("valueQuantity.unit"), observationData.getUnit(),
                "Observation unit mismatch");
        Assert.assertEquals(response.jsonPath().getString("valueQuantity.system"), observationData.getQuantitySystem(),
                "Observation quantity system mismatch");
        Assert.assertEquals(response.jsonPath().getString("valueQuantity.code"), observationData.getQuantityCode(),
                "Observation quantity code mismatch");
        Assert.assertEquals(response.jsonPath().getString("subject.reference"), "Patient/" + patientId,
                "Observation subject mismatch");
        Assert.assertEquals(response.jsonPath().getString("effectiveDateTime"), observationData.getEffectiveDate(),
                "Observation effective date mismatch");
        Assert.assertEquals(response.jsonPath().getString("note[0].text"), observationData.getNote(),
                "Observation note mismatch");

        String observationId = response.jsonPath().getString("id");
        Assert.assertNotNull(observationId, "Observation ID should not be null");
        Assert.assertFalse(observationId.isBlank(), "Observation ID should not be empty");
        context.setAttribute("observationId", observationId);
        context.setAttribute("observationData", observationData);
    }

    @Test(dependsOnMethods = "createObservation", groups = "observation-ready")
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
        ObservationData observationData = (ObservationData) context.getAttribute("observationData");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].display"), observationData.getCodingDisplay(),
                "Observation coding display mismatch after GET");
        Assert.assertEquals(response.jsonPath().getDouble("valueQuantity.value"), observationData.getValue(),
                "Observation value mismatch after GET");
    }

    @Test(dependsOnGroups = "patient-updated-read")
    public void updateObservation(ITestContext context) throws IOException {
        String patientId = (String) context.getAttribute("patientId");
        String observationId = (String) context.getAttribute("observationId");
        Assert.assertNotNull(patientId, "Patient ID was not available for Observation update");
        Assert.assertNotNull(observationId, "Observation ID was not available for update");

        ObservationData updateData = new ObservationData(patientId, observationId);
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
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].system"), updateData.getCodingSystem(),
                "Updated Observation coding system mismatch");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].code"), updateData.getCodingCode(),
                "Updated Observation LOINC code mismatch");
        Assert.assertEquals(response.jsonPath().getString("code.coding[0].display"), updateData.getCodingDisplay(),
                "Updated Observation coding display mismatch");
        Assert.assertEquals(response.jsonPath().getDouble("valueQuantity.value"), updateData.getValue(),
                "Updated Observation value mismatch");
        Assert.assertEquals(response.jsonPath().getString("valueQuantity.unit"), updateData.getUnit(),
                "Updated Observation unit mismatch");
        Assert.assertEquals(response.jsonPath().getString("subject.reference"), "Patient/" + patientId,
                "Updated Observation subject mismatch");
        Assert.assertEquals(response.jsonPath().getString("effectiveDateTime"), updateData.getEffectiveDate(),
                "Updated Observation effective date mismatch");
        Assert.assertEquals(response.jsonPath().getString("note[0].text"), updateData.getNote(),
                "Updated Observation note mismatch");
        context.setAttribute("updatedObservationData", updateData);
    }

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

    @Test(dependsOnMethods = "deleteObservation", groups = "observation-deleted")
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

    @Test
    public void createObservationWithInvalidResourceType() {
        String payload = "{\"resourceType\":\"NotAnObservation\",\"status\":\"final\"}";
        Response response = ObservationEndpoints.createObservation(
                prop.getProperty("observation_post_url"), payload);

        Assert.assertEquals(response.statusCode(), 400,
                "Invalid Observation should be rejected with HTTP 400");
    }

    @Test
    public void getObservationWithUnknownId() {
        Response response = ObservationEndpoints.getObservation(
                prop.getProperty("observation_get_url").replace("{observationId}", "observation-does-not-exist"));

        Assert.assertTrue(response.statusCode() == 404 || response.statusCode() == 410,
                "Unknown Observation should return HTTP 404 or 410, but received " + response.statusCode());
    }

    @Test
    public void updateObservationWithInvalidPayload() {
        String payload = "{\"resourceType\":\"Observation\",\"status\":\"invalid-status\"}";
        Response response = ObservationEndpoints.updateObservation(
                prop.getProperty("observation_update_url").replace("{observationId}", "observation-does-not-exist"),
                payload);

        Assert.assertTrue(response.statusCode() == 400 || response.statusCode() == 404 || response.statusCode() == 410,
                "Invalid update should return HTTP 400, 404, or 410, but received " + response.statusCode());
    }

    @Test
    public void deleteObservationWithUnknownId() {
        Response response = ObservationEndpoints.deleteObservation(
                prop.getProperty("observation_delete_url").replace("{observationId}", "observation-does-not-exist"));

        Assert.assertTrue(response.statusCode() == 200 || response.statusCode() == 204
                        || response.statusCode() == 404 || response.statusCode() == 410,
                "Unknown Observation delete should be idempotent (200/204) or return 404/410, but received "
                        + response.statusCode());
    }
}
