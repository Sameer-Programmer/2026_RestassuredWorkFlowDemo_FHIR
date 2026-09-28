package api.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    static Properties prop;

    public static Properties initProperties() throws IOException {

        prop = new Properties();

        String environment = System.getProperty("env", "Dev");

        String filePath = "src/test/resources/Config-"
                + environment
                + "Route.properties";

        FileInputStream fis = new FileInputStream(filePath);

        prop.load(fis);

        return prop;
    }
}