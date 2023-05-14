package msExchange.timeSlotManagement.auctionManagement;

import msExchange.Exceptions.AuctionNotFoundException;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class AuctionManager implements Runnable {
    Logger logger = Logger.getLogger(getClass().getName());
    private Map<UUID, Auction> auctions;
    private Date lastAuctionAdded;
    private BlockingQueue<Transaction> transactionQueue;
    private BlockingQueue<Bid> bidQueue;
    private BlockingQueue<Sell> sellQueue;

    public AuctionManager(final BlockingQueue<Transaction> transactionQueue, final BlockingQueue<Bid> bidQueue, final BlockingQueue<Sell> sellQueue) {
        this.auctions = new HashMap<>();
        this.lastAuctionAdded = new Date();
        this.transactionQueue = transactionQueue;
        this.bidQueue = bidQueue;
        this.sellQueue = sellQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                processQueues();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
                Thread.currentThread().interrupt();
            } catch (AuctionNotFoundException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private void processQueues() throws AuctionNotFoundException {
        //Process bidQueue
        Bid bid = bidQueue.poll(); // Non-blocking call, retrieves the next bid or null if empty
        if (bid != null) {
            Optional<UUID> auctionID = bid.getAuctionID();
            if (auctionID.isEmpty()) {
                throw new AuctionNotFoundException("The Auction ID is missing");
            }
            addBidToAuction(auctionID.get(), bid);
        }

        //Process sellQueue
        Sell sell = sellQueue.poll(); // Non-blocking call, retrieves the next sell or null if empty
        if (sell != null) {
            logger.info("Creating new Auction...");
            addNewAuction(sell);
        }

        //Check for ended auctions
        Date currentTime = new Date();
        for (Auction auction : auctions.values()) {
            if (auction.isAuctionEnded()) {
                //Remove auction
                auctions.remove(auction.getAuctionId());
            }
        }

    }

    private void addNewAuction(Sell sell) {
        UUID uuid = UUID.randomUUID();
        Auction auction = new Auction(uuid, sell, sell.getAskPrice(), sell.getVolume(), transactionQueue);
        auctions.put(uuid, auction);
        logger.info("Auction has been added: " + auctions.get(uuid));
    }

    private void addBidToAuction(UUID auctionID, Bid bid) {
        Auction auction = auctions.get(auctionID);
        auction.setBid(bid);
    }

    public void endAllAuctions() {
        for (Auction auction : auctions.values()) {
            auction.endAuction();
        }
    }

    public Map<UUID, Auction> getAuctions() {
        return auctions;
    }

    public Date getLastAuctionTime() {
        return null;
    }

}
