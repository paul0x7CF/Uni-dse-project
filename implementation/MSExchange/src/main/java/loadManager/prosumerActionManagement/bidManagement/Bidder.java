package loadManager.prosumerActionManagement.bidManagement;

import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.AuctionProsumerTracker;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.Bid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bidder {
    private static final Logger logger = LogManager.getLogger(Bidder.class);
    private final int MAX_THREAD_POOLS;
    //key -> slotID; each slot has its own auction finding algorithm
    private Map<UUID, AuctionFindingAlgorithm> auctionFinderPerSlot = new HashMap<>();
    private UUID bidderID;
    private BlockingQueue<MessageContent> outgoingQueue;
    private AuctionManager auctionManager;
    private AuctionProsumerTracker auctionProsumerTracker;
    private ExecutorService executorService;

    public Bidder(AuctionManager auctionManager, UUID bidderID, BlockingQueue<MessageContent> outogingMessage, AuctionProsumerTracker auctionProsumerTracker) {
        this.auctionManager = auctionManager;
        this.bidderID = bidderID;
        this.outgoingQueue = outogingMessage;
        this.auctionProsumerTracker = auctionProsumerTracker;

        PropertyFileReader propertyFileReader = new PropertyFileReader();
        MAX_THREAD_POOLS = Integer.parseInt(propertyFileReader.getMaxAuctionFindingAlgorithm());

        //Create a thread pool with a fixed number of threads
        executorService = Executors.newFixedThreadPool(MAX_THREAD_POOLS);
    }

    public void handleBid(Bid bid) {
        //only valid bids lend here
        prepareAuctionFinder(bid);
    }

    private void prepareAuctionFinder(Bid bid) {
        //TODO: add logic to reuse thread space

        if (auctionFinderPerSlot.containsKey(bid.getTimeSlot())) {
            //if there is an auction finding algorithm for this slot, ignore the new bid
            return;
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
