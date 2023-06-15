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

import MSP.Logic.Scheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumptionBuilding implements Runnable {

    // Define the logger

    private static final Logger logger = LogManager.getLogger(ConsumptionBuilding.class);

    // Define the private fields

    private final EProsumerType prosumerType;
    private LinkedHashSet<Consumer> consumerList = new LinkedHashSet<>();
    private Wallet wallet;
    protected Communication communicator;
    private BlockingQueue<TimeSlot> incomingMessages;
    protected PollConsumptionForecast pollOnConsumption;
    protected Scheduler scheduler = new Scheduler();

    // Define the constructor

    public ConsumptionBuilding(EProsumerType prosumerType, double cashBalance, final int port) {
        this.prosumerType = prosumerType;
        this.wallet = new Wallet(cashBalance);
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.communicator = new Communication(this.incomingMessages, port, EServiceType.Prosumer);
        final int INITIALIZED_CONSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("consumer.amount"));
        for (int i = 0; i < INITIALIZED_CONSUMER_AMOUNT; i++) {
            createConsumer(EConsumerType.valueOf(ConfigFileReader.getProperty("consumer.type" + ++i)));
        }

        logger.info("Prosumer created from type {} with: {} Consumer, cash balance {}", prosumerType, consumerList.size() + 1, cashBalance);
    }

    // Define the CRUD methods

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
        if (countDeleted.get() > 0) {
            logger.info("Deleted {} Consumer from type {}", countDeleted.get(), type);
            return true;
        } else {
            logger.info("No Producer Consumer from type {}", type);
            return false;
        }
    }

    // Define the methods for the Logic

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

    protected void executeAccountingStrategy(TimeSlot newTimeSlot) {
        try {
            ContextCalcAcct contextCalcAcct = new ContextCalcAcct();
            contextCalcAcct.setCalcAcctAStrategy(new CalcConsumption(communicator));

            this.pollOnConsumption = (PollConsumptionForecast) contextCalcAcct.calculateAccounting(new ArrayList<>(consumerList), newTimeSlot);
        } catch (DeviceNotSupportedException | UndefinedStrategyException e) {
            throw new RuntimeException(e);
        }

    }

    protected boolean isForecastResultAvailable() {
        return this.pollOnConsumption.isAvailable();
    }

    private void setConsumptionForecastResults(TimeSlot currTimeSlot) {
        HashMap<EConsumerType, Double> consumptionPollResultMap = (HashMap<EConsumerType, Double>) this.pollOnConsumption.getForecastResult();
        int countConsumerResults = 0;
        int countNoConsumerResults = 0;
        for (Consumer currConsumer : consumerList) {
            EConsumerType currConsumerType = currConsumer.getConsumerType();
            if (consumptionPollResultMap.containsKey(currConsumerType)) {
                currConsumer.setResultOfForecast(consumptionPollResultMap.get(currConsumerType));
                countConsumerResults++;
            } else if (currConsumer.isAllowedToConsume(currTimeSlot.getStartTime().toLocalTime())) {
                logger.error("No forecast result for Consumer type {}", currConsumerType);
            } else {
                countNoConsumerResults++;
            }
        }
        logger.debug("Forecast result set for {} Consumer", countConsumerResults);
        logger.debug("No forecast result set for {} Consumer because are not allowed to Consume", countNoConsumerResults);
    }

    protected double scheduleEnergyAmount(TimeSlot currTimeSlot) {
        double resultNeededEnergyAmount;
        setConsumptionForecastResults(currTimeSlot);
        resultNeededEnergyAmount = this.scheduler.calculate(this.consumerList);
        return resultNeededEnergyAmount;
    }

    protected void reset() {
        this.pollOnConsumption = null;
    }

    @Override
    public void run() {
        communicator.startBrokerRunner(Thread.currentThread().getName());
        communicator.addMessageHandler(ECategory.Exchange);
        communicator.addMessageHandler(ECategory.Auction);
        communicator.addMessageHandler(ECategory.Forecast);


        try {
            reset();

            TimeSlot newTimeSlot = incomingMessages.take();
            logger.info("Start executing Prosumer logic for new TimeSlot");
            this.executeAccountingStrategy(newTimeSlot);
            logger.debug("Waiting for forecast result");
            do {
                Thread.sleep(1000);
            } while (!isForecastResultAvailable());
            logger.debug("Forecast result is available continue with execution");

            double energyAmount = 0;
            energyAmount = scheduleEnergyAmount(newTimeSlot);
            if (energyAmount > 0) {
                logger.info("Prosumer {} need {} kWh", prosumerType, energyAmount);
                // TODO: create Bid
            } else if (energyAmount < 0) {
                logger.info("Prosumer {} has {} kWh more as needed", prosumerType, energyAmount);
                // TODO: create Sell
            } else {
                logger.info("-------------0--------------");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
