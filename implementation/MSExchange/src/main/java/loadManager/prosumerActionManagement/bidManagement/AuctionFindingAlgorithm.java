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
    private Bid bid;
    private BlockingQueue<Message> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;

    public AuctionFindingAlgorithm(Bid bid, AuctionManager auctionManager, BlockingQueue<Message> outgoingQueue, AuctionProsumerTracker auctionProsumerTracker) {
        this.bid = bid;
        this.auctionManager = auctionManager;
        this.outgoingQueue = outgoingQueue;
        this.auctionProsumerTracker = auctionProsumerTracker;
    }

    @Override
    public void run() {
        while (true) {

        }
    }

    public List<Auction> getBiddersAuctions() {
        return auctionManager.getBiddersAuctions(bid.getBidderID());
    }

    private synchronized void addProsumerToAuction(UUID auctionID, UUID prosumerID) {


    }

    //if prosumer sends new bid, replace old bid
    public synchronized void replaceBid(Bid bid) {
        this.bid = bid;
    }

}