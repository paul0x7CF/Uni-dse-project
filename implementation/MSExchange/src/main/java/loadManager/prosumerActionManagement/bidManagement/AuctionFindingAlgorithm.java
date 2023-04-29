package loadManager.prosumerActionManagement.bidManagement;

import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import protocol.Message;
import sendable.Bid;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class AuctionFindingAlgorithm implements Runnable {
    private List<UUID> auctionParticipation;
    private Bid bid;
    private AuctionManager auctionManager;
    private BlockingQueue<Message> outgoingQueue;
    private AuctionProsumerTracker auctionProsumerTracker;

    public AuctionFindingAlgorithm(final List<UUID> auctionParticipation, final Bid bid, final AuctionManager auctionManager, final BlockingQueue<Message> outgoingQueue, final AuctionProsumerTracker auctionProsumerTracker) {
        this.auctionParticipation = auctionParticipation;
        this.bid = bid;
        this.auctionManager = auctionManager;
        this.outgoingQueue = outgoingQueue;
        this.auctionProsumerTracker = auctionProsumerTracker;
    }

    @Override
    public void run() {

    }

    public List<Auction> getBiddersAuctions() {
        return null;
    }

    private synchronized void addProsumerToAuction(UUID auctionID, UUID prosumerID) {
    }


}