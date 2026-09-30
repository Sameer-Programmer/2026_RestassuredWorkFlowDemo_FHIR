package api.endPoints;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ObservationEndpoints {
    public static Response createObservation(String url, String payload) {
        return given()
                .contentType("application/fhir+json")
                .accept("application/fhir+json")
                .body(payload)
                .when()
                .post(url);
    }

    public static Response getObservation(String url) {
        return given()
                .accept("application/fhir+json")
                .when()
                .get(url);
    }

    public static Response updateObservation(String url, String payload) {
        return given()
                .contentType("application/fhir+json")
                .accept("application/fhir+json")
                .body(payload)
                .when()
                .put(url);
    }

    public static Response deleteObservation(String url) {
        return given()
                .accept("application/fhir+json")
                .when()
                .delete(url);
    }
}
