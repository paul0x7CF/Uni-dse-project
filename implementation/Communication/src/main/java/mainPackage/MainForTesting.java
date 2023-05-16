package mainPackage;

import broker.BrokerRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.EServiceType;
import sendable.MSData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static broker.InfoMessageBuilder.createRegisterMessage;

public class MainForTesting {
    private static final Logger log = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        ConfigReader configReader = new ConfigReader();
        String vpnbroadcastAddress = configReader.getProperty("vpnbroadcastAddress");
        int prosumerPort = Integer.parseInt(configReader.getProperty("prosumerPort"));
        int storagePort = Integer.parseInt(configReader.getProperty("storagePort"));
        int exchangePort = Integer.parseInt(configReader.getProperty("exchangePort"));
        int forecastPort = Integer.parseInt(configReader.getProperty("forecastPort"));

        final int prosumerCount = 1;
        final int storageCount  = 1;
        final int exchangeCount = 0;
        final int forecastCount = 0;

        ExecutorService executor = Executors.newFixedThreadPool(100);

        // create prosumer
        for (int i = 0; i < prosumerCount*10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Prosumer, prosumerPort + i));
        }

        // create storage
        for (int i = 0; i < storageCount*10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Storage, storagePort + i));
        }

        // create exchange
        for (int i = 0; i < exchangeCount*10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Exchange, exchangePort + i));
        }

        // create solar forecast
        for (int i = 0; i < forecastCount*10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Solar, forecastPort + i));
        }
        // create consumption forecast
        for (int i = 5; i < forecastCount*10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Consumption, forecastPort + i));
        }
    }

    private static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
        }
    }
}
