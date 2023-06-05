package sendable;

import java.util.Optional;
import java.util.UUID;

public class Bid implements ISendable {
    private final UUID bidderID;
    private double volume;
    private double price;
    private UUID auctionID;
    private UUID timeSlot;

    public Bid(double volume, double price, UUID timeSlot, UUID bidderID) {
        this.volume = volume;
        this.price = price;
        this.timeSlot = timeSlot;
        this.bidderID = bidderID;
    }

    public UUID getBidderID() {
        return bidderID;
    }

    public UUID getTimeSlot() {
        return timeSlot;
    }

    public double getVolume() {
        return volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Optional<UUID> getAuctionID() {
        return Optional.ofNullable(auctionID);
    }

    public void setAuctionID(UUID auctionID) {
        this.auctionID = auctionID;
    }
}
