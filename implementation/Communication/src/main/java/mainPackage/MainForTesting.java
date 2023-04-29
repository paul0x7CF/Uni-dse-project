package mainPackage;

import broker.InfoMessageCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainForTesting {
    private static final Logger logger = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Integer> ports = new ArrayList<>();

        logger.info("##### Round 1 #####");

        BrokerTestRunner prosumer = new BrokerTestRunner(EServiceType.Prosumer, 5000);
        ports.add(5000);
        BrokerTestRunner exchange = new BrokerTestRunner(EServiceType.Exchange, 6000);
        ports.add(6000);
        executor.execute(prosumer);
        executor.execute(exchange);

        prosumer.sendMessage(sendRegisterForEachPort(ports, prosumer));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }
        logger.info("##### Round 2 #####");

        ports.add(5010);
        BrokerTestRunner prosumer2 = new BrokerTestRunner(EServiceType.Prosumer, 5010);
        executor.execute(prosumer2);
        prosumer2.sendMessage(sendRegisterForEachPort(ports, prosumer2));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }

        logger.info("##### Round 3 #####");

        ports.add(7000);
        BrokerTestRunner solar1 = new BrokerTestRunner(EServiceType.Solar, 7000);
        executor.execute(solar1);
        solar1.sendMessage(sendRegisterForEachPort(ports, solar1));

        ports.add(8000);
        BrokerTestRunner consumption1 = new BrokerTestRunner(EServiceType.Consumption, 8000);
        executor.execute(consumption1);
        consumption1.sendMessage(sendRegisterForEachPort(ports, consumption1));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }

        logger.info("##### Round 4 #####");

        ports.add(9000);
        BrokerTestRunner storage1 = new BrokerTestRunner(EServiceType.Storage, 9000);
        executor.execute(storage1);
        storage1.sendMessage(sendRegisterForEachPort(ports, storage1));
    }

    private static Message registerMessage(BrokerTestRunner broker, int receiverPort) {
        MSData sender = broker.getCurrentService();
        MSData receiver = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "localhost", receiverPort);

        return InfoMessageCreator.createRegisterMessage(sender, receiver);
    }


    private static Message sendRegisterForEachPort(List<Integer> ports, BrokerTestRunner broker) {
        MSData sender = broker.getCurrentService();
        MSData receiver = null;
        for (int port : ports) {
            if (port == broker.getCurrentService().getPort()) {
                continue;
            }
            receiver = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "localhost", port);
            broker.sendMessage(InfoMessageCreator.createRegisterMessage(sender, receiver));
        }
        return InfoMessageCreator.createRegisterMessage(sender, receiver);
    }
}
