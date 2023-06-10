package MSF.communication;

import CF.protocol.Message;
import CF.broker.Broker;
import CF.broker.BrokerRunner;
import MSF.messageHandler.ExchangeMessageHandler;
import MSF.messageHandler.ProsumerMessageHandler;
import MSF.propertyHandler.PropertiesReader;
import exceptions.UnknownMessageException;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import CF.sendable.TimeSlot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class ForecastCommunicationHandler {
    private static final Logger logger = LogManager.getLogger(ForecastCommunicationHandler.class);
    private BrokerRunner communicationBroker;
    private MSData myMSData;
    private EServiceType serviceType;
    private int port;

    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private ForecastMessageHandler forecastMessageHandler;

    public ForecastCommunicationHandler(BlockingQueue<Message> inputQueue, BlockingQueue<Message> outputQueue, int port, EServiceType serviceType) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;

        setUpBroker();
    }

    public void sendMessage(Message message) {

    }

    public void setUpBroker() {
        this.communicationBroker = new BrokerRunner(serviceType, port);
        this.myMSData = this.communicationBroker.getCurrentService();
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
}
