package exchangeTests.AuctionManager;


import msExchange.auctionManagement.AuctionManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestAddAuction {

    @Test
    public void givenSellInBlockingQueue_run_expectedNewAuction() {
        //arrange
        BlockingQueue<Sell> sellQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();

        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(4));
        List<TimeSlot> timeSlotList = new ArrayList<>();
        timeSlotList.add(timeSlot);
        UUID slotID = timeSlot.getTimeSlotID();

        Sell sell = new Sell(12, 12, slotID, UUID.randomUUID());
        sell.setAuctionID(UUID.randomUUID());
        try {
            sellQueue.put(sell);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AuctionManager auctionManager = new AuctionManager(transactionQueue, bidQueue, sellQueue);
        auctionManager.addTimeSlots(timeSlot);

        //act
        // Use reflection to access the private processQueues() method
        Method processQueuesMethod = null;
        try {
            processQueuesMethod = AuctionManager.class.getDeclaredMethod("processQueues");
            processQueuesMethod.setAccessible(true); // Set the method accessible

            // Invoke the private processQueues() method
            processQueuesMethod.invoke(auctionManager);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        //assert
        Assertions.assertEquals(1, auctionManager.getAuctions().size());
    }
}
