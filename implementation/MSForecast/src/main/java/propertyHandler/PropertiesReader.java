package propertyHandler;

import exceptions.ProperiyFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Reads the properties from the property file.
 */
public class PropertiesReader {
    private static final Logger logger = LogManager.getLogger(PropertiesReader.class);
    private static final String CONFIG_FILE_PATH = "implementation/MSForecast/src/main/resources/config.properties";
    private static final String CONFIG_FILE_PATH_FROM_COMMUNICATION = "implementation/Communication/src/main/resources/LibraryConfig.properties";

    public static String readProperties(final String KEY, final String PATH) {
        Optional<String> result;

        try (FileInputStream propertyFile = new FileInputStream(PATH)) {

            Properties properties = new Properties();
            properties.load(propertyFile);

            result = Optional.ofNullable((properties.getProperty(KEY)));

            if (result.isPresent()) {
                return result.get();
            } else {
                throw new ProperiyFileException("Properties empty");
            }

        } catch (FileNotFoundException e) {
            logger.error("Config file not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error while reading config file");
            throw new RuntimeException(e);
        }
    }

    public static String getCommunicationProperty(final String KEY) {
        return readProperties(KEY, CONFIG_FILE_PATH_FROM_COMMUNICATION);
    }
    public static String getProperty(final String KEY) {
        return readProperties(KEY, CONFIG_FILE_PATH);
    }
}
