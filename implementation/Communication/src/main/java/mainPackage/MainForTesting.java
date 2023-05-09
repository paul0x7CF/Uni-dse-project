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

import static broker.InfoMessageCreator.createRegisterMessage;

public class MainForTesting {
    private static final Logger logger = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Integer> ports = new ArrayList<>();

        logger.info("##### Round 1 #####");

        BrokerRunner prosumer = new BrokerRunner(EServiceType.Prosumer, 5000);
        ports.add(5000);
        BrokerRunner exchange = new BrokerRunner(EServiceType.Exchange, 6000);
        ports.add(6000);
        executor.execute(prosumer);
        executor.execute(exchange);

        //MainForTesting.sendRegisterForEachPort(ports, prosumer);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }
        logger.info("##### Round 2 #####");

        ports.add(5010);
        BrokerRunner prosumer2 = new BrokerRunner(EServiceType.Prosumer, 5010);
        executor.execute(prosumer2);
        //MainForTesting.sendRegisterForEachPort(ports, prosumer2);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }

        logger.info("##### Round 3 #####");

        ports.add(7000);
        BrokerRunner solar1 = new BrokerRunner(EServiceType.Solar, 7000);
        executor.execute(solar1);
        //MainForTesting.sendRegisterForEachPort(ports, solar1);

        ports.add(8000);
        BrokerRunner consumption1 = new BrokerRunner(EServiceType.Consumption, 8000);
        executor.execute(consumption1);
        //MainForTesting.sendRegisterForEachPort(ports, consumption1);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }

        logger.info("##### Round 4 #####");

        ports.add(9000);
        BrokerRunner storage1 = new BrokerRunner(EServiceType.Storage, 9000);
        executor.execute(storage1);
        //sendRegisterForEachPort(ports, storage1);
    }

    private static void sendRegisterMessage(BrokerRunner broker, int receiverPort) {
        MSData sender = broker.getCurrentService();
        MSData receiver = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "localhost", receiverPort);

        broker.sendMessage(createRegisterMessage(sender, receiver));
    }


    private static void sendRegisterForEachPort(List<Integer> ports, BrokerRunner broker) {
        final MSData sender = broker.getCurrentService();
        for (int port : ports) {
            if (port == broker.getCurrentService().getPort()) {
                continue;
            }
            final MSData receiver = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "localhost", port);
            broker.sendMessage(createRegisterMessage(sender, receiver));
        }
    }
}
