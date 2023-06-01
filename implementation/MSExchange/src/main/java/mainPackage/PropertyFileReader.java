package mainPackage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReader {

    public static String getProperty(String key) {
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties");
            properties.load(configFile);
            configFile.close();
            return properties.getProperty("key");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
