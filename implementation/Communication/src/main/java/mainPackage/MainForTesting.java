package mainPackage;

import broker.InfoMessageCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainForTesting {
    private static final Logger logger = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        BrokerTestRunner prosumer = new BrokerTestRunner(EServiceType.Prosumer, 5000);
        BrokerTestRunner exchange = new BrokerTestRunner(EServiceType.Exchange, 6000);
        executor.execute(prosumer);
        executor.execute(exchange);

        prosumer.getBroker().sendMessage(registerMessage(prosumer, 6000));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }
        logger.info("Starting more prosumer");
        BrokerTestRunner prosumer2 = new BrokerTestRunner(EServiceType.Prosumer, 5010);
        executor.execute(prosumer2);

        prosumer2.getBroker().sendMessage(registerMessage(prosumer2, 5000));
        prosumer2.getBroker().sendMessage(registerMessage(prosumer2, 6000));


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        }
    }

    private static Message registerMessage(BrokerTestRunner broker, int receiverPort) {
        MSData sender = broker.getCurrentService();
        MSData receiver = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "localhost", receiverPort);

        return InfoMessageCreator.createRegisterMessage(sender, receiver);
    }
}
