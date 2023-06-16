package MSS.communication;

import CF.broker.BrokerRunner;
import CF.protocol.ECategory;
import CF.sendable.*;
import MSS.exceptions.UnknownMessageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class Communication {

    // Define the logger

    private static final Logger logger = LogManager.getLogger(Communication.class);

    // Define the private fields

    private MSData myMSData;
    private EServiceType serviceType;
    private BrokerRunner communicationBroker;
    private BlockingQueue<Transaction> incomingTransactionQueue;

    // Define the constructor

    public Communication(BlockingQueue<Transaction> incomingTransactionQueue, final int port, EServiceType serviceType) {
        this.incomingTransactionQueue = incomingTransactionQueue;
        this.serviceType = serviceType;
        createBroker(port);

        logger.info("BrokerRunner initialized with Ip: {} Port: {}", this.myMSData.getAddress(), this.myMSData.getPort());
    }

    // Define the initialization methods

    private void createBroker(final int port) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
        this.myMSData = this.communicationBroker.getCurrentService();
    }

    public void startBrokerRunner(String threadName) {
        new Thread(this.communicationBroker, "Com-of-" + threadName).start();
    }

    public void addMessageHandler(ECategory category) {
        try {
            switch (category) {
                case Exchange -> {
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new ExchangeMessageHandler(myMSData, this.incomingTransactionQueue));
                }
                default -> {
                    throw new UnknownMessageException();
                }
            }
        } catch (UnknownMessageException e) {
            logger.warn(e.getMessage());
        }
    }

}


