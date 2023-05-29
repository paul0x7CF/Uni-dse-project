package mainPackage;

import broker.BrokerRunner;
import broker.InfoMessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainForTesting {
    private static final Logger log = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        ConfigReader configReader = new ConfigReader();
        int testErrorIterations = Integer.parseInt(configReader.getProperty("testErrorIterations"));
        int testErrorAmount = Integer.parseInt(configReader.getProperty("testErrorAmount"));
        int testErrorTimeout = Integer.parseInt(configReader.getProperty("testErrorTimeout"));
        int portJumpSize = Integer.parseInt(configReader.getProperty("portJumpSize"));
        int prosumerPort = Integer.parseInt(configReader.getProperty("prosumerPort"));
        int prosumerAmount = Integer.parseInt(configReader.getProperty("prosumerAmount"));
        int storagePort = Integer.parseInt(configReader.getProperty("storagePort"));
        int storageAmount = Integer.parseInt(configReader.getProperty("storageAmount"));
        int exchangePort = Integer.parseInt(configReader.getProperty("exchangePort"));
        int exchangeAmount = Integer.parseInt(configReader.getProperty("exchangeAmount"));
        int solarPort = Integer.parseInt(configReader.getProperty("solarPort"));
        int solarAmount = Integer.parseInt(configReader.getProperty("solarAmount"));
        int consumptionPort = Integer.parseInt(configReader.getProperty("consumptionPort"));
        int consumptionAmount = Integer.parseInt(configReader.getProperty("consumptionAmount"));

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

        // create prosumer
        for (int i = 0; i < prosumerAmount * portJumpSize; i += portJumpSize) {
            executor.execute(new BrokerRunner(EServiceType.Prosumer, prosumerPort + i));
        }

        // create storage
        for (int i = 0; i < storageAmount * portJumpSize; i += portJumpSize) {
            executor.execute(new BrokerRunner(EServiceType.Storage, storagePort + i));
        }

        // create exchange
        for (int i = 0; i < exchangeAmount * portJumpSize; i += portJumpSize) {
            executor.execute(new BrokerRunner(EServiceType.Exchange, exchangePort + i));
        }

        // create solar forecast
        BrokerRunner solar = null;
        for (int i = 0; i < solarAmount * portJumpSize; i += portJumpSize) {
            solar = new BrokerRunner(EServiceType.Solar, solarPort + i);
            executor.execute(solar);
        }

        // create consumption forecast
        BrokerRunner consumption = null;
        for (int i = 0; i < consumptionAmount * portJumpSize; i += portJumpSize) {
            consumption = new BrokerRunner(EServiceType.Consumption, consumptionPort + i);
            executor.execute(consumption);
        }

        sleep(2);
        log.info("All brokers started");

        for (int i = 0; i < testErrorIterations; ++i) {
            sleep(testErrorTimeout);
            if (i == 1) testErrorTimeout /= 2;
            if (solar != null && consumption != null) {
                for (int j = 0; j < testErrorAmount; ++j) {
                    Message message = InfoMessageBuilder.createErrorMessage(solar.getCurrentService(), consumption.getCurrentService(), "test", "test");
                    log.info("Sending message {}", message.getMessageID());
                    solar.sendMessage(message);
                }
            } else {
                log.error("solar or consumption is null");
            }
        }

        log.info("#".repeat(25));
        log.info("solar services: {}", solar.getServices().size());
        log.info("#".repeat(25));

        sleep(5);
/*
        List<Runnable> brokers = executor.shutdownNow();
        for (Runnable broker : brokers) {
            if (broker instanceof BrokerRunner b) {
                // TODO: reimplement
                b.stop();
            }
        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                log.info("All threads finished");
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
        }

         */
        log.fatal("All brokers stopped");
        log.info("#".repeat(25));
        log.info("solar services: {}", solar.getServices().size());
        log.info("#".repeat(25));
        sleep(3);
    }

    private static void sleep(long duration) {
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
        }
    }
}
