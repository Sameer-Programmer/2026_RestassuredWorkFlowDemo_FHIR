package api.utilities;

import com.github.javafaker.Faker;

/** Shared Faker provider used by all dynamic payload builders. */
public final class FakerUtil {

    private static final Faker FAKER = new Faker();

    private FakerUtil() {
        // Utility class
    }

    public static Faker getFaker() {
        return FAKER;
    }
}
