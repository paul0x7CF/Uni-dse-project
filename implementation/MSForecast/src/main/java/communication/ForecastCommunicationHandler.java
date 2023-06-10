package communication;

import broker.BrokerRunner;
import communication.messageHandler.ExchangeMessageHandler;
import communication.messageHandler.ProsumerMessageHandler;
import exceptions.UnknownMessageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import broker.Broker;
import sendable.EServiceType;
import sendable.MSData;
import sendable.TimeSlot;

import java.util.concurrent.BlockingQueue;

public class ForecastCommunicationHandler {
    private static final Logger logger = LogManager.getLogger(ForecastCommunicationHandler.class);
    private MSData myMSData;
    private int port;
    private EServiceType serviceType;
    private BrokerRunner communicationBroker;
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
