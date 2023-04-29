package loadManager.prosumerActionManagement.bidManagement;

import loadManager.auctionManagement.AuctionManager;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import protocol.Message;
import sendable.Bid;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Bidder {
    private Map<UUID, AuctionFindingAlgorithm> auctionFinderPerSlot;
    private UUID bidderID;
    private BlockingQueue<Message> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;

    public Bidder(AuctionManager auctionManager, UUID bidderID, BlockingQueue<Message> outogingMessage, AuctionProsumerTracker auctionProsumerTracker) {
        this.auctionManager = auctionManager;
        this.bidderID = bidderID;
        this.outgoingQueue = outogingMessage;
        this.auctionProsumerTracker = auctionProsumerTracker;
    }

    public void handleBid(Bid bid) {
    }
}
