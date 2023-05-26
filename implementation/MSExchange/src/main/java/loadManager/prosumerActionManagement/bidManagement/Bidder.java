package loadManager.prosumerActionManagement.bidManagement;

import loadManager.auctionManagement.AuctionManager;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import protocol.Message;
import sendable.Bid;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bidder {
    //key -> slotID; each slot has its own auction finding algorithm
    private Map<UUID, AuctionFindingAlgorithm> auctionFinderPerSlot;
    private UUID bidderID;
    private int maxThreadPools;
    private BlockingQueue<Message> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;
    private ExecutorService executorService;

    public Bidder(AuctionManager auctionManager, UUID bidderID, BlockingQueue<Message> outogingMessage, AuctionProsumerTracker auctionProsumerTracker) {
        this.auctionManager = auctionManager;
        this.bidderID = bidderID;
        this.outgoingQueue = outogingMessage;
        this.auctionProsumerTracker = auctionProsumerTracker;

        readProperties();
        //Create a thread pool with a fixed number of threads
        executorService = Executors.newFixedThreadPool(maxThreadPools);
    }

    private void readProperties() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("src/main/java/config.properties");
            properties.load(configFile);
            configFile.close();

            maxThreadPools = Integer.parseInt(properties.getProperty("prosumer.maxAuctionFindingAlgorithm"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBid(Bid bid) {
        //only valid bids lend here
        prepareAuctionFinder(bid);

    }

    private void prepareAuctionFinder(Bid bid) {
        //TODO: add logic to reuse thread space

        //if there is an auction finding algorithm for this slot, replace old bid
        if (auctionFinderPerSlot.containsKey(bid.getTimeSlot())) {
            AuctionFindingAlgorithm auctionFindingAlgorithm = auctionFinderPerSlot.get(bid.getTimeSlot());
            auctionFindingAlgorithm.replaceBid(bid);
        } else {
            //if there is no auction finding algorithm for this slot, create one
            AuctionFindingAlgorithm auctionFindingAlgorithm = new AuctionFindingAlgorithm(bid, auctionManager, outgoingQueue, auctionProsumerTracker);
            auctionFinderPerSlot.put(bid.getTimeSlot(), auctionFindingAlgorithm);

            //Create and execute a new thread for the algorithm
            executorService.execute(auctionFindingAlgorithm);
        }
    }

    public UUID getBidderID() {
        return bidderID;
    }
}
