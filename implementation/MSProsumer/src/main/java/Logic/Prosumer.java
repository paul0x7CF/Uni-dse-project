package Logic;

import Data.Consumer;
import Data.EProsumerType;
import Data.SolarPanel;
import Data.Wallet;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Prosumer implements Runnable{

    private UUID prosumerID;
    private EProsumerType prosumerType;
    private DemandManager demandManager;
    private Scheduler scheduler;
    private List<SolarPanel> producer;
    private List<Consumer> consumer;
    private Wallet wallet;
    private HashMap<UUID, BlockingQueue<Message>> slotsDemand;
    private BlockingQueue<TimeSlot> availableTimeSlots;
    private BlockingQueue<Message> outgoingMessages;

    public Prosumer(EProsumerType prosumerType, BlockingQueue<TimeSlot> availableTimeSlots, BlockingQueue<Message> outgoingMessages) {
        this.prosumerType = prosumerType;
        this.availableTimeSlots = availableTimeSlots;
        this.outgoingMessages = outgoingMessages;
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

    }
}
