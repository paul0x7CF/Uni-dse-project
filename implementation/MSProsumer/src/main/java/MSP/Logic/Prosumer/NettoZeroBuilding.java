package MSP.Logic.Prosumer;

import CF.sendable.TimeSlot;
import MSP.Communication.callback.CallbackSellLower;
import MSP.Communication.polling.PollProductionForecast;
import MSP.Configuration.ConfigFileReader;
import MSP.Data.*;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.ServiceNotFoundException;
import MSP.Exceptions.UndefinedStrategyException;
import MSP.Logic.AccountingStrategy.CalcProduction;
import MSP.Logic.AccountingStrategy.ContextCalcAcct;
import MSP.Logic.Scheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NettoZeroBuilding extends ConsumptionBuilding {

    // Define the logger

    private static final Logger logger = LogManager.getLogger(NettoZeroBuilding.class);

    // Define the private fields

    private LinkedHashSet<Producer> producerList = new LinkedHashSet<>();
    private PollProductionForecast pollOnProduction;

    // Define the constructor

    public NettoZeroBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);

        final int INITIALIZED_PRODUCER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("producer.amount"));
        for (int i = 0; i < INITIALIZED_PRODUCER_AMOUNT; i++) {
            createProducer(EProducerType.valueOf(ConfigFileReader.getProperty("producer.type" + (i + 1))));
        }
        communicator.setCallbackOnSellLower(actSellLowerQuestion());

        logger.info("{} Producer created", producerList.size() + 1);

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

    // Define the methods for the Logic

    @Override
    protected void executeAccountingStrategy(TimeSlot newTimeSlot) throws ServiceNotFoundException, DeviceNotSupportedException, UndefinedStrategyException {
        super.executeAccountingStrategy(newTimeSlot);
        try {

            ContextCalcAcct contextCalcAcct = new ContextCalcAcct();
            contextCalcAcct.setCalcAcctAStrategy(new CalcProduction(super.communicator));

            this.pollOnProduction = (PollProductionForecast) contextCalcAcct.calculateAccounting(new ArrayList<>(producerList), newTimeSlot);

        } catch (DeviceNotSupportedException | UndefinedStrategyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected double scheduleEnergyAmount(TimeSlot currTimeSlot) {
        double resultEnergyAmount;
        double neededEnergyAmount = super.scheduleEnergyAmount(currTimeSlot);
        double producedEnergyAmount = (double) this.pollOnProduction.getForecastResult();
        resultEnergyAmount = neededEnergyAmount - producedEnergyAmount;
        return resultEnergyAmount;
    }

    @Override
    protected void reset() {
        super.reset();
        this.pollOnProduction = null;
    }
    @Override
    protected boolean isForecastResultAvailable() {
        boolean resultForConsumption = super.isForecastResultAvailable();
        boolean resultForProduction = this.pollOnProduction.isAvailable();
        return resultForConsumption && resultForProduction;
    }


    // Define the CRUD methods

    // Create
    @Override
    public boolean createProducer(EProducerType panelType) {
        Producer producer = new Producer(panelType);
        producerList.add(producer);
        logger.info("Created Producer from type {}", panelType);
        return true;
    }

    // Read
    @Override
    public ArrayList<EProducerType> getProducers() {
        ArrayList<EProducerType> consumerList = new ArrayList<>();
        for (Producer producer : this.producerList) {
            consumerList.add(producer.getProducerType());
        }
        return consumerList;
    }

    // Update
    @Override
    public int updateProducer(EProducerType panelType, int efficiency) {
        int countUpdated = 0;
        for (Producer producer : producerList) {
            if (producer.getProducerType().equals(panelType)) {
                producer.setEfficiency(efficiency);
                countUpdated++;
            }
        }
        return countUpdated;
    }

    // Delete
    @Override
    public boolean deleteProducer(EProducerType panelType) {
        AtomicInteger countDeleted = new AtomicInteger();
        this.producerList.removeIf(producer -> {
            if(producer.getProducerType().equals(panelType)){
                countDeleted.getAndIncrement();
                return true;
            }
            return false;
        });
        if (countDeleted.get() > 0) {
            logger.info("Deleted {} Producer from type {}", countDeleted.get(), panelType);
            return true;
        } else {
            logger.info("No Producer found to delete from type {}", panelType);
            return false;
        }

    }



}



