package loadManager.auctionManagement;

import CF.sendable.Bid;
import CF.sendable.Transaction;
import MSP.Exceptions.CommandNotPossibleException;
import loadManager.SellInformation;

import java.util.UUID;

public class Auction {
    private UUID auctionID;
    private UUID bidderID;
    private UUID sellerID;
    private UUID timeSlotID;
    private double pricePerKWh;
    private double totalVolume;
    private double coveredVolume = 0;
    private boolean auctionEnded = false;
    private UUID exchangeID;

    public Auction(UUID auctionID, SellInformation sellPosition) {
        this.auctionID = auctionID;
        this.sellerID = sellPosition.getSell().getSellerID();
        this.pricePerKWh = sellPosition.getSell().getAskPrice();
        this.totalVolume = sellPosition.getSell().getVolume();
        this.timeSlotID = sellPosition.getSell().getTimeSlot();
        this.exchangeID = sellPosition.getExchangeID();
    }

    public void setBid(Bid bidPosition) {
        if (!auctionEnded) {
            if (this.bidderID != null) {
                if (bidPosition.getPrice() > pricePerKWh) {
                    this.pricePerKWh = bidPosition.getPrice();
                    this.bidderID = bidPosition.getBidderID();
                    this.coveredVolume = bidPosition.getVolume();
                }
            } else {
                this.pricePerKWh = bidPosition.getPrice();
                this.bidderID = bidPosition.getBidderID();
                this.coveredVolume = bidPosition.getVolume();
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
        Transaction transaction = new Transaction(sellerID, bidderID, totalVolume, pricePerKWh, auctionID);

        return transaction;

    }

    public boolean isAuctionEnded() {
        return this.auctionEnded;
    }

    public double getPrice() {
        return pricePerKWh;
    }

    public double getCoveredVolume() {
        return coveredVolume;
    }
    
    public UUID getAuctionId() {
        return auctionID;
    }

    public UUID getBidderID() {
        return bidderID;
    }

    public double getTotalVolume() {
        return this.totalVolume;
    }

    public UUID getSellerID() {
        return this.sellerID;
    }

    public UUID getTimeSlotID() {
        return this.timeSlotID;
    }
}
