package Logic.Prosumer;

import Communication.Communication;
import Data.Consumer;
import Data.EProsumerType;
import Data.IProsumerDevice;
import Data.Wallet;
import Exceptions.DeviceNotSupportedException;
import Exceptions.UndefinedStrategyException;
import Logic.AccountingStrategy.CalcConsumption;
import Logic.AccountingStrategy.ContextCalcAcct;
import Logic.DemandManager;
import Logic.Scheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.Bid;
import sendable.EServiceType;
import sendable.Sell;
import sendable.TimeSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Prosumer implements Runnable {

    private static final Logger logger = LogManager.getLogger(Prosumer.class);


    private EProsumerType prosumerType;
    private DemandManager demandManager;
    private List<Consumer> consumer;
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

        logger.info("Prosumer created from type {} with cash balance {}", prosumerType, cashBalance);
    }

    private void createProducer() {

    }

    private void createConsumer() {

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
            contextCalcAcct.calculateAccounting(new ArrayList<>(consumer), newTimeSlot.getTimeSlotID());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (DeviceNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (UndefinedStrategyException e) {
            throw new RuntimeException(e);
        }


    }
}
