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

            Bid newBid = new Bid(nextVolume, 1.5, newAuction.getTimeSlotID(), UUID.randomUUID());
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
                Assertions.assertEquals(auction.getTotalVolume(), auction.getCoveredVolume());
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

            Bid newBid = new Bid(nextVolume, 1.5, newAuction.getTimeSlotID(), UUID.randomUUID());
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

            if (i == 1 ) {
                Assertions.assertEquals(50, auction.getCoveredVolume());
            } else if(i==2){
                Assertions.assertEquals(75, auction.getCoveredVolume());

            }else{
                Assertions.assertEquals(auction.getTotalVolume(), auction.getCoveredVolume());
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


}
