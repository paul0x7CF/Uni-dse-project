package MSP.Communication;

import CF.sendable.*;
import MSP.Communication.MessageHandling.MessageBuilder;
import MSP.Communication.callback.CallbackBidHigher;
import MSP.Communication.callback.CallbackSellLower;
import MSP.Communication.callback.CallbackTransaction;
import MSP.Communication.polling.PollConsumptionForecast;
import MSP.Communication.polling.PollProductionForecast;
import MSP.Data.Consumer;
import MSP.Exceptions.ServiceNotFoundException;
import MSP.Exceptions.UnknownMessageException;
import MSP.Communication.MessageHandling.AuctionMessageHandler;
import MSP.Communication.MessageHandling.ExchangeMessageHandler;
import MSP.Communication.MessageHandling.ForecastMessageHandler;
import CF.broker.BrokerRunner;
import MSP.Logic.Prosumer.RESTData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.ECategory;
import CF.protocol.Message;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.*;
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
    private CallbackTransaction callbackOnTransaction;
    private CallbackSellLower callbackOnSellLower;
    private CallbackBidHigher callbackOnBidHigher;

    // Define the constructor

    public Communication(BlockingQueue<TimeSlot> availableTimeSlot, final int port, EServiceType serviceType, RESTData restData) {
        this.inputQueueTimeSlot = availableTimeSlot;
        this.serviceType = serviceType;
        createBroker(port);
        this.messageBuilder = new MessageBuilder(this.myMSData);

        ConfigurableApplicationContext context = new SpringApplicationBuilder(RestHandler.class).properties(Collections.singletonMap("server.port", port + 2)).run();

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
                case Auction -> {
                    this.communicationBroker.addMessageHandler(ECategory.Auction, new AuctionMessageHandler(this.myMSData, this.callbackOnBidHigher, this.callbackOnSellLower));
                }
                case Exchange -> {
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new ExchangeMessageHandler(this.inputQueueTimeSlot, this.callbackOnTransaction, this.myMSData));
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

    // Set the callbacks

    public void setCallbackOnTransaction(CallbackTransaction callbackOnTransaction) {
        this.callbackOnTransaction = callbackOnTransaction;
    }

    public void setCallbackOnSellLower(CallbackSellLower callbackSellLower) {
        this.callbackOnSellLower = callbackSellLower;
    }

    public void setCallbackOnBidHigher(CallbackBidHigher callbackBidHigher) {
        this.callbackOnBidHigher = callbackBidHigher;
    }


    public void resetPollingMaps(){
        this.pollForecastConsumptionMap.clear();
        this.pollForecastProductionMap.clear();
    }

    // Define the methods for sending messages

    public PollConsumptionForecast sendConsumptionRequestMessage(ConsumptionRequest consumptionRequest) throws ServiceNotFoundException {
        logger.trace("Executing Send Consumption Request Message");
        this.pollForecastConsumptionMap.put(consumptionRequest.getRequestTimeSlotId(), new PollConsumptionForecast());
        logger.trace("added ConsumptionPoll");

        Optional<Message> messageToSend = Optional.empty();

        // Send message to all services of type Forecast for ConsumptionRequest
        // Only one Response message will be needed because all services of type Forecast will send the same response
        int countSending = 0;
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Forecast)) {
            messageToSend = Optional.of(this.messageBuilder.buildConsumptionForecastMessage(consumptionRequest, receiverService));
            communicationBroker.sendMessage(messageToSend.get());
            logger.trace("ConsumptionRequestMessage sent to: Ip:{}, Port: {}, with message ID {}", receiverService.getAddress(), receiverService.getPort(), messageToSend.get().getMessageID());
            countSending++;
        }
        logger.debug("Consumption RequestMessage was sent to {} Forecast services", countSending);

        if (messageToSend.isEmpty()) {
            throw new ServiceNotFoundException("Forecast Service was not found to send Consumption Request");
        }

        return this.pollForecastConsumptionMap.get(consumptionRequest.getRequestTimeSlotId());
    }

    public PollProductionForecast sendProductionRequestMessage(SolarRequest solarRequest) throws ServiceNotFoundException {
        logger.trace("Executing Send Production Request Message");
        this.pollForecastProductionMap.put(solarRequest.getRequestTimeSlotId(), new PollProductionForecast());
        logger.trace("added ProductionPoll");
        Optional<Message> messageToSend = Optional.empty();

        int countSending = 0;
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Forecast)) {
            messageToSend = Optional.of(this.messageBuilder.buildProductionForecastMessage(solarRequest, receiverService));
            communicationBroker.sendMessage(messageToSend.get());
            logger.trace("ProductionRequestMessage sent to: to: Ip:{}, Port: {}, with message ID {}", receiverService.getAddress(), receiverService.getPort(), messageToSend.get().getMessageID());
            countSending++;
        }
        logger.debug("Production RequestMessage was sent to {} Forecast services", countSending);
        if (messageToSend.isEmpty()) {
            throw new ServiceNotFoundException("Forecast Service was not found to send Production Request");
        }
        return this.pollForecastProductionMap.get(solarRequest.getRequestTimeSlotId());
    }

    public void sendBid(double energyAmount, double price, TimeSlot auctionTimeSlot) throws ServiceNotFoundException {
        logger.trace("Executing Send Bid Message");
        Bid bidToSend = new Bid(energyAmount, price, auctionTimeSlot.getTimeSlotID(), this.myMSData.getId());
        logger.trace("Bid created with: energyAmount: {}, price: {}", energyAmount, price);
        sendBid(bidToSend);

    }

    public void sendSell(double energyAmount, double price, TimeSlot auctionTimeSlot) throws ServiceNotFoundException {
        logger.trace("Executing Send Sell Message");
        Sell sellToSend = new Sell(energyAmount, price, auctionTimeSlot.getTimeSlotID(), this.myMSData.getId());
        logger.trace("Sell created with: energyAmount: {}, price: {}", energyAmount, price);
        sendSell(sellToSend);

    }

    public void sendLowerSell(Sell sellToSend) throws ServiceNotFoundException {
        logger.trace("Executing Send Lower Sell Message");
        sendSell(sellToSend);

    }

    public void sendHigherBid(Bid bidToChange) throws ServiceNotFoundException {
        logger.trace("Executing Send Higher Bid Message");
        sendBid(bidToChange);
    }


    private void sendSell(Sell sellToSend) throws ServiceNotFoundException {
        Optional<Message> messageToSend = Optional.empty();
        int countExchangeServices = 0;
        int countSending = 0;
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Exchange)) {
            if (countSending == 0) {
                messageToSend = Optional.of(this.messageBuilder.buildSellMessage(sellToSend, receiverService));
                communicationBroker.sendMessage(messageToSend.get());
                logger.trace("Sell Message sent to: Ip:{}, Port: {}", receiverService.getAddress(), receiverService.getPort());
                countSending++;
            }
            countExchangeServices++;
        }
        logger.debug("Sell Message was sent to {} Exchange services", countExchangeServices);
        if (countExchangeServices > 1) {
            logger.warn("More than one Exchange service was found; expected only one; Message was sent to the first one");
        }

        if (messageToSend.isEmpty()) {
            throw new ServiceNotFoundException("Exchange Service was not found to send Sell");
        }
    }

    private void sendBid(Bid bidToSend) throws ServiceNotFoundException {

        Optional<Message> messageToSend = Optional.empty();

        int countExchangeServices = 0;
        int countSending = 0;
        for (MSData receiverService : communicationBroker.getServicesByType(EServiceType.Exchange)) {
            if (countSending == 0) {
                messageToSend = Optional.of(this.messageBuilder.buildBidMessage(bidToSend, receiverService));
                communicationBroker.sendMessage(messageToSend.get());
                logger.trace("Bid Message sent to: Ip:{}, Port: {}", receiverService.getAddress(), receiverService.getPort());
                countSending++;
            }
            countExchangeServices++;
        }
        logger.debug("Bid Message was sent to {} Exchange services", countExchangeServices);
        if (countExchangeServices > 1) {
            logger.warn("More than one Exchange service was found; expected only one; Message was sent to the first one");
        }

        if (messageToSend.isEmpty()) {
            throw new ServiceNotFoundException("Exchange Service was not found to send Bid");
        }
    }
}


