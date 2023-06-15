package MSP.Communication;

import CF.sendable.*;
import MSP.Communication.MessageHandling.MessageBuilder;
import MSP.Communication.polling.PollConsumptionForecast;
import MSP.Communication.polling.PollProductionForecast;
import MSP.Exceptions.ServiceNotFoundRuntimeException;
import MSP.Exceptions.UnknownMessageException;
import MSP.Communication.MessageHandling.AuctionMessageHandler;
import MSP.Communication.MessageHandling.ExchangeMessageHandler;
import MSP.Communication.MessageHandling.ForecastMessageHandler;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import MSP.Main.ProsumerManager;
import CF.broker.BrokerRunner;
import CF.messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.ECategory;
import CF.protocol.Message;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Communication {

    // Define the logger

    private static final Logger logger = LogManager.getLogger(Communication.class);

    // Define the private fields

    private MSData myMSData;
    private EServiceType serviceType;
    private BrokerRunner communicationBroker;
    private MessageBuilder messageBuilder;
    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private HashMap<UUID, PollConsumptionForecast> pollForecastConsumptionMap = new HashMap<>();
    private HashMap<UUID, PollProductionForecast> pollForecastProductionMap = new HashMap<>();

    // Define the constructor

    public Communication(BlockingQueue<TimeSlot> availableTimeSlot, final int port, EServiceType serviceType) {
        this.inputQueueTimeSlot = availableTimeSlot;
        this.serviceType = serviceType;
        createBroker(port);
        this.messageBuilder = new MessageBuilder(this.myMSData);

        logger.info("BrokerRunner initialized with Ip: {} Port: {}", this.myMSData.getAddress(), this.myMSData.getPort());
    }

    // Define the initialization methods

    private void createBroker(final int port) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
        this.myMSData = this.communicationBroker.getCurrentService();
    }

    public void startBrokerRunner(String threadName) {
        new Thread(this.communicationBroker,"Com-of-"+threadName).start();
    }

    public void addMessageHandler(ECategory category) {
        try {
            switch (category) {
                case Auction -> {
                    this.communicationBroker.addMessageHandler(ECategory.Auction, new AuctionMessageHandler());
                }
                case Exchange -> {
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new ExchangeMessageHandler(inputQueueTimeSlot));
                }
                case Forecast -> {
                    this.communicationBroker.addMessageHandler(ECategory.Forecast, new ForecastMessageHandler(this.pollForecastConsumptionMap, this.pollForecastProductionMap));
                }
                default -> {
                    throw new UnknownMessageException();
                }
            }
        } catch (UnknownMessageException e) {
            logger.warn(e.getMessage());
        }
    }

    // Define the methods for sending messages

    public PollConsumptionForecast sendConsumptionRequestMessage(ConsumptionRequest consumptionRequest) {
        logger.debug("Executing Send Consumption Request Message");
        this.pollForecastConsumptionMap.put(consumptionRequest.getRequestTimeSlotId(), new PollConsumptionForecast());
        logger.trace("added ConsumptionPoll");

        Optional<Message> messageToSend = Optional.empty();

        // Send message to all services of type Forecast for ConsumptionRequest
        // Only one Response message will be needed because all services of type Forecast will send the same response
        int countSending = 0;
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Forecast)) {
            messageToSend= Optional.of(this.messageBuilder.buildConsumptionForecastMessage(consumptionRequest, receiverService));
            communicationBroker.sendMessage(messageToSend.get());
            logger.trace("ConsumptionRequestMessage sent to: Ip:{}, Port: {}", receiverService.getAddress(), receiverService.getPort());
            countSending++;
        }
        logger.debug("ConsumptionRequestMessage was sent to {} Forecast services", countSending);

        if(messageToSend.isEmpty()){
            throw new ServiceNotFoundRuntimeException();
        }

        return this.pollForecastConsumptionMap.get(consumptionRequest.getRequestTimeSlotId());
    }

    public PollProductionForecast sendProductionRequestMessage(SolarRequest solarRequest) {
        logger.debug("Executing Send Production Request Message");
        this.pollForecastProductionMap.put(solarRequest.getRequestTimeSlotId(), new PollProductionForecast());
        logger.trace("added ProductionPoll");
        Optional<Message> messageToSend = Optional.empty();

        int countSending = 0;
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Forecast)) {
            messageToSend= Optional.of(this.messageBuilder.buildProductionForecastMessage(solarRequest, receiverService));
            communicationBroker.sendMessage(messageToSend.get());
            logger.trace("ProductionRequestMessage sent to: Ip:{}, Port: {}", receiverService.getAddress(), receiverService.getPort());
            countSending++;
        }
        logger.debug("ConsumptionRequestMessage was sent to {} Forecast services", countSending);
        if(messageToSend.isEmpty()){
            throw new ServiceNotFoundRuntimeException();
        }
        return this.pollForecastProductionMap.get(solarRequest.getRequestTimeSlotId());
    }

}


