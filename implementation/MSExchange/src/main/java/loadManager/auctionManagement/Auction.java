package loadManager.auctionManagement;

import loadManager.Exceptions.CommandNotPossibleException;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.UUID;

public class Auction {
    private UUID auctionID;
    private UUID bidderID;
    private UUID sellerID;
    private UUID timeSlotID;
    private double pricePerKWh;
    private double volume;
    private boolean auctionEnded = false;

    public Auction(UUID auctionID, Sell sellPosition) {
        this.auctionID = auctionID;
        this.sellerID = sellPosition.getSellerID();
        this.pricePerKWh = sellPosition.getAskPrice();
        this.volume = sellPosition.getVolume();
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
        auctionEnded = true;
    }

    public Transaction getTransaction() throws CommandNotPossibleException {
        if (!auctionEnded) {
            throw new CommandNotPossibleException("Auction has not ended yet");
        }
        Transaction transaction = new Transaction(sellerID, bidderID, volume, pricePerKWh, auctionID);

        return transaction;

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

    public UUID getSellerID() {
        return this.sellerID;
    }

    public UUID getTimeSlotID() {
        return this.timeSlotID;
    }
}
