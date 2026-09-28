package api.endPoints;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PatientEndpoints {
    // Create Patient
    public static Response createPatient(String url, String payload) {

        return given()
                .contentType("application/fhir+json")
                .body(payload)
                .when()
                .post(url);
    }
    // Get Patient
    public static Response getPatient(String url) {

        return given()
                .accept("application/fhir+json")
                .when()
                .get(url);
    }

    // Update Patient
    public static Response updatePatient(String url, String payload) {

        return given()
                .contentType("application/fhir+json")
                .accept("application/fhir+json")
                .body(payload)
                .when()
                .put(url);
    }
}
