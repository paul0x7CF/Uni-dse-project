package Communication;

import Communication.MessageHandling.MessageBuilder;
import Exceptions.ServiceNotFoundRuntimeException;
import Exceptions.UnknownMessageException;
import Communication.MessageHandling.AuctionMessageHandler;
import Communication.MessageHandling.ExchangeMessageHandler;
import Communication.MessageHandling.ForecastMessageHandler;
import Logic.Prosumer.Prosumer;
import MSProsumer.Main.ProsumerManager;
import broker.BrokerRunner;
import messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.ConsumptionRequest;
import sendable.EServiceType;
import sendable.MSData;
import sendable.TimeSlot;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class Communication {

    private static final Logger logger = LogManager.getLogger(Communication.class);

    private MSData myMSData;
    private EServiceType serviceType;
    private BrokerRunner communicationBroker;
    private Prosumer myProsumer;

    private MessageBuilder messageBuilder;

    private BlockingQueue<TimeSlot> inputQueueTimeSlot;

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

    private void sendConsumptionRequestMessage(ConsumptionRequest consumptionRequest) {
        final Optional<Message>[] messageToSend = new Optional[]{Optional.empty()};
        communicationBroker.getServices().forEach(service -> {
            if (service.getType().equals(EServiceType.Consumption)) {
                MSData receiver = service;
                messageToSend[0] = Optional.of(this.messageBuilder.buildConsumptionForecastMessage(consumptionRequest, receiver));
            }
        });
        if(messageToSend[0].isPresent()) {
            communicationBroker.sendMessage(messageToSend[0].get());
        }
        else {
            throw new ServiceNotFoundRuntimeException();
        }

    }


}
