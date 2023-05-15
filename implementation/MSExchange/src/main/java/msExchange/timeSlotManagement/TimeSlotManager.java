package msExchange.timeSlotManagement;

import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class TimeSlotManager implements Runnable {
    private final Map<UUID, TimeSlot> timeSlots = new HashMap<>();
    private BlockingQueue<Sell> sellQueue;
    private BlockingQueue<Bid> bidQueue;
    private BlockingQueue<Transaction> transactionQueue;
    private Map<UUID, BlockingQueue<Bid>> timeSlotBidQueue;
    private Map<UUID, BlockingQueue<Sell>> timeSlotSellQueue;

    private ExecutorService executorService;

    public TimeSlotManager(final BlockingQueue<Sell> sellQueue, final BlockingQueue<Bid> bidQueue, final BlockingQueue<Transaction> transactionQueue) {
        this.sellQueue = sellQueue;
        this.bidQueue = bidQueue;
        this.transactionQueue = transactionQueue;
    }

    @Override
    public void run() {

    }

    public Optional<Transaction> updateTimeSlots(List<sendable.TimeSlot> timeSlots) {
        return null;
    }

    private synchronized void createTimeSlot(sendable.TimeSlot timeSlot) {
    }

    private synchronized Transaction closeTimeSlot(UUID slotId) {
        return null;
    }

    private void addTimeSlot(TimeSlot timeSlot) {
    }

    private Transaction closeTimeSlot(TimeSlot timeSlot) {
        return null;
    }


}
