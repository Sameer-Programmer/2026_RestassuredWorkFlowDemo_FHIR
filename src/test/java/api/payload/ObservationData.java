package api.payload;

import java.time.LocalDate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Builds a dynamic FHIR R4 Observation linked to the generated Patient. */
public final class ObservationData {
    private final String patientId;
    private final double value;
    private final String effectiveDate;
    private final String payload;

    private ObservationData(String patientId, double value, String effectiveDate, String payload) {
        this.patientId = patientId;
        this.value = value;
        this.effectiveDate = effectiveDate;
        this.payload = payload;
    }

    public static ObservationData create(String patientId) throws IOException {
        return build(patientId, null, 6.3);
    }

    public static ObservationData update(String patientId, String observationId) throws IOException {
        return build(patientId, observationId, 6.8);
    }

    private static ObservationData build(String patientId, String observationId, double value) throws IOException {
        String effectiveDate = LocalDate.now().toString();
        Path templatePath = Path.of(System.getProperty("user.dir"), "TestData", "observation.json");
        String payload = Files.readString(templatePath)
                .replace("{{value}}", Double.toString(value))
                .replace("{{patientId}}", patientId)
                .replace("{{effectiveDate}}", effectiveDate);
        if (observationId != null) {
            payload = payload.replace("\"resourceType\": \"Observation\"",
                    "\"resourceType\": \"Observation\",\n  \"id\": \"" + observationId + "\"");
        }
        return new ObservationData(patientId, value, effectiveDate, payload);
    }

    public String getPatientId() { return patientId; }
    public double getValue() { return value; }
    public String getEffectiveDate() { return effectiveDate; }
    public String getPayload() { return payload; }
}
