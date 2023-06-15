package MSP.Logic.Prosumer;

import MSP.Communication.Communication;
import MSP.Communication.polling.PollConsumptionForecast;
import MSP.Configuration.ConfigFileReader;
import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Data.EProsumerType;
import MSP.Data.Wallet;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.UndefinedStrategyException;
import MSP.Logic.AccountingStrategy.CalcConsumption;
import MSP.Logic.AccountingStrategy.ContextCalcAcct;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.Bid;
import CF.sendable.EServiceType;
import CF.sendable.Sell;
import CF.sendable.TimeSlot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumptionBuilding implements Runnable {

    private static final Logger logger = LogManager.getLogger(ConsumptionBuilding.class);


    private final EProsumerType prosumerType;
    private LinkedHashSet<Consumer> consumerList = new LinkedHashSet<>();
    private Wallet wallet;
    protected Communication communicator;
    private HashMap<UUID, BlockingQueue<Message>> slotsDemand;
    private BlockingQueue<TimeSlot> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;
    protected PollConsumptionForecast pollOnConsumption;


    public ConsumptionBuilding(EProsumerType prosumerType, double cashBalance, final int port) {
        this.prosumerType = prosumerType;
        this.wallet = new Wallet(cashBalance);
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.outgoingMessages = new LinkedBlockingQueue<>();
        this.communicator = new Communication(this.incomingMessages, this.outgoingMessages, port, EServiceType.Prosumer);
        final int INITIALIZED_CONSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("consumer.amount"));
        for (int i = 0; i < INITIALIZED_CONSUMER_AMOUNT; i++) {
            createConsumer(EConsumerType.valueOf(ConfigFileReader.getProperty("consumer.type" + ++i)));
        }

        logger.info("Prosumer created from type {} with: {} Consumer, cash balance {}", prosumerType, consumerList.size() + 1, cashBalance);
    }

    private void createConsumer(EConsumerType type) {
        Consumer newConsumer = new Consumer(type);
        consumerList.add(newConsumer);
    }

    private boolean deleteConsumer(EConsumerType type) {
        AtomicInteger countDeleted = new AtomicInteger();
        this.consumerList.removeIf(consumer -> {
            countDeleted.getAndIncrement();
            return consumer.getConsumerType().equals(type);
        });
        if(countDeleted.get() > 0) {
            logger.info("Deleted {} Consumer from type {}", countDeleted.get(), type);
            return true;
        }
        else {
            logger.info("No Producer Consumer from type {}", type);
            return false;
        }
    }



    public void increaseCashBalance(double amount) {
        wallet.incrementCashBalance(amount);
    }

    public void decreaseCashBalance(double amount) {
        wallet.decrementCashBalance(amount);
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

    public void executeAccountingStrategy(TimeSlot newTimeSlot) {
        try {
            ContextCalcAcct contextCalcAcct = new ContextCalcAcct();
            contextCalcAcct.setCalcAcctAStrategy(new CalcConsumption(communicator));

            this.pollOnConsumption = (PollConsumptionForecast) contextCalcAcct.calculateAccounting(new ArrayList<>(consumerList), newTimeSlot);
        } catch (DeviceNotSupportedException | UndefinedStrategyException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isForecastResultAvailable() {
        return this.pollOnConsumption.isAvailable();
    }

    @Override
    public void run() {
        communicator.startBrokerRunner(Thread.currentThread().getName());
        communicator.addMessageHandler(ECategory.Exchange);
        communicator.addMessageHandler(ECategory.Auction);
        communicator.addMessageHandler(ECategory.Forecast);


        try {
            TimeSlot newTimeSlot = incomingMessages.take();
            logger.info("Start executing Prosumer logic for new TimeSlot");
            this.executeAccountingStrategy(newTimeSlot);
            logger.debug("Waiting for forecast result");
            do{
                Thread.sleep(1000);
            }while (!isForecastResultAvailable());
            logger.debug("Forecast result is available continue with execution");


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
