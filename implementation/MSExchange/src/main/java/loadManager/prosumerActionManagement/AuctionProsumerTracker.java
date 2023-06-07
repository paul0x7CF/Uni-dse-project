package loadManager.prosumerActionManagement;

import Exceptions.IllegalAuctionException;
import sendable.Transaction;

import java.util.*;

public class AuctionProsumerTracker {
    //Map<TimeSlotID, Map<AuctionID, List<BidderID>>>
    private Map<UUID, Map<UUID, List<UUID>>> auctionsPerTimeSlot = new HashMap<>();

    public List<UUID> getBiddersNotSatisfied(UUID auctionID) {
        //returns a List of all Bidders, except for the winner's ID
        List<UUID> originalList = new ArrayList<>();
        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getValue().containsKey(auctionID)) {
                originalList = entry.getValue().get(auctionID);
            }
        }

        if (originalList == null) {
            throw new IllegalAuctionException("Auction with ID " + auctionID + " not found");
        }

        List<UUID> resultList = new ArrayList<>(originalList.subList(0, originalList.size() - 1));
        return resultList;
    }

    public synchronized void addBidderToAuction(UUID auctionId, UUID bidderId) {
        boolean auctionFound = false;
        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getValue().containsKey(auctionId)) {
                entry.getValue().get(auctionId).add(bidderId);
                auctionFound = true;
            }
        }

        if (!auctionFound) {
            throw new IllegalAuctionException("Auction with ID " + auctionId + " not found");
        }
    }

    public synchronized void addAuction(UUID timeSlotId, UUID auctionID) {
        auctionsPerTimeSlot.computeIfAbsent(timeSlotId, k -> new HashMap<>()).put(auctionID, new ArrayList<>());
    }

    public synchronized List<UUID> getFirstInAuction(UUID bidderId, UUID slotID) {
        List<UUID> firstInAuction = new ArrayList<>();
        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getKey().equals(slotID)) {
                for (Map.Entry<UUID, List<UUID>> entry2 : entry.getValue().entrySet()) {
                    if (entry2.getValue().get(0).equals(bidderId)) {
                        firstInAuction.add(entry2.getKey());
                    }
                }
            }
        }
        return firstInAuction;
    }

    /*
    public List<UUID> getAuctionsWithoutBidders(UUID timeSlotID) {
        List<UUID> auctionsWithoutBidders = new ArrayList<>();
        if (auctionsPerTimeSlot.containsKey(timeSlotID)) {
            Map<UUID, List<UUID>> auctions = auctionsPerTimeSlot.get(timeSlotID);

            for (Map.Entry<UUID, List<UUID>> entry : auctions.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    auctionsWithoutBidders.add(entry.getKey());
                }
            }
        }

        return auctionsWithoutBidders;
    }
*/

    //TODO: Maybe add winner variable to Auction - to check who are the real losers
    //adds the winning bidder to the list of bidders
    public void checkWithTransactions(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        UUID bidderID = transaction.getBuyerID();
        UUID auctionID = transaction.getAuctionID();

        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getValue().containsKey(auctionID)) {
                entry.getValue().get(auctionID).add(bidderID);
            }
        }
    }

    public List<UUID> getBiddersAuctions(UUID bidderID) {
        List<UUID> biddersAuctions = new ArrayList<>();

        for (Map<UUID, List<UUID>> auctionMap : auctionsPerTimeSlot.values()) {
            for (UUID auctionId : auctionMap.keySet()) {
                List<UUID> bidderIds = auctionMap.get(auctionId);
                if (bidderIds.contains(biddersAuctions)) {
                    biddersAuctions.add(auctionId);
                }
            }
        }

        return biddersAuctions;
    }
}