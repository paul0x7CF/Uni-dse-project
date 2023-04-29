package loadManager.prosumerActionManagement;

import sendable.Transaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionProsumerTracker {
    private Map<UUID, List<UUID>> biddersPerAuction;

    public List<UUID> getBiddersNotSatisfied() {
        return null;
    }

    public void addBidderToAuction(UUID auctionId, UUID bidderId) {
    }

    public synchronized void addAuction(UUID auctionID) {
    }

    public void checkWithTransactions(Transaction transaction) {
    }
}