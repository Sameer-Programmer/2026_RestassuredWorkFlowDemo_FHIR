package api.payload;

import com.github.javafaker.Faker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class ObservationData {

    private String patientId;
    private String status;
    private String codingSystem;
    private String codingCode;
    private String codingDisplay;
    private double value;
    private String unit;
    private String quantitySystem;
    private String quantityCode;
    private String effectiveDate;
    private String note;
    private String payload;

    public ObservationData(String patientId,
                           String status,
                           String codingSystem,
                           String codingCode,
                           String codingDisplay,
                           double value,
                           String unit,
                           String quantitySystem,
                           String quantityCode,
                           String effectiveDate,
                           String note,
                           String payload) {
        this.patientId = patientId;
        this.status = status;
        this.codingSystem = codingSystem;
        this.codingCode = codingCode;
        this.codingDisplay = codingDisplay;
        this.value = value;
        this.unit = unit;
        this.quantitySystem = quantitySystem;
        this.quantityCode = quantityCode;
        this.effectiveDate = effectiveDate;
        this.note = note;
        this.payload = payload;
    }

    public static ObservationData getObservationPayload(String patientId)
            throws IOException {
        return buildPayload(patientId, null);
    }

    public static ObservationData getObservationUpdatePayload(String patientId,
                                                               String observationId)
            throws IOException {
        return buildPayload(patientId, observationId);
    }

    private static ObservationData buildPayload(String patientId,
                                                String observationId)
            throws IOException {
        Faker faker = new Faker();

        String status = "final";
        String codingSystem = "http://loinc.org";
        String codingCode = "4548-4";
        String codingDisplay = "Hemoglobin A1c/Hemoglobin.total in Blood";
        double value = faker.number().numberBetween(40, 99) / 10.0;
        String unit = "%";
        String quantitySystem = "http://unitsofmeasure.org";
        String quantityCode = "%";
        String effectiveDate = LocalDate.now()
                .minusDays(faker.number().numberBetween(0, 30))
                .toString();
        String note = faker.lorem().sentence(8);

        Path observationData = Path.of(
                System.getProperty("user.dir"),
                "TestData",
                "observation.json"
        );

        String payload = Files.readString(observationData);
        payload = payload.replace("{{patientId}}", patientId);
        payload = payload.replace("{{status}}", status);
        payload = payload.replace("{{codingSystem}}", codingSystem);
        payload = payload.replace("{{codingCode}}", codingCode);
        payload = payload.replace("{{codingDisplay}}", codingDisplay);
        payload = payload.replace("{{value}}", Double.toString(value));
        payload = payload.replace("{{unit}}", unit);
        payload = payload.replace("{{quantitySystem}}", quantitySystem);
        payload = payload.replace("{{quantityCode}}", quantityCode);
        payload = payload.replace("{{effectiveDate}}", effectiveDate);
        payload = payload.replace("{{note}}", note);

        if (observationId != null) {
            payload = payload.replace(
                    "\"resourceType\": \"Observation\"",
                    "\"resourceType\": \"Observation\",\n  \"id\": \""
                            + observationId + "\"");
        }

        return new ObservationData(
                patientId,
                status,
                codingSystem,
                codingCode,
                codingDisplay,
                value,
                unit,
                quantitySystem,
                quantityCode,
                effectiveDate,
                note,
                payload
        );
    }

    public String getPatientId() { return patientId; }
    public String getStatus() { return status; }
    public String getCodingSystem() { return codingSystem; }
    public String getCodingCode() { return codingCode; }
    public String getCodingDisplay() { return codingDisplay; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getQuantitySystem() { return quantitySystem; }
    public String getQuantityCode() { return quantityCode; }
    public String getEffectiveDate() { return effectiveDate; }
    public String getNote() { return note; }
    public String getPayload() { return payload; }
}
