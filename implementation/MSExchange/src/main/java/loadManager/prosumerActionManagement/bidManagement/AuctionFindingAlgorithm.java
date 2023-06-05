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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class AuctionFindingAlgorithm implements Runnable {
    private static final Logger logger = LogManager.getLogger(AuctionFindingAlgorithm.class);
    private final Object lock = new Object(); //Create a lock object
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
            synchronized (lock) {
                List<UUID> auctions = auctionProsumerTracker.getFirstInAuction(bid.getBidderID(), bid.getTimeSlot());
                double coveredVolume = auctionManager.coveredVolume(auctions);
                if (coveredVolume != bid.getVolume()) {
                    try {
                        findAuctionsToCoverVolume(coveredVolume, auctionManager.getAuctions(auctions));
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

    private void findAuctionsToCoverVolume(double coveredVolume, List<Auction> auctions) throws CommandNotPossibleException, InvalidTimeSlotException {
        if (coveredVolume > bid.getVolume()) {
            throw new CommandNotPossibleException("coveredVolume is bigger than bid volume");
        }
        List<Auction> remainingAuctions = new ArrayList<>();
        for (Auction eachAuction : auctionManager.getAllAuctionsForSlot(bid.getTimeSlot())) {
            if (!auctions.contains(eachAuction)) {
                remainingAuctions.add(eachAuction);
            }
        }

        remainingAuctions.sort(Comparator.comparing(Auction::getPrice).reversed());
        double remainingVolume = bid.getVolume() - coveredVolume;

        for(Auction eachAuction : remainingAuctions) {
            if (eachAuction.getPrice() < bid.getPrice()) {
                if(remainingVolume<= eachAuction.getVolume()){
                    //bid can be covered totally

                }
            } else {
                break;
            }
        }

    }

    private synchronized void addProsumerToAuction(UUID auctionID, UUID prosumerID) {
        //TODO: add prosumer to auction

    }

    //if prosumer sends new bid, replace old bid
    public synchronized void replaceBid(Bid bid) {
        this.bid = bid;
    }

}