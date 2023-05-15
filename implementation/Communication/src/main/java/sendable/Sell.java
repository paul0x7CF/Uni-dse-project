package sendable;

import java.util.Optional;
import java.util.UUID;

public class Sell implements ISendable {
    private final UUID sellerID = UUID.randomUUID();
    private final double volume;
    private final double askPrice;
    private UUID auctionID;
    private UUID timeSlot;

    public Sell(double volume, double askPrice, UUID timeSlot) {
        this.volume = volume;
        this.askPrice = askPrice;
        this.timeSlot = timeSlot;
    }

    public UUID getSellerID() {
        return sellerID;
    }
    public java.util.UUID getTimeSlot() {
        return timeSlot;
    }

    public double getVolume() {
        return volume;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public Optional<UUID> getAuctionID() {
        return Optional.ofNullable(auctionID);
    }

    public void setAuctionID(UUID auctionID) {
        this.auctionID = auctionID;
    }
}
