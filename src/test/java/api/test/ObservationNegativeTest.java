package api.test;

import api.endPoints.ObservationEndpoints;
import api.utilities.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

/** Negative API coverage kept independent from the positive CRUD chain. */
public class ObservationNegativeTest {
    private Properties prop;

    @BeforeClass
    public void setup() throws IOException {
        prop = ConfigReader.initProperties();
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
