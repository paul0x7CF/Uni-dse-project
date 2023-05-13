package msExchange.timeSlotManagement.auctionManagement;

import msExchange.Exceptions.AuctionNotFoundException;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class AuctionManager implements Runnable {
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
            // Process bidQueue and sellQueue, handle auctions, etc.
            try {
                //Process bidQueue
                Bid bid = bidQueue.poll(); // Non-blocking call, retrieves the next bid or null if empty
                if (bid != null) {
                    Optional<UUID> auctionID = bid.getAuctionID();
                    if (auctionID.isEmpty()) {
                        throw new AuctionNotFoundException("The Auction ID was missing");
                    }
                    addBidToAuction(auctionID.get(), bid);
                }

                //Process sellQueue
                Sell sell = sellQueue.poll(); // Non-blocking call, retrieves the next sell or null if empty
                if (sell != null) {
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

                Thread.sleep(1000); //  // Sleep for a certain duration before processing the next iteration
            } catch (AuctionNotFoundException | InterruptedException e) {
                e.getMessage();
            }


        }
    }

    private void addNewAuction(Sell sell) {
        UUID uuid = UUID.randomUUID();
        Auction auction = new Auction(uuid, sell, sell.getAskPrice(), sell.getVolume(), transactionQueue);
        auctions.put(uuid, auction);
    }

    private void addBidToAuction(UUID auctionID, Bid bid) {
        Auction auction = auctions.get(auctionID);
        auction.setBid(bid);
    }

    public Auction getAuction(UUID auctionID) {
        return auctions.get(auctionID);
    }

    public void endAllAuctions() {
        for (Auction auction : auctions.values()) {
            auction.endAuction();
        }
    }

    public Map<UUID, Double> getAuctions() {
        return null;
    }

    public Date getLastAuctionTime() {
        return null;
    }

}
