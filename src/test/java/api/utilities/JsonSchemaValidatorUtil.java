package api.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public final class JsonSchemaValidatorUtil {
    private JsonSchemaValidatorUtil() {
    }

    public static void validate(String json, String schemaPath) {
        assertThat(json, matchesJsonSchemaInClasspath(schemaPath));
    }
}
