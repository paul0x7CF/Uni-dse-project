package Logic.Prosumer;

import Communication.Communication;
import Data.Consumer;
import Data.EProsumerType;
import Data.SolarPanel;
import Data.Wallet;
import Logic.DemandManager;
import Logic.Scheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Prosumer implements Runnable{

    private static final Logger logger = LogManager.getLogger(Prosumer.class);


    private UUID prosumerID;
    private EProsumerType prosumerType;
    private DemandManager demandManager;
    private List<Consumer> consumer;
    private Wallet wallet;
    private Communication communicator;
    private HashMap<UUID, BlockingQueue<Message>> slotsDemand;
    private BlockingQueue<Message> incomingMessages;
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
        this.communicator = new Communication(this.incomingMessages, this.outgoingMessages, port);

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
        this.communicator.startBrokerRunner();
        /*
        while (true) {
            try {
                Message newMessage = this.incomingMessages.take();
                logger.debug("Message received from type : {}", newMessage.getCategory());
                switch (newMessage.getCategory()) {
                    case SellLowerQuestion:
                        actSellLowerQuestion(message);
                        break;
                    case BidHigherQuestion:
                        actBidHigherQuestion(message);
                        break;
                    default:
                        throw new UnknownMessageException();
                }
            } catch (InterruptedException e) {
                logger.error("Error while receiving message: {}", e.getMessage());
            } catch (UnknownMessageException e) {
                logger.warn(e.getMessage());
            }


        }
        */


    }
}
