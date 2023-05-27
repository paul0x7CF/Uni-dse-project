package mainPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final String PROPERTIES_FILE = "implementation/Communication/src/main/resources/config.properties";
    private final Properties properties;

    public ConfigReader() {
        try {
            properties = new Properties();
            FileInputStream configFile = new FileInputStream(PROPERTIES_FILE);
            properties.load(configFile);
        } catch (Exception e) {
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}