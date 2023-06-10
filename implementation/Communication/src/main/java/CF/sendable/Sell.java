package CF.sendable;

import java.util.Optional;
import java.util.UUID;

public class Sell implements ISendable {
    private final UUID sellerID;
    private final double volume;
    private double askPrice;
    private UUID auctionID;
    private UUID timeSlot;

    public Sell(double volume, double askPrice, UUID timeSlot, UUID sellerID) {
        this.volume = volume;
        this.askPrice = askPrice;
        this.timeSlot = timeSlot;
        this.sellerID = sellerID;
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

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public Optional<UUID> getAuctionID() {
        return Optional.ofNullable(auctionID);
    }

    public void setAuctionID(UUID auctionID) {
        this.auctionID = auctionID;
    }
}
