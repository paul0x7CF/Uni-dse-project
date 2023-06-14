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

    private static final Logger logger = LogManager.getLogger(Communication.class);

    private MSData myMSData;
    private EServiceType serviceType;
    private BrokerRunner communicationBroker;

    private MessageBuilder messageBuilder;

    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private HashMap<UUID, PollConsumptionForecast> pollForecastConsumptionMap = new HashMap<>();
    private HashMap<UUID, PollProductionForecast> pollForecastProductionMap = new HashMap<>();

    private BlockingQueue<Message> incomingMessages;

    private BlockingQueue<Message> outgoingMessages;

    private ProsumerManager prosumerManager;

    private MessageHandler prosMessageHandler;

    public Communication(BlockingQueue<TimeSlot> inputQueueTimeSlot, BlockingQueue<Message> inputForecastResponse, BlockingQueue<Message> outgoingMessages, ProsumerManager prosumerManager) {
        this.inputQueueTimeSlot = inputQueueTimeSlot;
        //this.inputForecastResponse = inputForecastResponse;
        this.outgoingMessages = outgoingMessages;
        this.prosumerManager = prosumerManager;
    }

    public Communication(BlockingQueue<TimeSlot> availableTimeSlot, BlockingQueue<Message> outgoingMessages, final int port, EServiceType serviceType) {
        this.inputQueueTimeSlot = availableTimeSlot;
        this.outgoingMessages = outgoingMessages;
        this.serviceType = serviceType;
        createBroker(port);
        this.messageBuilder = new MessageBuilder(this.myMSData);

        logger.info("BrokerRunner initialized with Id: {} Ip: {} Port: {}", this.myMSData.getId(), this.myMSData.getAddress(), this.myMSData.getPort());
    }

    private void createBroker(final int port) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
        this.myMSData = this.communicationBroker.getCurrentService();
    }

    public void startBrokerRunner() {
        new Thread(this.communicationBroker).start();
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

    public PollConsumptionForecast sendConsumptionRequestMessage(ConsumptionRequest consumptionRequest) {
        this.pollForecastConsumptionMap.put(consumptionRequest.getRequestTimeSlotId(), new PollConsumptionForecast());
        logger.trace("added ConsumptionPoll");

        Optional<Message> messageToSend = Optional.empty();

        // Send message to all services of type Forecast for ConsumptionRequest
        // Only one Response message will be needed because all services of type Forecast will send the same response
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Forecast)) {
            messageToSend= Optional.of(this.messageBuilder.buildConsumptionForecastMessage(consumptionRequest, receiverService));
            communicationBroker.sendMessage(messageToSend.get());
            logger.debug("ConsumptionRequestMessage sent to: Ip:{}, Port: {}", receiverService.getAddress(), receiverService.getPort());
        }

        if(messageToSend.isEmpty()){
            throw new ServiceNotFoundRuntimeException();
        }

        return this.pollForecastConsumptionMap.get(consumptionRequest.getRequestTimeSlotId());
    }

    public void sendProductionRequestMessage(SolarRequest solarRequest) {

        Optional<Message> messageToSend = Optional.empty();

        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Forecast)) {
            messageToSend= Optional.of(this.messageBuilder.buildProductionForecastMessage(solarRequest, receiverService));
            communicationBroker.sendMessage(messageToSend.get());
            logger.debug("ProductionRequestMessage sent to: Ip:{}, Port: {}", receiverService.getAddress(), receiverService.getPort());
        }
        if(messageToSend.isEmpty()){
            throw new ServiceNotFoundRuntimeException();
        }
    }

}


