package loadManager.auctionManagement;

import Exceptions.CommandNotPossibleException;
import loadManager.SellInformation;
import sendable.Bid;
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
    private UUID exchangeID;

    public Auction(UUID auctionID, SellInformation sellPosition) {
        this.auctionID = auctionID;
        this.sellerID = sellPosition.getSell().getSellerID();
        this.pricePerKWh = sellPosition.getSell().getAskPrice();
        this.volume = sellPosition.getSell().getVolume();
        this.timeSlotID = sellPosition.getSell().getTimeSlot();
        this.exchangeID = sellPosition.getExchangeID();
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
