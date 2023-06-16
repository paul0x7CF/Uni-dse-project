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

    public String getNumNewTimeSlots() {
        return getProperty("timeslot.numNewTimeSlots");
    }

    public String getMaxNumTimeSlotSaved() {
        return getProperty("timeslot.maxNumTimeSlotSaved");
    }

    public String getDuration() {
        return getProperty("timeslot.duration");
    }

    public String getLoadManagerServiceType() {
        return getProperty("loadManager.serviceType");
    }

    public String getMaxAuctionFindingAlgorithm() {
        return getProperty("prosumer.maxAuctionFindingAlgorithm");
    }

    public String getExchangeServiceType() {
        return getProperty("exchange.serviceType");
    }

    public String getCheckDuration() {
        return getProperty("exchange.checkDuration");
    }

    public String getMinutesToLiveAfterExpiring() {
        return getProperty("exchange.minutesToLiveAfterExpiring");
    }

    public String getCapacity() {
        return getProperty("exchange.capacity");
    }


    public String getCheckInterval() {
        return getProperty("timeSlot.checkInterval");
    }

    public String getK(){
        return getProperty("loadManager.averagePriceK");
    }
}
