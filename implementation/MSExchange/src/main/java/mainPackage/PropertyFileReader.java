package mainPackage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReader {

    private static String getProperty(String key) {
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties");
            properties.load(configFile);
            configFile.close();
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNumNewTimeSlots() {
        return getProperty("timeslot.numNewTimeSlots");
    }

    public static String getMaxNumTimeSlotSaved() {
        return getProperty("timeslot.maxNumTimeSlotSaved");
    }

    public static String getDuration() {
        return getProperty("timeslot.duration");
    }

    public static String getLoadManagerServiceType() {
        return getProperty("loadManager.serviceType");
    }

    public static String getLoadManagerPort() {
        return getProperty("loadManager.port");
    }

    public static String getMaxAuctionFindingAlgorithm() {
        return getProperty("prosumer.maxAuctionFindingAlgorithm");
    }

    public static String getExchangePort() {
        return getProperty("exchange.port");
    }

    public static String getExchangeServiceType() {
        return getProperty("exchange.serviceType");
    }

    public static String getCheckDuration() {
        return getProperty("exchange.checkDuration");
    }

    public static String getMinutesToLiveAfterExpiring() {
        return getProperty("exchange.minutesToLiveAfterExpiring");
    }

    public static String getCapacity() {
        return getProperty("exchange.capacity");
    }


}
