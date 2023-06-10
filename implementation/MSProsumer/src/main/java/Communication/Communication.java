package Communication;

import Communication.MessageHandling.MessageBuilder;
import Data.Producer;
import Exceptions.ServiceNotFoundRuntimeException;
import Exceptions.UnknownMessageException;
import Communication.MessageHandling.AuctionMessageHandler;
import Communication.MessageHandling.ExchangeMessageHandler;
import Communication.MessageHandling.ForecastMessageHandler;
import Exceptions.UnsupportedSendingObjectException;
import Logic.Prosumer.Prosumer;
import MSProsumer.Main.ProsumerManager;
import broker.BrokerRunner;
import messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Communication {

    private static final Logger logger = LogManager.getLogger(Communication.class);

    private MSData myMSData;
    private EServiceType serviceType;
    private BrokerRunner communicationBroker;
    private Prosumer myProsumer;

    private MessageBuilder messageBuilder;

    private BlockingQueue<TimeSlot> inputQueueTimeSlot;
    private HashMap<UUID, PollForecast> pollForecastConsumptionMap = new HashMap<>();
    private HashMap<UUID, PollForecast> pollForecastProductionMap = new HashMap<>();

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

    public Communication(BlockingQueue<TimeSlot> availableTimeSlot, BlockingQueue<Message> outgoingMessages, final int port, Prosumer myProsumer, EServiceType serviceType) {
        this.inputQueueTimeSlot = availableTimeSlot;
        this.outgoingMessages = outgoingMessages;
        this.myProsumer = myProsumer;
        this.serviceType = serviceType;
        this.messageBuilder = new MessageBuilder();
        createBroker(port);

        logger.info("BrokerRunner initialized with Id: {} Ip: {} Port: {}", this.myMSData.getId(), this.myMSData.getAddress(), this.myMSData.getPort());
    }

    private void createBroker(final int port) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
        this.myMSData = this.communicationBroker.getCurrentService();
    }

    public void startBrokerRunner() {
        this.communicationBroker.run();
    }

    public void addMessageHandler(ECategory category) {
        try {
            switch (category) {
                case Auction -> {
                    this.communicationBroker.addMessageHandler(ECategory.Auction, new AuctionMessageHandler());
                }
                case Exchange -> {
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new ExchangeMessageHandler(myProsumer, inputQueueTimeSlot));
                }
                case Forecast -> {
                    this.communicationBroker.addMessageHandler(ECategory.Forecast, new ForecastMessageHandler(myProsumer));
                }
                default -> {
                    throw new UnknownMessageException();
                }
            }
        } catch (UnknownMessageException e) {
            logger.warn(e.getMessage());
        }
    }

    public PollForecast sendConsumptionRequestMessage(ConsumptionRequest consumptionRequest) {
        this.pollForecastConsumptionMap.put(consumptionRequest.getRequestTimeSlotId(), new PollForecast());
        logger.trace("added ConsumptionPoll");

        Optional<Message> messageToSend = Optional.empty();

        // TODO: @Zivan @Paul are there more than one forecast services?
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


