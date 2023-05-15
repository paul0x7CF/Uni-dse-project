package msExchange.timeSlotManagement;

import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

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
        while (true) {
            processSellQueue();
            processBidQueue();
        }
    }

    private void processBidQueue() {
        Bid bid = bidQueue.poll();
        if (bid != null) {
            UUID timeSlot = bid.getTimeSlot();
            BlockingQueue<Bid> queue = timeSlotBidQueue.get(timeSlot);
            if (queue == null) {
                // Create a new BlockingQueue for this time slot as it doesn't exist
                queue = new LinkedBlockingQueue<>();
                timeSlotBidQueue.put(timeSlot, queue);

            }
            queue.add(bid); //Add the bid to the BlockingQueue for this time slot
        }
    }

    private void processSellQueue() {
        Sell sell = sellQueue.poll();
        if (sell != null) {
            UUID timeSlot = sell.getTimeSlot();
            BlockingQueue<Sell> queue = timeSlotSellQueue.get(timeSlot);
            if (queue == null) {
                //Create a new BlockingQueue for this time slot as it doesn't exist
                queue = new LinkedBlockingQueue<>();
                timeSlotSellQueue.put(timeSlot, queue);
            }
            queue.add(sell); //Add the sell to the BlockingQueue for this time slot
        }
    }

    public synchronized void updateTimeSlots(List<sendable.TimeSlot> newTimeSlots) {
        for (sendable.TimeSlot slot : newTimeSlots) {
            if (timeSlots.get(slot.getTimeSlotID()) == null) {
                timeSlots.put(slot.getTimeSlotID(), createTimeSlot(slot));
            }
        }
    }

    private TimeSlot createTimeSlot(sendable.TimeSlot slot) {
        return new TimeSlot(slot.getTimeSlotID(), slot.getStartTime(), slot.getEndTime());
    }

    private synchronized void closeTimeSlot(UUID slotId) {

    }
}
