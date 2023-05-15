package msExchange.timeSlotManagement.auctionManagement;

import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Auction extends Thread {
    private UUID auctionID;
    private Bid bidPosition;
    private Sell sellPosition;
    private double pricePerKWh;
    private double volume;
    private boolean auctionEnded = false;
    private BlockingQueue<Transaction> transactionQueue;

    public Auction(UUID auctionID, Sell sellPosition, double askPrice, double volume, BlockingQueue<Transaction> transactionQueue) {
        this.auctionID = auctionID;
        this.sellPosition = sellPosition;
        this.pricePerKWh = askPrice;
        this.volume = volume;
        this.transactionQueue = transactionQueue;
    }

    public void setBid(Bid bidPosition) {
        if (!auctionEnded) {
            if (this.bidPosition != null) {
                if (bidPosition.getPrice() > this.bidPosition.getPrice()) {
                    this.bidPosition = bidPosition;
                }
            } else {
                this.bidPosition = bidPosition;
            }
        }

    }

    public void endAuction() {
        //Create a transaction and add it to blockingQueue
        auctionEnded = true;
        Transaction transaction = new Transaction(sellPosition.getSellerID(), bidPosition.getBidderID(), volume, bidPosition.getPrice());

        try {
            transactionQueue.put(transaction);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAuctionEnded() {
        return this.auctionEnded;
    }

    public double getPrice() {
        return pricePerKWh;
    }

    public UUID getAuctionId() {
        return auctionID;
    }

    public Bid getBidPosition() {
        return this.bidPosition;
    }

    public double getVolume() {
        return this.volume;
    }
}
