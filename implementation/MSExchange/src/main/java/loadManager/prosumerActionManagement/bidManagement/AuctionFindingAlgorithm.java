package loadManager.prosumerActionManagement.bidManagement;

import Exceptions.CommandNotPossibleException;
import Exceptions.InvalidTimeSlotException;
import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.Bid;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class AuctionFindingAlgorithm implements Runnable {
    private static final Logger logger = LogManager.getLogger(AuctionFindingAlgorithm.class);
    private final Object lock = new Object(); //Create a lock object
    private BidForTimeSlot bidForTimeSlot;
    private BlockingQueue<MessageContent> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;

    public AuctionFindingAlgorithm(Bid incomingBid, AuctionManager auctionManager, BlockingQueue<MessageContent> outgoingQueue, AuctionProsumerTracker auctionProsumerTracker) {
        bidForTimeSlot = new BidForTimeSlot(incomingBid);
        this.auctionManager = auctionManager;
        this.outgoingQueue = outgoingQueue;
        this.auctionProsumerTracker = auctionProsumerTracker;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                List<UUID> auctions = auctionProsumerTracker.getFirstInAuction(bidForTimeSlot.getIncomingBid().getBidderID(), bidForTimeSlot.getIncomingBid().getTimeSlot());
                bidForTimeSlot.updateBids(auctions);
                double coveredVolume = bidForTimeSlot.getCoveredVolume();
                if (coveredVolume != bidForTimeSlot.getIncomingBid().getVolume()) {
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
    }

    private void findAuctionsToCoverVolume(double remainingVolume, List<Auction> auctions) throws CommandNotPossibleException, InvalidTimeSlotException {
        //TODO: find auctions to cover volume


    }

    private synchronized void addProsumerToAuction(UUID auctionID, UUID prosumerID) {
        //TODO: add prosumer to auction


    }

}