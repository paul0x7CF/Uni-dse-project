package sendable;

import java.util.Optional;
import java.util.UUID;

public class Sell implements ISendable {
    private final UUID sellerID = UUID.randomUUID();
    private final double volume;
    private final double askPrice;
    private UUID auctionID;

    public Sell(double volume, double askPrice) {
        this.volume = volume;
        this.askPrice = askPrice;
    }

    public UUID getSellerID() {
        return sellerID;
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
