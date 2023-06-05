package loadManager.prosumerActionManagement.bidManagement;

import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import sendable.Bid;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class AuctionFindingAlgorithm implements Runnable {
    private Bid bid;
    private BlockingQueue<MessageContent> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;

    public AuctionFindingAlgorithm(Bid bid, AuctionManager auctionManager, BlockingQueue<MessageContent> outgoingQueue, AuctionProsumerTracker auctionProsumerTracker) {
        this.bid = bid;
        this.auctionManager = auctionManager;
        this.outgoingQueue = outgoingQueue;
        this.auctionProsumerTracker = auctionProsumerTracker;
    }

    @Override
    public void run() {
        while (true) {
            if (auctionProsumerTracker.getFirstInAuction(bid.getBidderID(), bid.getTimeSlot()).isEmpty()) {
                //TODO: Algorithm
                findAuctionsToCoverVolume();
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void findAuctionsToCoverVolume() {
        List<Auction> bidderAuctions = getBiddersAuctions();

    }

    public List<Auction> getBiddersAuctions() {
        List<UUID> auctionIDs = auctionProsumerTracker.getBiddersAuctions(bid.getBidderID());
        return auctionManager.getAuctions(auctionIDs);
    }

    private synchronized void addProsumerToAuction(UUID auctionID, UUID prosumerID) {
        //TODO: add prosumer to auction

    }

    //if prosumer sends new bid, replace old bid
    public synchronized void replaceBid(Bid bid) {
        this.bid = bid;
    }

}