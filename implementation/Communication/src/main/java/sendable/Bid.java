package sendable;

import java.util.Optional;
import java.util.UUID;

public class Bid implements ISendable {
    private final UUID bidderID = UUID.randomUUID();
    private double volume;
    private double price;
    private UUID auctionID;
    private UUID timeSlot;

    public Bid(double volume, double price, UUID timeSlot) {
        this.volume = volume;
        this.price = price;
        this.timeSlot = timeSlot;
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

    public Optional<UUID> getAuctionID() {
        return Optional.ofNullable(auctionID);
    }

    public void setAuctionID(UUID auctionID) {
        this.auctionID = auctionID;
    }
}
