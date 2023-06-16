package loadManager.prosumerActionManagement.bidManagement;

import CF.sendable.Bid;
import CF.sendable.ISendable;
import MSP.Exceptions.CommandNotPossibleException;
import MSP.Exceptions.InvalidBidException;
import MSP.Exceptions.InvalidTimeSlotException;
import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.EBuildCategory;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class AuctionFindingAlgorithm implements Runnable {
    private static final Logger logger = LogManager.getLogger(AuctionFindingAlgorithm.class);
    private final Object lock = new Object(); //Create a lock object
    private BidForTimeSlot bidForTimeSlot;
    private BlockingQueue<MessageContent> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;
    private boolean shouldContinue = true;
    private boolean timeSlotIsOpen = true;

    public AuctionFindingAlgorithm(Bid incomingBid, AuctionManager auctionManager, BlockingQueue<MessageContent> outgoingQueue, AuctionProsumerTracker auctionProsumerTracker) {
        bidForTimeSlot = new BidForTimeSlot(incomingBid);
        this.auctionManager = auctionManager;
        this.outgoingQueue = outgoingQueue;
        this.auctionProsumerTracker = auctionProsumerTracker;
    }

    @Override
    public void run() {

        while (timeSlotIsOpen) {
            synchronized (lock) {
                double coveredVolume = bidForTimeSlot.getCoveredVolume();
                if (coveredVolume != bidForTimeSlot.getIncomingBid().getVolume()) {
                    List<UUID> auctions = auctionProsumerTracker.getFirstInAuction(bidForTimeSlot.getIncomingBid().getBidderID(), bidForTimeSlot.getIncomingBid().getTimeSlot());
                    bidForTimeSlot.updateBids(auctions);
                    try {
                        findAuctionsToCoverVolume(bidForTimeSlot.getIncomingBid().getVolume() - coveredVolume, auctionManager.getAuctions(auctions));
                    } catch (CommandNotPossibleException | InvalidTimeSlotException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        lock.wait(2000); //Release the lock and wait
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


        double volumeInAuctions = getVolumeInAuctions();
        logger.info("The volume in auctions is " + volumeInAuctions);
        double notCoveredVolume = bidForTimeSlot.getIncomingBid().getVolume() - volumeInAuctions;
        if (notCoveredVolume != 0.0) {
            createMessageContent(new Bid(notCoveredVolume, bidForTimeSlot.getIncomingBid().getPrice(), bidForTimeSlot.getIncomingBid().getTimeSlot(), bidForTimeSlot.getIncomingBid().getBidderID()), EBuildCategory.BidToStorage);
            logger.info("Part of the bid with volume " + notCoveredVolume + " was sent to storage");

            while (!timeSlotIsOpen && shouldContinue) {
                synchronized (lock) {
                    Map<UUID, UUID> wonAuctions = auctionProsumerTracker.getWonAuctions(bidForTimeSlot.getIncomingBid().getTimeSlot());
                    Iterator<Bid> iterator = bidForTimeSlot.getBidsInAuctions().iterator();

                    while (iterator.hasNext()) {
                        Bid bidInAuction = iterator.next();
                        if (wonAuctions.containsKey(bidInAuction.getAuctionID()) && !wonAuctions.containsValue(bidInAuction.getBidderID())) {
                            volumeInAuctions -= bidInAuction.getVolume();
                            createMessageContent(new Bid(bidInAuction.getVolume(), bidInAuction.getPrice(), bidInAuction.getTimeSlot(), bidInAuction.getBidderID()), EBuildCategory.BidToStorage);
                            iterator.remove(); // Remove the Bid from the list
                            logger.info("Part of the bid with volume " + bidInAuction.getVolume() + " has been sent to storage");
                        }
                    }

                    if (volumeInAuctions == 0) {
                        shouldContinue = false;
                    }

                    try {
                        lock.wait(2000); //Release the lock and wait
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    private void createMessageContent(ISendable sendable, EBuildCategory buildCategory) {
        MessageContent messageContent = new MessageContent(sendable, buildCategory);
        try {
            outgoingQueue.put(messageContent);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private double getVolumeInAuctions() {
        double volume = 0;
        for (Bid bidInAuction : bidForTimeSlot.getBidsInAuctions()) {
            volume += bidInAuction.getVolume();
        }
        return volume;
    }

    private void findAuctionsToCoverVolume(double remainingVolume, List<Auction> winningAuctions) throws CommandNotPossibleException, InvalidTimeSlotException {
        List<Auction> allAuctions = auctionManager.getAllAuctionsForSlot(bidForTimeSlot.getIncomingBid().getTimeSlot());
        if(!allAuctions.isEmpty()){
            logger.debug("Auctions in TimeSlot: " + allAuctions.size());
        }

        for (Auction auction : allAuctions) {

            if (!winningAuctions.contains(auction)) {
                double volume = auction.getTotalVolume() < remainingVolume ? auction.getTotalVolume() : remainingVolume;

                if (auction.getPrice() * auction.getCoveredVolume() < bidForTimeSlot.getIncomingBid().getPrice() * volume) {
                    logger.trace("in Auction finder, found auction to cover volume: " + volume);
                    Bid newBid = new Bid(volume, bidForTimeSlot.getIncomingBid().getPrice(), bidForTimeSlot.getIncomingBid().getTimeSlot(), bidForTimeSlot.getIncomingBid().getBidderID());
                    newBid.setAuctionID(auction.getAuctionId());
                    try {
                        auctionManager.setBidder(auction.getAuctionId(), newBid);
                    } catch (InvalidBidException e) {
                        throw new RuntimeException(e);
                    }
                    auctionProsumerTracker.addBidderToAuction(auction.getAuctionId(), bidForTimeSlot.getIncomingBid().getBidderID());
                    bidForTimeSlot.addBid(newBid);
                    logger.info("Added bid to bidForTimeSlot: " + newBid.getVolume());
                    remainingVolume -= volume;

                    EBuildCategory bidToExchange = EBuildCategory.BidToExchange;
                    bidToExchange.setUUID(auction.getExchangeID());
                    MessageContent messageContent = new MessageContent(newBid, bidToExchange);

                    try {
                        outgoingQueue.put(messageContent);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void endAuctionFinder() {
        logger.debug("Auction Finder is ending");
        synchronized (lock) {
            timeSlotIsOpen = false;
            lock.notify();
        }
    }
}