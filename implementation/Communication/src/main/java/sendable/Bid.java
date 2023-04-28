package sendable;

import java.util.Optional;
import java.util.UUID;

public class Bid implements ISendable {
    private final UUID bidderID = UUID.randomUUID();
    private double volume;
    private double price;
    private UUID auctionID;

    public Bid(double volume, double price) {
        this.volume = volume;
        this.price = price;
    }

    public UUID getBidderID() {
        return bidderID;
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
