package loadManager.auctionManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionManager {
    private Map<UUID, List<Auction>> auctions;

    public AuctionManager() {
        auctions = new HashMap<>();
    }

    public void addAuction(UUID slotId, Auction auction) {
        // Adds an auction with the specified UUID
    }

    public void removeAuction(UUID auctionId) {
        // Removes the auction with the specified UUID
    }

    public void removeAllAuctionsFromSlot(UUID slotId) {
        auctions.remove(slotId);
    }

    public Auction getAuction(UUID auctionId) {
        // Returns the auction with the specified UUID
        return null;
    }

    public List<Auction> getAllAuctionsForSlot(UUID slotId) {
        // Returns a copy of the entire auction list
        return auctions.get(slotId);
    }

    private void findProsumersNotSatisfied() {
    }

    private void sendProsumerToStorage(UUID prosumer, double kwh) {
    }
}