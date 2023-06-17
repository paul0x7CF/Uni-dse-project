package CF.mainPackage;

import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used to read the properties file located in the "resources" folder.
 */
public class ConfigReader {
    private static final String PROPERTIES_FILE = "LibraryConfig.properties";
    private final Properties properties;

    public ConfigReader() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
            properties = new Properties();
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}