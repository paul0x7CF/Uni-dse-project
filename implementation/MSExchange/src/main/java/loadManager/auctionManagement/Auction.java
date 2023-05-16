package loadManager.auctionManagement;

import java.util.UUID;

public class Auction {
    private UUID auctionId;
    private double kwh;
    private UUID timeSlotId;
    private UUID exchangeID;
    private UUID sellerID;

    public Auction(UUID auctionId, double kwh, UUID timeSlotId, UUID exchangeID, UUID sellerID) {
        this.auctionId = auctionId;
        this.kwh = kwh;
        this.timeSlotId = timeSlotId;
        this.exchangeID = exchangeID;
        this.sellerID = sellerID;
    }

    public UUID getAuctionId() {
        return this.auctionId;
    }

    public double getKwh() {
        return this.kwh;
    }

    public UUID getTimeSlotId() {
        return this.timeSlotId;
    }

    public UUID getExchangeID() {
        return this.exchangeID;
    }

    public UUID getSellerID() {
        return this.sellerID;
    }

    public void endAuction() {
    }
}
