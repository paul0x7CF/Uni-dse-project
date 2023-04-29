package msExchange.timeSlotManagement.auctionManagement;

import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.UUID;

public class Auction extends Thread {
    private UUID auctionId;
    private Bid bidPosition;
    private Sell sellPosition;
    private double pricePerKWh;
    private double volume;

    public Auction(Sell sellPosition, double askPrice, double volume) {
    }

    public void setBid(Bid bidPosition) {
    }

    public double getPrice() {
        return pricePerKWh;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public Transaction getTransaction() {
        return null;
    }
}
