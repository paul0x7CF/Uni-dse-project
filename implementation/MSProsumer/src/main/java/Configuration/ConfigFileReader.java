package Configuration;

import Exceptions.ConfigFileReaderRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class ConfigFileReader {

    private static final Logger logger = LogManager.getLogger(ConfigFileReader.class);

    private static final String CONFIG_FILE_PATH = "implementation/MSProsumer/src/main/resources/config.properties";

    public static String getProperty(final String KEY) {
        Optional<String> result;
        try (FileInputStream configFile = new FileInputStream(CONFIG_FILE_PATH)) {

            Properties properties = new Properties();
            properties.load(configFile);

            result = Optional.ofNullable((properties.getProperty(KEY)));

            if (result.isPresent()) {
                return result.get();
            } else {
                throw new ConfigFileReaderRuntimeException();
            }


        } catch (FileNotFoundException e) {
            logger.error("Config file not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error while reading config file");
            throw new RuntimeException(e);
        }

    }
}
