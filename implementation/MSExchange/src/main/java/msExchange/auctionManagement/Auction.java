package msExchange.auctionManagement;

import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Auction {
    private UUID auctionID;
    private UUID bidderID;
    private UUID sellerID;
    private UUID timeSlotID;
    private double pricePerKWh;
    private double volume;
    private boolean auctionEnded = false;
    private BlockingQueue<Transaction> transactionQueue;

    public Auction(UUID auctionID, Sell sellPosition, BlockingQueue<Transaction> transactionQueue) {
        this.auctionID = auctionID;
        this.sellerID = sellPosition.getSellerID();
        this.pricePerKWh = sellPosition.getAskPrice();
        this.volume = sellPosition.getVolume();
        this.transactionQueue = transactionQueue;
        this.timeSlotID = sellPosition.getTimeSlot();
    }

    public void setBid(Bid bidPosition) {
        if (!auctionEnded) {
            if (this.bidderID != null) {
                if (bidPosition.getPrice() > pricePerKWh) {
                    this.pricePerKWh = bidPosition.getPrice();
                    this.bidderID = bidPosition.getBidderID();
                }
            } else {
                this.pricePerKWh = bidPosition.getPrice();
                this.bidderID = bidPosition.getBidderID();
            }
        }
    }

    public void endAuction() {
        //Create a transaction and add it to blockingQueue
        auctionEnded = true;
        Transaction transaction = new Transaction(sellerID, bidderID, volume, pricePerKWh, auctionID);

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

    public UUID getBidderID() {
        return bidderID;
    }

    public double getVolume() {
        return this.volume;
    }
}
