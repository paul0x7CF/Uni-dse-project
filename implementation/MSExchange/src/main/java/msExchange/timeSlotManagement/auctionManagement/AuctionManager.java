package msExchange.timeSlotManagement.auctionManagement;

import loadManager.auctionManagement.Auction;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class AuctionManager implements Runnable {
    private Map<UUID, Auction> auctions;
    private Date lastAuctionAdded;
    private BlockingQueue<Transaction> transactionQueue;
    private BlockingQueue<Bid> bidQueue;
    private BlockingQueue<Sell> sellQueue;

    public AuctionManager(final BlockingQueue<Transaction> transactionQueue, final BlockingQueue<Bid> bidQueue, final BlockingQueue<Sell> sellQueue) {
        this.transactionQueue = transactionQueue;
        this.bidQueue = bidQueue;
        this.sellQueue = sellQueue;
    }

    @Override
    public void run() {

    }

    public Auction getAuction(UUID auctionID) {
        return null;
    }

    public void endAllAuctions() {
    }

    public Map<UUID, Double> getAuctions() {
        return null;
    }

    public Date getLastAuctionTime() {
        return null;
    }

    private void addToTransactionQueue(Transaction transaction) {
    }


}
