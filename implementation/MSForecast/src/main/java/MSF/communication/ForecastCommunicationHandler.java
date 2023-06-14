package MSF.communication;

import CF.protocol.Message;
import CF.broker.BrokerRunner;
import CF.sendable.ConsumptionResponse;
import CF.sendable.SolarResponse;
import MSF.communication.messageHandler.ExchangeMessageHandler;
import MSF.communication.messageHandler.ProsumerMessageHandler;
import MSF.data.ProsumerRequest;
import MSF.data.ProsumerResponse;
import MSF.exceptions.UnknownMessageException;
import CF.protocol.ECategory;
import CF.sendable.EServiceType;
import CF.sendable.TimeSlot;
import MSF.forecast.MSForecast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ForecastCommunicationHandler {
    private static final Logger logger = LogManager.getLogger(ForecastCommunicationHandler.class);
    private BrokerRunner communicationBroker;
    private MSForecast msForecast;
    private BlockingQueue<ProsumerRequest> inputQueueProsumerRequest;
    //private BlockingQueue<ProsumerResponse> outputQueue;
    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private ForecastMessageBuilder forecastMessageBuilder;

    public ForecastCommunicationHandler(BlockingQueue<ProsumerRequest> inputQueueProsumerRequest, BlockingQueue<TimeSlot> inputQueueTimeSlot, int port, EServiceType serviceType, MSForecast msForecast) {
        this.inputQueueProsumerRequest = inputQueueProsumerRequest;
        this.msForecast = msForecast;
        this.inputQueueTimeSlot = inputQueueTimeSlot;
        setUpBroker(port, serviceType);

        this.forecastMessageBuilder = new ForecastMessageBuilder(this.communicationBroker.getCurrentService());

        logger.info("Broker registered with Id:" +
                this.communicationBroker.getCurrentService().getId() +
                ", Address: " +
                this.communicationBroker.getCurrentService().getAddress() +
                ", Port: " + this.communicationBroker.getCurrentService().getPort());
    }

    public void sendConsumptionResponseMessage(ConsumptionResponse consumptionResponse, String senderAddress, int senderPort, UUID senderID) {
        communicationBroker.sendMessage(this.forecastMessageBuilder.buildConsumptionResponseMessage(consumptionResponse, senderAddress, senderPort, senderID));
    }

    public void sendProductionResponseMessage(SolarResponse solarResponse, String senderAddress, int senderPort, UUID senderID) {
        communicationBroker.sendMessage(this.forecastMessageBuilder.buildSolarResponseMessage(solarResponse, senderAddress, senderPort, senderID));
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
}
