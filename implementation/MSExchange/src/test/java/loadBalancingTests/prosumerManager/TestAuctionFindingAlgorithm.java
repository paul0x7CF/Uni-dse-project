package loadBalancingTests.prosumerManager;

import CF.sendable.Bid;
import CF.sendable.Sell;
import MSP.Exceptions.InvalidBidException;
import MSP.Exceptions.InvalidTimeSlotException;
import loadManager.SellInformation;
import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import loadManager.prosumerActionManagement.bidManagement.AuctionFindingAlgorithm;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestAuctionFindingAlgorithm {

    @Test
    public void givenBid_findAuctionsToCoverVolume_expectedVolumeSet() throws InvalidTimeSlotException {
        //arrange
        AuctionManager auctionManager = new AuctionManager();
        AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();
        BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();
        Bid bid = new Bid(200, 2, UUID.randomUUID(), UUID.randomUUID());


        AuctionFindingAlgorithm auctionFinder = new AuctionFindingAlgorithm(bid, auctionManager, outgoingQueue, auctionProsumerTracker);

        int nextVolume = 25;
        for (int i = 0; i < 10; i++) {
            Sell sell = new Sell(100, 1, bid.getTimeSlot(), UUID.randomUUID());
            SellInformation sellInformation = new SellInformation(sell, UUID.randomUUID());

            Auction newAuction = new Auction(UUID.randomUUID(), sellInformation);
            auctionManager.addAuction(newAuction);
            auctionProsumerTracker.addAuction(bid.getTimeSlot(), newAuction.getAuctionId());

            Bid newBid = new Bid(nextVolume, 1.5, newAuction.getTIMESLOT_ID(), UUID.randomUUID());
            auctionProsumerTracker.addBidderToAuction(newAuction.getAuctionId(), newBid.getBidderID());
            try {
                auctionManager.setBidder(newAuction.getAuctionId(), newBid);
            } catch (InvalidBidException e) {
                throw new RuntimeException(e);
            }

            if (nextVolume < 100) {
                nextVolume += 25;
            }
        }

        //act
        Method findAlgorithm = null;
        try {
            findAlgorithm = AuctionFindingAlgorithm.class.getDeclaredMethod("findAuctionsToCoverVolume", double.class, List.class);
            findAlgorithm.setAccessible(true);

            findAlgorithm.invoke(auctionFinder, bid.getVolume(), new ArrayList<Auction>());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //assert
        List<UUID> auctionIds = auctionProsumerTracker.getFirstInAuction(bid.getBidderID(), bid.getTimeSlot());

        Assertions.assertEquals(2, auctionIds.size());

        int i = 0;
        for (Auction auction : auctionManager.getAllAuctionsForSlot(bid.getTimeSlot())) {
            System.out.println("Auction " + i + " has covered volume: " + auction.getCoveredVolume());

            if (i != 2) {
                Assertions.assertEquals(auction.getTOTAL_VOLUME(), auction.getCoveredVolume());
            } else {
                Assertions.assertEquals(75, auction.getCoveredVolume());
            }
            boolean found = false;

            if (i == 0 || i == 1) {
                for (UUID auctionId : auctionIds) {
                    if (auctionId.equals(auction.getAuctionId())) {
                        found = true;
                    }
                }

                Assertions.assertTrue(found);
            }
            i++;
        }


    }

    @Test
    public void givenBid_findAuctionsToCoverVolume_expectedNotAllCovered() throws InvalidTimeSlotException {
        //arrange
        AuctionManager auctionManager = new AuctionManager();
        AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();
        BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();
        Bid bid = new Bid(150, 2, UUID.randomUUID(), UUID.randomUUID());


        AuctionFindingAlgorithm auctionFinder = new AuctionFindingAlgorithm(bid, auctionManager, outgoingQueue, auctionProsumerTracker);

        int nextVolume = 25;
        for (int i = 0; i < 10; i++) {
            Sell sell = new Sell(100, 1, bid.getTimeSlot(), UUID.randomUUID());
            SellInformation sellInformation = new SellInformation(sell, UUID.randomUUID());

            Auction newAuction = new Auction(UUID.randomUUID(), sellInformation);
            auctionManager.addAuction(newAuction);
            auctionProsumerTracker.addAuction(bid.getTimeSlot(), newAuction.getAuctionId());

            Bid newBid = new Bid(nextVolume, 1.5, newAuction.getTIMESLOT_ID(), UUID.randomUUID());
            auctionProsumerTracker.addBidderToAuction(newAuction.getAuctionId(), newBid.getBidderID());
            try {
                auctionManager.setBidder(newAuction.getAuctionId(), newBid);
            } catch (InvalidBidException e) {
                throw new RuntimeException(e);
            }

            if (nextVolume < 100) {
                nextVolume += 25;
            }
        }

        //act
        Method findAlgorithm = null;
        try {
            findAlgorithm = AuctionFindingAlgorithm.class.getDeclaredMethod("findAuctionsToCoverVolume", double.class, List.class);
            findAlgorithm.setAccessible(true);

            findAlgorithm.invoke(auctionFinder, bid.getVolume(), new ArrayList<Auction>());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //assert
        List<UUID> auctionIds = auctionProsumerTracker.getFirstInAuction(bid.getBidderID(), bid.getTimeSlot());

        Assertions.assertEquals(2, auctionIds.size());

        int i = 0;
        for (Auction auction : auctionManager.getAllAuctionsForSlot(bid.getTimeSlot())) {
            System.out.println("Auction " + i + " has covered volume: " + auction.getCoveredVolume());

            if (i == 1) {
                Assertions.assertEquals(50, auction.getCoveredVolume());
            } else if (i == 2) {
                Assertions.assertEquals(75, auction.getCoveredVolume());

            } else {
                Assertions.assertEquals(auction.getTOTAL_VOLUME(), auction.getCoveredVolume());
            }
            boolean found = false;

            if (i == 0 || i == 1) {
                for (UUID auctionId : auctionIds) {
                    if (auctionId.equals(auction.getAuctionId())) {
                        found = true;
                    }
                }

                Assertions.assertTrue(found);
            }
            i++;
        }
    }

    //send rest to storage
    @Test
    public void givenAuctions1_run_expectedCorrectOutPutQueue() {
        //arrange
        AuctionManager auctionManager = new AuctionManager();
        AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();
        BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();

        UUID timeSlotID = UUID.randomUUID();
        List<UUID> timeSlotIDs = new ArrayList<>();
        timeSlotIDs.add(timeSlotID);
        auctionManager.addNewTimeSlots(timeSlotIDs);

        Bid bid = new Bid(150, 2, timeSlotID, UUID.randomUUID());
        System.out.println("My Bidder ID: " + bid.getBidderID() + " My TimeSlot: " + bid.getTimeSlot());
        AuctionFindingAlgorithm auctionFinder = new AuctionFindingAlgorithm(bid, auctionManager, outgoingQueue, auctionProsumerTracker);


        Auction auction1 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(100, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 1 ID: " + auction1.getAuctionId());
        Auction auction2 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(30, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 2 ID: " + auction2.getAuctionId());
        Auction auction3 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(100, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 3 ID: " + auction3.getAuctionId());
        Auction auction4 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(100, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 4 ID: " + auction4.getAuctionId());

        auctionManager.addAuction(auction1);
        auctionManager.addAuction(auction2);
        auctionManager.addAuction(auction3);
        auctionManager.addAuction(auction4);

        auctionProsumerTracker.addAuction(timeSlotID, auction1.getAuctionId());
        auctionProsumerTracker.addAuction(timeSlotID, auction2.getAuctionId());
        auctionProsumerTracker.addAuction(timeSlotID, auction3.getAuctionId());
        auctionProsumerTracker.addAuction(timeSlotID, auction4.getAuctionId());

        UUID bidder1 = UUID.randomUUID();
        UUID bidder2 = UUID.randomUUID();

        auctionProsumerTracker.addBidderToAuction(auction1.getAuctionId(), bidder1);
        auctionProsumerTracker.addBidderToAuction(auction2.getAuctionId(), bidder1);
        auctionProsumerTracker.addBidderToAuction(auction3.getAuctionId(), bidder2);
        auctionProsumerTracker.addBidderToAuction(auction4.getAuctionId(), bidder2);

        try {
            auctionManager.setBidder(auction1.getAuctionId(), new Bid(50, 1, timeSlotID, bidder1));
            auctionManager.setBidder(auction2.getAuctionId(), new Bid(30, 1, timeSlotID, bidder1));
            auctionManager.setBidder(auction3.getAuctionId(), new Bid(100, 2.5, timeSlotID, bidder2));
            auctionManager.setBidder(auction4.getAuctionId(), new Bid(100, 2.5, timeSlotID, bidder2));
        } catch (InvalidBidException e) {
            throw new RuntimeException(e);
        }

        //act
        Thread thread = new Thread(auctionFinder);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        auctionFinder.endAuctionFinder();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //assert
        Assertions.assertEquals(3, outgoingQueue.size());

        int i = 0;
        for (MessageContent messageContent : outgoingQueue) {
            try {
                MessageContent mC = outgoingQueue.take();
                System.out.println("Message Content BuildCategory: " + mC.getBuildCategory());
                Bid b = (Bid) mC.getContent();
                System.out.println("Bid volume: " + b.getVolume());
                System.out.println("Bid price: " + b.getPrice());
                System.out.println("Bid timeSlot: " + b.getTimeSlot());
                System.out.println("Bid bidderID: " + b.getBidderID());
                System.out.println("Bid auctionID: " + b.getAuctionID());
                if (i == 0) {
                    Assertions.assertEquals(100, b.getVolume());
                    Assertions.assertEquals(2, b.getPrice());
                    Assertions.assertEquals(timeSlotID, b.getTimeSlot());
                    Assertions.assertEquals(bid.getBidderID(), b.getBidderID());
                    Assertions.assertEquals(auction1.getAuctionId(), b.getAuctionID().get());
                }
                if (i == 1) {
                    Assertions.assertEquals(30, b.getVolume());
                    Assertions.assertEquals(2, b.getPrice());
                    Assertions.assertEquals(timeSlotID, b.getTimeSlot());
                    Assertions.assertEquals(bid.getBidderID(), b.getBidderID());
                    Assertions.assertEquals(auction2.getAuctionId(), b.getAuctionID().get());
                }
                if (i == 3) {
                    Assertions.assertEquals(20, b.getVolume());
                    Assertions.assertEquals(0, b.getPrice());
                    Assertions.assertEquals(timeSlotID, b.getTimeSlot());
                    Assertions.assertEquals(bid.getBidderID(), b.getBidderID());
                    Assertions.assertEquals(Optional.empty(), b.getAuctionID());
                }
                i++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //outbid
    @Test
    public void givenAuctions2_run_expectedCorrectOutPutQueue() throws InterruptedException {
        //arrange
        AuctionManager auctionManager = new AuctionManager();
        AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();
        BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();

        UUID timeSlotID = UUID.randomUUID();
        List<UUID> timeSlotIDs = new ArrayList<>();
        timeSlotIDs.add(timeSlotID);
        auctionManager.addNewTimeSlots(timeSlotIDs);

        Bid bid = new Bid(150, 2, timeSlotID, UUID.randomUUID());
        System.out.println("My Bidder ID: " + bid.getBidderID() + " My TimeSlot: " + bid.getTimeSlot());
        AuctionFindingAlgorithm auctionFinder = new AuctionFindingAlgorithm(bid, auctionManager, outgoingQueue, auctionProsumerTracker);


        Auction auction1 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(100, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 1 ID: " + auction1.getAuctionId());
        Auction auction2 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(30, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 2 ID: " + auction2.getAuctionId());
        Auction auction3 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(100, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 3 ID: " + auction3.getAuctionId());
        Auction auction4 = new Auction(UUID.randomUUID(), new SellInformation(new Sell(100, 1, timeSlotID, UUID.randomUUID()), UUID.randomUUID()));
        System.out.println("Auction 4 ID: " + auction4.getAuctionId());

        auctionManager.addAuction(auction1);
        auctionManager.addAuction(auction2);
        auctionManager.addAuction(auction3);
        auctionManager.addAuction(auction4);

        auctionProsumerTracker.addAuction(timeSlotID, auction1.getAuctionId());
        auctionProsumerTracker.addAuction(timeSlotID, auction2.getAuctionId());
        auctionProsumerTracker.addAuction(timeSlotID, auction3.getAuctionId());
        auctionProsumerTracker.addAuction(timeSlotID, auction4.getAuctionId());

        UUID bidder1 = UUID.randomUUID();
        UUID bidder2 = UUID.randomUUID();

        auctionProsumerTracker.addBidderToAuction(auction1.getAuctionId(), bidder1);
        auctionProsumerTracker.addBidderToAuction(auction2.getAuctionId(), bidder1);
        auctionProsumerTracker.addBidderToAuction(auction3.getAuctionId(), bidder2);
        auctionProsumerTracker.addBidderToAuction(auction4.getAuctionId(), bidder2);

        try {
            auctionManager.setBidder(auction1.getAuctionId(), new Bid(50, 1, timeSlotID, bidder1));
            auctionManager.setBidder(auction2.getAuctionId(), new Bid(30, 1, timeSlotID, bidder1));
            auctionManager.setBidder(auction3.getAuctionId(), new Bid(100, 2.5, timeSlotID, bidder2));
            auctionManager.setBidder(auction4.getAuctionId(), new Bid(100, 2.5, timeSlotID, bidder2));
        } catch (InvalidBidException e) {
            throw new RuntimeException(e);
        }

        //act
        Thread thread = new Thread(auctionFinder);
        thread.start();
        Thread.sleep(1000);

        auctionProsumerTracker.addBidderToAuction(auction1.getAuctionId(), bidder2);
        try {
            auctionManager.setBidder(auction1.getAuctionId(), new Bid(100, 2.5, timeSlotID, bidder2));
        } catch (InvalidBidException e) {
            throw new RuntimeException(e);
        }


        Thread.sleep(2000);


        auctionFinder.endAuctionFinder();

        Thread.sleep(1000);

        //assert
        Assertions.assertEquals(3, outgoingQueue.size());

        int i = 0;
        for (MessageContent messageContent : outgoingQueue) {
            try {
                MessageContent mC = outgoingQueue.take();
                System.out.println("Message Content BuildCategory: " + mC.getBuildCategory());
                Bid b = (Bid) mC.getContent();
                System.out.println("Bid volume: " + b.getVolume());
                System.out.println("Bid price: " + b.getPrice());
                System.out.println("Bid timeSlot: " + b.getTimeSlot());
                System.out.println("Bid bidderID: " + b.getBidderID());
                System.out.println("Bid auctionID: " + b.getAuctionID());
                if (i == 0) {
                    Assertions.assertEquals(100, b.getVolume());
                    Assertions.assertEquals(2, b.getPrice());
                    Assertions.assertEquals(timeSlotID, b.getTimeSlot());
                    Assertions.assertEquals(bid.getBidderID(), b.getBidderID());
                    Assertions.assertEquals(auction1.getAuctionId(), b.getAuctionID().get());
                }
                if (i == 1) {
                    Assertions.assertEquals(30, b.getVolume());
                    Assertions.assertEquals(2, b.getPrice());
                    Assertions.assertEquals(timeSlotID, b.getTimeSlot());
                    Assertions.assertEquals(bid.getBidderID(), b.getBidderID());
                    Assertions.assertEquals(auction2.getAuctionId(), b.getAuctionID().get());
                }
                if (i == 3) {
                    Assertions.assertEquals(120, b.getVolume());
                    Assertions.assertEquals(0, b.getPrice());
                    Assertions.assertEquals(timeSlotID, b.getTimeSlot());
                    Assertions.assertEquals(bid.getBidderID(), b.getBidderID());
                    Assertions.assertEquals(Optional.empty(), b.getAuctionID());
                }
                i++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
