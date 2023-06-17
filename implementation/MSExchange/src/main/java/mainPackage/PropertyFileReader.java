package mainPackage;

import loadManager.prosumerActionManagement.priceCalculationStrategy.IPriceMechanism;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;


public class PropertyFileReader {

    private static String getProperty(String key) {
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("implementation/MSExchange/src/main/resources/config.properties");
            properties.load(configFile);
            configFile.close();
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String getK() {
        return getProperty("loadManager.averagePriceK");
    }

    public IPriceMechanism getPriceMechanism() {
        try {
            Class<?> clazz = Class.forName(getProperty("loadManager.priceMechanism"));
            Constructor<?> constructor = clazz.getDeclaredConstructor();

            return (IPriceMechanism) constructor.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public double getTerminateMinutes() {
        return Double.parseDouble(getProperty("exchange.terminateMinutes"));
    }
}
