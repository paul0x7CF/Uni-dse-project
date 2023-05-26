package msExchange;

import broker.Broker;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import org.apache.logging.log4j.message.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class MSExchange implements IExchange, Runnable {
    Broker broker;
    BlockingQueue<Message> incomingQueue;
    BlockingQueue<Bid> bidQueue;
    BlockingQueue<Sell> sellQueue;
    BlockingQueue<Transaction> transactionQueue;
    ExecutorService executorService;
    private UUID exchangeID;
    private boolean duplicated;
    private TimeSlotBuilder timeSlotManager;

    public MSExchange(boolean duplicated) {
        this.duplicated = duplicated;
        this.incomingQueue = incomingQueue;
    }

    @Override
    public void run() {

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


}
