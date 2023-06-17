package loadManager.prosumerActionManagement;

import CF.sendable.Transaction;
import MSP.Exceptions.IllegalAuctionException;
import MSP.Exceptions.ProsumerUnknownException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AuctionProsumerTracker {
    private static final Logger logger = LogManager.getLogger(AuctionProsumerTracker.class);
    //Map<TimeSlotID, Map<AuctionID, List<BidderID>>>
    private final Map<UUID, Map<UUID, List<UUID>>> auctionsPerTimeSlot = new HashMap<>();

    public synchronized void addBidderToAuction(UUID auctionId, UUID bidderId) {
        boolean auctionFound = false;
        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getValue().containsKey(auctionId)) {
                entry.getValue().get(auctionId).add(bidderId);
                auctionFound = true;
            }
        }

        if (!auctionFound) {
            throw new IllegalAuctionException("LOAD_MANAGER: Auction with ID " + auctionId + " not found");
        }
    }

    public synchronized void addAuction(UUID timeSlotId, UUID auctionID) {
        logger.debug("LOAD_MANAGER: Adding new Auction to prosumerTracker: {}", auctionID);
        auctionsPerTimeSlot.computeIfAbsent(timeSlotId, k -> new HashMap<>()).put(auctionID, new ArrayList<>());
    }

    public synchronized List<UUID> getFirstInAuction(UUID bidderId, UUID slotID) {
        List<UUID> firstInAuction = new ArrayList<>();
        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getKey().equals(slotID)) {
                for (Map.Entry<UUID, List<UUID>> entry2 : entry.getValue().entrySet()) {
                    List<UUID> bidderIDs = entry2.getValue();
                    if (!bidderIDs.isEmpty() && bidderIDs.get(bidderIDs.size() - 1).equals(bidderId)) {
                        firstInAuction.add(entry2.getKey());
                    }
                }
            }
        }
        return firstInAuction;
    }

    public synchronized Map<UUID, UUID> getWonAuctions(UUID timeSlotID) {
        Map<UUID, UUID> wonAuctions = new HashMap<>();
        if (auctionsPerTimeSlot.containsKey(timeSlotID)) {
            Map<UUID, List<UUID>> auctions = auctionsPerTimeSlot.get(timeSlotID);

            for (Map.Entry<UUID, List<UUID>> entry : auctions.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    if (entry.getValue().size() == 1) {
                        wonAuctions.put(entry.getKey(), entry.getValue().get(0));
                    }
                }
            }
        }

        return wonAuctions;
    }

    //set the winning bidder and delets every other one
    public void checkWithTransactions(Transaction transaction) throws ProsumerUnknownException {
        if (transaction == null) {
            throw new IllegalArgumentException("LOAD_MANAGER: Transaction cannot be null");
        }

        UUID bidderID = transaction.getBuyerID();
        UUID auctionID = transaction.getAuctionID();

        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getValue().containsKey(auctionID)) {
                List<UUID> bidders = entry.getValue().get(auctionID);
                if (!bidders.contains(bidderID)) {
                    throw new ProsumerUnknownException("LOAD_MANAGER: Prosumer seems to be unknown to the auction", Optional.ofNullable(bidderID));
                }

                //Remove all UUIDs except the winning bidder ID from the list
                bidders.removeIf(uuid -> !uuid.equals(bidderID));
            }
        }
    }
}