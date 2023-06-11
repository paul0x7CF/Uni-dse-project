package MSF.communication;

import CF.exceptions.MessageProcessingException;
import CF.protocol.Message;
import CF.broker.BrokerRunner;
import MSF.communication.messageHandler.ExchangeMessageHandler;
import MSF.communication.messageHandler.ProsumerMessageHandler;
import MSF.exceptions.UnknownMessageException;
import CF.protocol.ECategory;
import CF.sendable.EServiceType;
import CF.sendable.TimeSlot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class ForecastCommunicationHandler {
    private static final Logger logger = LogManager.getLogger(ForecastCommunicationHandler.class);
    private BrokerRunner communicationBroker;
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private ForecastMessageHandler forecastMessageHandler;

    public ForecastCommunicationHandler(BlockingQueue<Message> inputQueue, BlockingQueue<Message> outputQueue, int port, EServiceType serviceType) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;

        setUpBroker(port, serviceType);

        logger.info("Broker registered with Id:" +
                this.communicationBroker.getCurrentService().getId() +
                ", Address: " +
                this.communicationBroker.getCurrentService().getAddress() +
                ", Port: " + this.communicationBroker.getCurrentService().getPort());
    }

    public void sendMessage(Message message) {
        communicationBroker.sendMessage(message);
    }

    public void setUpBroker(int port, EServiceType serviceType) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
    }

    public void startBrokerRunner() {
        this.communicationBroker.run();
    }

    public void addMessageHandler(ECategory category) {
        try {
            switch (category) {
                case Exchange -> {
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new ExchangeMessageHandler(inputQueueTimeSlot));
                }
                case Forecast -> {
                    this.communicationBroker.addMessageHandler(ECategory.Forecast, new ProsumerMessageHandler());
                }
                default -> {
                    throw new UnknownMessageException();
                }
            }
        } catch (UnknownMessageException e) {
            logger.warn(e.getMessage());
        }
    }

    public BrokerRunner getBroker() {
        return communicationBroker;
    }
}
