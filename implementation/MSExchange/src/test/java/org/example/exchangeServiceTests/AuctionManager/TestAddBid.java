package org.example.exchangeServiceTests.AuctionManager;

import msExchange.Exceptions.AuctionNotFoundException;
import msExchange.auctionManagement.Auction;
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

public class TestAddBid {

    @Test
    public void givenBidInBidQueue_processQueues_expectedAuctionWithNewHighestBidder() {
        //arrage
        BlockingQueue<Sell> sellQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();

        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(4));
        List<TimeSlot> timeSlotList = new ArrayList<>();
        timeSlotList.add(timeSlot);
        UUID slotID = timeSlot.getTimeSlotID();


        Sell sell = new Sell(12, 12, slotID);
        UUID auctionUUID = UUID.randomUUID();
        sell.setAuctionID(auctionUUID);

        UUID bidderUUID = UUID.randomUUID();
        Bid bid = new Bid(12, 13, slotID, bidderUUID);


        bid.setAuctionID(auctionUUID);

        try {
            sellQueue.put(sell);
            bidQueue.put(bid);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AuctionManager auctionManager = new AuctionManager(transactionQueue, bidQueue, sellQueue);
        auctionManager.addTimeSlots(timeSlotList);

        //act
        // Use reflection to access the private processQueues() method
        Method processQueuesMethod = null;
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println(i);
                processQueuesMethod = AuctionManager.class.getDeclaredMethod("processQueues");
                processQueuesMethod.setAccessible(true); // Set the method accessible

                // Invoke the private processQueues() method
                processQueuesMethod.invoke(auctionManager);
            }


        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //assert
        Auction auction = auctionManager.getAuctions().get(auctionUUID);
        UUID bidderID = auction.getBidderID();

        Assertions.assertEquals(bidderUUID, bidderID);
        Assertions.assertTrue(auction.getVolume() <= auction.getVolume());

    }

    @Test
    public void given3BidsArrivingInDifferentOrder_addBid_expectedBidWithHighestPriceAsBidder() {
        //arrage
        BlockingQueue<Sell> sellQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();

        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(4));
        List<TimeSlot> timeSlotList = new ArrayList<>();
        timeSlotList.add(timeSlot);
        UUID slotID = timeSlot.getTimeSlotID();

        Sell sell = new Sell(12, 9, slotID);
        UUID auctionUUID = UUID.randomUUID();
        sell.setAuctionID(auctionUUID);

        UUID bidderUUID1 = UUID.randomUUID();
        Bid bid1 = new Bid(12, 13, slotID, bidderUUID1);

        UUID bidderUUID2 = UUID.randomUUID();
        Bid bid2 = new Bid(12, 15, slotID, bidderUUID2);

        UUID bidderUUID3 = UUID.randomUUID();
        Bid bid3 = new Bid(12, 10, slotID, bidderUUID3);


        bid1.setAuctionID(auctionUUID);
        bid2.setAuctionID(auctionUUID);
        bid3.setAuctionID(auctionUUID);

        try {
            sellQueue.put(sell);
            bidQueue.put(bid1);
            bidQueue.put(bid2);
            bidQueue.put(bid3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AuctionManager auctionManager = new AuctionManager(transactionQueue, bidQueue, sellQueue);
        auctionManager.addTimeSlots(timeSlotList);

        //act
        // Use reflection to access the private processQueues() method
        Method processQueuesMethod = null;
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                processQueuesMethod = AuctionManager.class.getDeclaredMethod("processQueues");
                processQueuesMethod.setAccessible(true); // Set the method accessible

                // Invoke the private processQueues() method
                processQueuesMethod.invoke(auctionManager);
            }


        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //assert
        Auction auction = auctionManager.getAuctions().get(auctionUUID);

        UUID bidderID = auction.getBidderID();

        Assertions.assertEquals(bidderUUID2, bidderID);
        Assertions.assertTrue(auction.getVolume() <= auction.getVolume());

    }

    @Test
    public void givenBidWithoutAuctionID_addBid_expectedAuctionNotFoundException() {
        //arrage
        BlockingQueue<Sell> sellQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(4));
        List<TimeSlot> timeSlotList = new ArrayList<>();
        timeSlotList.add(timeSlot);
        UUID slotID = timeSlot.getTimeSlotID();

        Sell sell = new Sell(12, 9, slotID);
        UUID auctionUUID = UUID.randomUUID();
        sell.setAuctionID(auctionUUID);

        Bid bid1 = new Bid(12, 13, slotID, UUID.randomUUID());
        UUID bidderUUID1 = bid1.getBidderID();

        try {
            sellQueue.put(sell);
            bidQueue.put(bid1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AuctionManager auctionManager = new AuctionManager(transactionQueue, bidQueue, sellQueue);
        auctionManager.addTimeSlots(timeSlotList);

        //act
        // Use reflection to access the private processQueues() method
        Method processQueuesMethod = null;
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                processQueuesMethod = AuctionManager.class.getDeclaredMethod("processQueues");
                processQueuesMethod.setAccessible(true); // Set the method accessible

                // Invoke the private processQueues() method
                processQueuesMethod.invoke(auctionManager);

                //assert
                Assertions.fail("Expected AuctionNotFoundException, but no exception was thrown.");
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();

            Assertions.assertTrue(targetException instanceof AuctionNotFoundException, "Expected AuctionNotFoundException, but different exception was thrown.");
        }


    }
}
