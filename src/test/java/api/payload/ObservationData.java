package api.payload;

import api.utilities.FakerUtil;

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

    public ObservationData(String patientId) throws IOException {
        buildPayload(patientId, null);
    }

    public ObservationData(String patientId, String observationId)
            throws IOException {
        buildPayload(patientId, observationId);
    }

    private void buildPayload(String patientId, String observationId)
            throws IOException {
        var faker = FakerUtil.getFaker();

        this.patientId = patientId;
        this.status = "final";
        this.codingSystem = "http://loinc.org";
        this.codingCode = "4548-4";
        this.codingDisplay = "Hemoglobin A1c/Hemoglobin.total in Blood";
        this.value = faker.number().numberBetween(40, 99) / 10.0;
        this.unit = "%";
        this.quantitySystem = "http://unitsofmeasure.org";
        this.quantityCode = "%";
        this.effectiveDate = LocalDate.now()
                .minusDays(faker.number().numberBetween(0, 30))
                .toString();
        this.note = faker.lorem().sentence(8);

        Path observationData = Path.of(
                System.getProperty("user.dir"),
                "TestData",
                "observation.json"
        );

        this.payload = Files.readString(observationData);
        this.payload = this.payload.replace("{{patientId}}", this.patientId);
        this.payload = this.payload.replace("{{status}}", this.status);
        this.payload = this.payload.replace("{{codingSystem}}", this.codingSystem);
        this.payload = this.payload.replace("{{codingCode}}", this.codingCode);
        this.payload = this.payload.replace("{{codingDisplay}}", this.codingDisplay);
        this.payload = this.payload.replace("{{value}}", Double.toString(this.value));
        this.payload = this.payload.replace("{{unit}}", this.unit);
        this.payload = this.payload.replace("{{quantitySystem}}", this.quantitySystem);
        this.payload = this.payload.replace("{{quantityCode}}", this.quantityCode);
        this.payload = this.payload.replace("{{effectiveDate}}", this.effectiveDate);
        this.payload = this.payload.replace("{{note}}", this.note);

        if (observationId != null) {
            this.payload = this.payload.replace(
                    "\"resourceType\": \"Observation\"",
                    "\"resourceType\": \"Observation\",\n  \"id\": \""
                            + observationId + "\"");
        }
    }

    public String getPatientId() {
        return patientId;
    }

    public String getStatus() {
        return status;
    }

    public String getCodingSystem() {
        return codingSystem;
    }

    public String getCodingCode() {
        return codingCode;
    }

    public String getCodingDisplay() {
        return codingDisplay;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getQuantitySystem() {
        return quantitySystem;
    }

    public String getQuantityCode() {
        return quantityCode;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getNote() {
        return note;
    }

    public String getPayload() {
        return payload;
    }
}
