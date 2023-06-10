package Logic.Prosumer;

import Communication.Communication;
import Communication.PollForecast;
import Configuration.ConfigFileReader;
import Data.Consumer;
import Data.EConsumerType;
import Data.EProsumerType;
import Data.Wallet;
import Exceptions.DeviceNotSupportedException;
import Exceptions.UndefinedStrategyException;
import Logic.AccountingStrategy.CalcConsumption;
import Logic.AccountingStrategy.ContextCalcAcct;
import Logic.DemandManager;
import protocol.ECategory;
import protocol.Message;
import sendable.Bid;
import sendable.EServiceType;
import sendable.Sell;
import sendable.TimeSlot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Prosumer implements Runnable {

    private static final Logger logger = LogManager.getLogger(Prosumer.class);


    private EProsumerType prosumerType;
    private DemandManager demandManager;
    private List<Consumer> consumerList = new LinkedList<>();
    private Wallet wallet;
    private Communication communicator;
    private HashMap<UUID, BlockingQueue<Message>> slotsDemand;
    private BlockingQueue<TimeSlot> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;

    public Prosumer(EProsumerType prosumerType, BlockingQueue<TimeSlot> availableTimeSlots, BlockingQueue<Message> outgoingMessages) {
        this.prosumerType = prosumerType;
        //this.availableTimeSlots = availableTimeSlots;
        this.outgoingMessages = outgoingMessages;
    }

    public Prosumer(EProsumerType prosumerType, double cashBalance, final int port) {
        this.prosumerType = prosumerType;
        this.wallet = new Wallet(cashBalance);
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.outgoingMessages = new LinkedBlockingQueue<>();
        this.communicator = new Communication(this.incomingMessages, this.outgoingMessages, port, this, EServiceType.Prosumer);
        final int INITIALIZED_CONSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("consumer.amount"));
        for(int i = 0; i < INITIALIZED_CONSUMER_AMOUNT; i++) {
            createConsumer(EConsumerType.valueOf(ConfigFileReader.getProperty("consumer.type" + ++i)));
        }

        logger.info("Prosumer created from type {} with: {} Consumer, cash balance {}", prosumerType,consumerList.size()+1, cashBalance);
    }

    private void createProducer() {

    }

    private void createConsumer(EConsumerType type) {
        Consumer newConsumer = new Consumer(type);
        consumerList.add(newConsumer);
    }

    public void actSellLowerQuestion(Message message) {

    }

    public void actBidHigherQuestion(Message message) {

    }

    private Bid createBid(double volume) {
        return null;
    }

    private Sell createSell(double volume) {
        return null;
    }

    @Override
    public void run() {
        communicator.startBrokerRunner();
        communicator.addMessageHandler(ECategory.Exchange);
        communicator.addMessageHandler(ECategory.Auction);
        communicator.addMessageHandler(ECategory.Forecast);


        try {
            TimeSlot newTimeSlot = incomingMessages.take();
            ContextCalcAcct contextCalcAcct = new ContextCalcAcct();

            contextCalcAcct.setCalcAcctAStrategy(new CalcConsumption(communicator));
            PollForecast myPoll= contextCalcAcct.calculateAccounting(new ArrayList<>(consumerList), newTimeSlot.getTimeSlotID());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (DeviceNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (UndefinedStrategyException e) {
            throw new RuntimeException(e);
        }


    }
}
