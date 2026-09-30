package api.payload;

import com.github.javafaker.Faker;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Builds a complete FHIR Observation resource for POST/PUT /Observation.
 *
 * The LOINC coding is intentionally fixed because it is controlled terminology;
 * Faker is used for the variable observation value, date, and clinical note.
 */
public class ObservationData {
    private final String patientId;
    private final String observationId;
    private final String status;
    private final String codingSystem;
    private final String codingCode;
    private final String codingDisplay;
    private final double value;
    private final String unit;
    private final String quantitySystem;
    private final String quantityCode;
    private final String effectiveDate;
    private final String note;
    private final String payload;

    private ObservationData(String patientId, String observationId, String status,
                            String codingSystem, String codingCode, String codingDisplay,
                            double value, String unit, String quantitySystem,
                            String quantityCode, String effectiveDate, String note,
                            String payload) {
        this.patientId = patientId;
        this.observationId = observationId;
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

    public static ObservationData create(String patientId) throws IOException {
        return build(patientId, null);
    }

    public static ObservationData update(String patientId, String observationId) throws IOException {
        return build(patientId, observationId);
    }

    private static ObservationData build(String patientId, String observationId) throws IOException {
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
                .minusDays(faker.number().numberBetween(0, 31))
                .toString();
        String note = faker.lorem().sentence(8);

        Path templatePath = Path.of(System.getProperty("user.dir"), "TestData", "observation.json");
        String payload = Files.readString(templatePath)
                .replace("{{value}}", Double.toString(value))
                .replace("{{patientId}}", patientId)
                .replace("{{effectiveDate}}", effectiveDate)
                .replace("{{note}}", JSONObject.quote(note));

        if (observationId != null) {
            JSONObject observation = new JSONObject(payload);
            observation.put("id", observationId);
            payload = observation.toString();
        }

        return new ObservationData(
                patientId, observationId, status, codingSystem, codingCode, codingDisplay,
                value, unit, quantitySystem, quantityCode, effectiveDate, note, payload
        );
    }

    public String getPatientId() { return patientId; }
    public String getObservationId() { return observationId; }
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
