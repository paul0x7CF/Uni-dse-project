package CF.mainPackage;

import CF.protocol.Message;
import CF.broker.BrokerRunner;
import CF.broker.InfoMessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
        int forecastPort = Integer.parseInt(configReader.getProperty("solarPort"));
        int forecastAmount = Integer.parseInt(configReader.getProperty("solarAmount"));
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
        BrokerRunner exchange = null;
        for (int i = 0; i < exchangeAmount * portJumpSize; i += portJumpSize) {
            exchange = new BrokerRunner(EServiceType.Exchange, exchangePort + i);
            executor.execute(exchange);
        }

        // create forecast
        BrokerRunner forecast = null;
        for (int i = 0; i < forecastAmount * portJumpSize; i += portJumpSize) {
            forecast = new BrokerRunner(EServiceType.Forecast, forecastPort + i);
            executor.execute(forecast);
        }

        sleep(15);

        sleep(2);
        log.info("All brokers started");

        for (int i = 0; i < testErrorIterations; ++i) {
            sleep(testErrorTimeout);
            if (i == 1) testErrorTimeout /= 2;
            if (forecast != null && exchange != null) {
                for (int j = 0; j < testErrorAmount; ++j) {
                    Message message = InfoMessageBuilder.createErrorMessage(forecast.getCurrentService(), exchange.getCurrentService(), "test", "test");
                    log.info("Sending error message {}", message.getMessageID());
                    forecast.sendMessage(message);
                }
            } else {
                log.error("forecast or consumption is null");
            }
        }

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
        printServiceSize(forecast);
        exchange.stop();
        sleep(1);
        printServiceSize(forecast);
    }

    private static void sleep(long duration) {
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
        }
    }

    private static void printServiceSize(BrokerRunner broker) {
        log.info("#".repeat(25));
        if (broker != null && broker.getCurrentService() != null) {
            log.info("{} services: {}", broker.getCurrentService().getPort(), broker.getServices().size());
        }
        log.info("#".repeat(25));
    }
}
