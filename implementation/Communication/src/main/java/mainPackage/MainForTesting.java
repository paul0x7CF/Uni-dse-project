package mainPackage;

import broker.BrokerRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.EServiceType;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        final int storageCount = 1;
        final int exchangeCount = 1;
        final int forecastCount = 1;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

        // create prosumer
        for (int i = 0; i < prosumerCount * 10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Prosumer, prosumerPort + i));
        }

        // create storage
        for (int i = 0; i < storageCount * 10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Storage, storagePort + i));
        }

        // create exchange
        for (int i = 0; i < exchangeCount * 10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Exchange, exchangePort + i));
        }

        // create solar forecast
        for (int i = 0; i < forecastCount * 10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Solar, forecastPort + i));
        }
        // create consumption forecast
        for (int i = 5; i < forecastCount * 10; i += 10) {
            executor.execute(new BrokerRunner(EServiceType.Consumption, forecastPort + i));
        }

        sleep(20 * 1000);

        executor.shutdown();
        try {
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                log.info("All threads finished");
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
        }
        List<Runnable> brokers = executor.shutdownNow();
        for (Runnable broker : brokers) {
            if (broker instanceof BrokerRunner) {
                ((BrokerRunner) broker).stop();
            }
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
