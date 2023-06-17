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

public class ConsumptionBuilding implements RESTData, Runnable {

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
        setInstanceforREST();
        this.communicator = new Communication(this.incomingMessages, port, EServiceType.Prosumer, this);

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

    protected void createConsumer() {
        final int INITIALIZED_CONSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("consumer.amount"));
        for (int i = 1; i <= INITIALIZED_CONSUMER_AMOUNT; i++) {
            createConsumer(EConsumerType.valueOf(ConfigFileReader.getProperty("consumer.type" + i)));
        }
    }

    protected void setInstanceforREST() {
        Singleton.getInstance().setConsumptionBuilding(this);
    }

    public EProsumerType getProsumerType() {
        return prosumerType;
    }

    // Define the CRUD methods

    // Create
    @Override
    public boolean createConsumer(EConsumerType type) {
        Consumer consumer = new Consumer(type);
        consumerList.add(consumer);
        logger.info("Created Consumer from type {}", type);
        return true;
    }

    // Read
    @Override
    public ArrayList<EConsumerType> getConsumers() {
        ArrayList<EConsumerType> consumerList = new ArrayList<>();
        for (Consumer consumer : this.consumerList) {
            consumerList.add(consumer.getConsumerType());
        }
        return consumerList;
    }

    public HashSet<Consumer> getConsumerss() {
        return consumerList;
    }


    // Update
    @Override
    public int updateConsumer(EConsumerType consumerType, int averageConsumption) {
        AtomicInteger countUpdated = new AtomicInteger();
        this.consumerList.forEach(consumer -> {
            if (consumer.getConsumerType().equals(consumerType)) {
                consumer.setAverageConsumption(averageConsumption);
                countUpdated.getAndIncrement();
            }
        });
        if (countUpdated.get() > 0) {
            logger.info("Updated {} Consumer from type {}", countUpdated.get(), consumerType);
            return countUpdated.get();
        } else {
            logger.info("No Producer Consumer from type {}", consumerType);
            return countUpdated.get();
        }

    }

    // Delete
    @Override
    public boolean deleteConsumer(EConsumerType type) {
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
        CallbackTransaction callbackOnTransaction = (totalPrice, singlePrice) -> {
            logger.debug("Transaction callback received with price {}", totalPrice);
            if (totalPrice > 0) {
                wallet.incrementCashBalance(totalPrice);
            } else {
                wallet.decrementCashBalance(totalPrice);
            }
            this.scheduler.insertValue(singlePrice);
            logger.debug("New cash balance {}", wallet.getCashBalance());
            logger.info("---------------------TimeSlot finished---------------------------");
        };
        return callbackOnTransaction;
    }

    private CallbackSellLower actSellLowerQuestion() {
        CallbackSellLower callbackOnSellLower = sellToChange -> {
            logger.info("SellLower callback received with min sell price from Exchange {}", sellToChange.getAskPrice());
            double priceToChange = sellToChange.getAskPrice();
            priceToChange = this.wallet.getLowerSellPrice(priceToChange);
            sellToChange.setAskPrice(priceToChange);
            try {
                logger.debug("SellLower Response with price {}", sellToChange.getAskPrice());
                this.communicator.sendLowerSell(sellToChange);
            } catch (ServiceNotFoundException e) {
                logger.error(e.getMessage());
            }

        };
        return callbackOnSellLower;

    }

    public CallbackBidHigher actBidHigherQuestion() {
        return bidToChange -> {
            logger.info("BidHigher callback received with min Bid price from Exchange {}", bidToChange.getPrice());
            double priceToChange = bidToChange.getPrice();
            priceToChange = this.wallet.getHigherBidPrice(priceToChange);
            bidToChange.setPrice(priceToChange);
            try {
                logger.debug("BidHigher Response with price {}", bidToChange.getPrice());
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
