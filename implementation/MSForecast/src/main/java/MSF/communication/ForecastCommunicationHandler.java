package MSF.communication;

import CF.protocol.Message;
import CF.broker.BrokerRunner;
import MSF.communication.messageHandler.ExchangeMessageHandler;
import MSF.communication.messageHandler.ProsumerMessageHandler;
import MSF.communication.messageHandler.ProsumerRequest;
import MSF.exceptions.UnknownMessageException;
import CF.protocol.ECategory;
import CF.sendable.EServiceType;
import CF.sendable.TimeSlot;
import MSF.forecast.MSForecast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class ForecastCommunicationHandler {
    private static final Logger logger = LogManager.getLogger(ForecastCommunicationHandler.class);
    private BrokerRunner communicationBroker;
    private MSForecast msForecast;
    private BlockingQueue<ProsumerRequest> inputQueueProsumerRequest;
    private BlockingQueue<Message> outputQueue;
    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private ForecastMessageHandler forecastMessageHandler;

    public ForecastCommunicationHandler(BlockingQueue<ProsumerRequest> inputQueueProsumerRequest, BlockingQueue<Message> outputQueue, int port, EServiceType serviceType, MSForecast msForecast) {
        this.inputQueueProsumerRequest = inputQueueProsumerRequest;
        this.outputQueue = outputQueue;
        this.msForecast = msForecast;

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
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new ExchangeMessageHandler(msForecast));
                }
                case Forecast -> {
                    this.communicationBroker.addMessageHandler(ECategory.Forecast, new ProsumerMessageHandler(inputQueueProsumerRequest));
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
