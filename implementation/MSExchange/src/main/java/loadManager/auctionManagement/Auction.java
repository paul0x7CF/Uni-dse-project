package loadManager.auctionManagement;

import java.util.UUID;

public class Auction {
    private UUID auctionId;
    private double kwh;
    private UUID timeSlotId;

    public Auction(UUID auctionId, double kwh, UUID timeSlotId) {
        this.auctionId = auctionId;
        this.kwh = kwh;
        this.timeSlotId = timeSlotId;
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
}
