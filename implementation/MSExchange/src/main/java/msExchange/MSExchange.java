package msExchange;

import broker.Broker;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import msExchange.networkCommunication.Communication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class MSExchange implements IExchange, Runnable {
    private static final Logger logger = LogManager.getLogger(MSExchange.class);
    private Broker broker;
    private BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outgoingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Bid> bidQueue;
    private BlockingQueue<Sell> sellQueue;
    private BlockingQueue<Transaction> transactionQueue;
    private Communication communication;
    private ExecutorService executorService;
    private UUID exchangeID;
    private boolean duplicated;
    private TimeSlotBuilder timeSlotManager;

    public MSExchange(boolean duplicated) {
        this.duplicated = duplicated;
    }

    private void startCommunication() {
        communication = new Communication(incomingMessages, outgoingMessages);
        communication.startBrokerRunner();
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);

    }

    @Override
    public void processBidQueue() {

    }

    @Override
    public void receivedBid(Bid bid) {

    }

    @Override
    public void receivedSell(Sell sell) {

    }


    public boolean isDuplicated() {
        return duplicated;
    }
}
