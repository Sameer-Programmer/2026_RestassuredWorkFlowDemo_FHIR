package api.utilities;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;

public class FhirValidatorUtil {

    private static final FhirContext FHIR_CONTEXT =
            FhirContext.forR4();

    public static void validate(String json) {

        FhirValidator validator =
                FHIR_CONTEXT.newValidator();

        ValidationResult result =
                validator.validateWithResult(json);

        if (!result.isSuccessful()) {

            System.out.println("FHIR Validation Failed:");

            result.getMessages().forEach(message ->
                    System.out.println(
                            message.getSeverity() + " : "
                                    + message.getLocationString()
                                    + " : "
                                    + message.getMessage()
                    )
            );

            throw new AssertionError(
                    "FHIR validation failed"
            );
        }

        System.out.println("FHIR Validation Passed");
    }
}