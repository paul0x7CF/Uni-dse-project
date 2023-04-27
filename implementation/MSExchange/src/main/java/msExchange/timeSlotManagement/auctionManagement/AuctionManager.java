package msExchange.timeSlotManagement.auctionManagement;

import sendable.Sell;
import sendable.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionManager {
    private List<AuctionThread> auctions;
    private Date lastAuctionAdded;

    public void startAuction(Sell sell) {
    }

    public List<Transaction> endAllAuctions() {
        return null;
    }

    public Map<UUID, Double> getAuctions() {
        return null;
    }

    public Date getLastAuctionTime() {
        return null;
    }
}
