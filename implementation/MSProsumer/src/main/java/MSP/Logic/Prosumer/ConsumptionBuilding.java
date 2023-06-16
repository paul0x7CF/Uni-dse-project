package MSP.Logic.Prosumer;

import MSP.Communication.callback.CallbackBidHigher;
import MSP.Communication.callback.CallbackSellLower;
import MSP.Communication.callback.CallbackTransaction;
import MSP.Communication.Communication;
import MSP.Communication.polling.PollConsumptionForecast;
import MSP.Configuration.ConfigFileReader;
import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Data.EProsumerType;
import MSP.Data.Wallet;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.ServiceNotFoundException;
import MSP.Exceptions.TimeOutWaitingRuntimeException;
import MSP.Exceptions.UndefinedStrategyException;
import MSP.Logic.AccountingStrategy.CalcConsumption;
import MSP.Logic.AccountingStrategy.ContextCalcAcct;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.TimeSlot;

import MSP.Logic.Scheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

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

        // Set Callbacks
        communicator.setCallbackOnTransaction(this.actOnTransactionFinished());
        communicator.setCallbackOnSellLower(this.actSellLowerQuestion());
        communicator.setCallbackOnBidHigher(this.actBidHigherQuestion());

        final int INITIALIZED_CONSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("consumer.amount"));
        for (int i = 1; i <= INITIALIZED_CONSUMER_AMOUNT; i++) {
            createConsumer(EConsumerType.valueOf(ConfigFileReader.getProperty("consumer.type" + i)));
        }

        logger.info("Prosumer created from type {} with: {} Consumer from type, cash balance {}", prosumerType, consumerList.size(), cashBalance);
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

    private CallbackTransaction actOnTransactionFinished() {
        CallbackTransaction callbackOnTransaction = price -> {
            logger.debug("Transaction callback received with price {}", price);
            if (price > 0) {
                wallet.incrementCashBalance(price);
            } else {
                wallet.decrementCashBalance(price);
            }
            logger.debug("New cash balance {}", wallet.getCashBalance());
            logger.info("---------------------TimeSlot finished---------------------------");
        };
        return callbackOnTransaction;
    }

    private CallbackSellLower actSellLowerQuestion() {
        CallbackSellLower callbackOnSellLower = sellToChange -> {
            logger.info("SellLower callback received with price {}", sellToChange.getAskPrice());
            double priceToChange = sellToChange.getAskPrice();
            priceToChange = this.wallet.getLowerSellPrice(priceToChange);
            sellToChange.setAskPrice(priceToChange);
            try {
                this.communicator.sendLowerSell(sellToChange);
            } catch (ServiceNotFoundException e) {
                logger.error(e.getMessage());
            }

        };
        return callbackOnSellLower;

    }

    public CallbackBidHigher actBidHigherQuestion() {
        return bidToChange -> {
            logger.info("BidHigher callback received with price {}", bidToChange.getPrice());
            double priceToChange = bidToChange.getPrice();
            priceToChange = this.wallet.getHigherBidPrice(priceToChange);
            bidToChange.setPrice(priceToChange);
            try {
                this.communicator.sendHigherBid(bidToChange);
            } catch (ServiceNotFoundException e) {
                logger.error(e.getMessage());
            }

        };

    }

    protected void executeAccountingStrategy(TimeSlot newTimeSlot) throws DeviceNotSupportedException, UndefinedStrategyException, ServiceNotFoundException {
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

        // Initialize the Broker
        communicator.startBrokerRunner(Thread.currentThread().getName());
        communicator.addMessageHandler(ECategory.Exchange);
        communicator.addMessageHandler(ECategory.Auction);
        communicator.addMessageHandler(ECategory.Forecast);


        try {
            Thread.sleep(1000);
            while (true) {
                reset();
                logger.info("Waiting for new TimeSlot");
                TimeSlot newTimeSlot = incomingMessages.take();
                // TODO: check if new Day
                logger.info("------------------Start executing Prosumer logic for new TimeSlot---------------------");
                this.executeAccountingStrategy(newTimeSlot);
                logger.info("Waiting for forecast result");
                int timeoutcounter = 0;
                do {
                    timeoutcounter++;
                    Thread.sleep(1000);
                    if(timeoutcounter > 15){
                        throw new TimeOutWaitingRuntimeException("Timeout waiting for forecast result");
                    }
                } while (!isForecastResultAvailable());

                logger.info("Forecast result is available continue with execution");

                double energyAmount = 0;
                energyAmount = scheduleEnergyAmount(newTimeSlot);
                Random random = new Random();
                double randomValue = 1 + (random.nextDouble() * 2);
                if (energyAmount > 0) {
                    logger.info("Prosumer {} need {} Wh", prosumerType, energyAmount);
                    this.communicator.sendBid(energyAmount, this.wallet.getSellPrice()*randomValue, newTimeSlot);
                } else if (energyAmount < 0) {
                    energyAmount = energyAmount * (-1);
                    logger.info("Prosumer {} has {} Wh more as needed", prosumerType, energyAmount);
                    this.communicator.sendSell(energyAmount, this.wallet.getBidPrice()*randomValue, newTimeSlot);
                } else {
                    logger.info("-------------0--------------");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ServiceNotFoundException | DeviceNotSupportedException | UndefinedStrategyException e) {
            logger.fatal(e.getMessage() + ";--------------Prosumer stopped-----------------");
        }


    }
}
