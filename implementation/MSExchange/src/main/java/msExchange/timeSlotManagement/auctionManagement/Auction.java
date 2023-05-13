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

    public Auction(UUID auctionID, Sell sellPosition, double askPrice, double volume, BlockingQueue<Transaction> transactionQueue) {
        this.auctionID = auctionID;
        this.sellPosition = sellPosition;
        this.pricePerKWh = askPrice;
        this.volume = volume;
    }

    public void setBid(Bid bidPosition) {
    }

    public double getPrice() {
        return pricePerKWh;
    }

    public UUID getAuctionId() {
        return auctionID;
    }


    public void endAuction() {
        //Create a transaction and add it to blockingQueue

        auctionEnded = true;
    }

    public boolean isAuctionEnded() {
        return auctionEnded;
    }
}
