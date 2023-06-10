package MSP.Exceptions;

import CF.sendable.Bid;
import CF.sendable.Sell;

import java.util.Optional;
import java.util.UUID;

public class AuctionNotFoundException extends Exception {
    Optional<Sell> sell;
    Optional<Bid> bid;
    Optional<UUID> auctionID;

    public AuctionNotFoundException(String message, Optional<UUID> auctionID, Optional<Sell> sell, Optional<Bid> bid) {
        super(message);
        this.sell = sell;
        this.bid = bid;
        this.auctionID = auctionID;
    }

    public Optional<UUID> getAuctionID() {
        return auctionID;
    }

    public Optional<Bid> getBid() {
        return bid;
    }

    public Optional<Sell> getSell() {
        return sell;
    }
}
