package loadManager.prosumerActionManagement;

import loadManager.Exceptions.IllegalAuctionException;
import loadManager.Exceptions.IllegalSlotException;
import sendable.Transaction;

import java.util.*;

public class AuctionProsumerTracker {
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

    public List<UUID> getBiddersNotSatisfiedForTimeslot(UUID timeSlotID) {
        List<UUID> notSatisfiedBidders = new ArrayList<>();
        List<UUID> satisfiedBidders = new ArrayList<>();

        if (auctionsPerTimeSlot.containsKey(timeSlotID)) {
            Map<UUID, List<UUID>> auctions = auctionsPerTimeSlot.get(timeSlotID);

            for (List<UUID> bidders : auctions.values()) {
                satisfiedBidders.add(bidders.get(bidders.size() - 1));
                notSatisfiedBidders.addAll(bidders.subList(0, bidders.size() - 1));
            }

            //remove all satisfied bidders from notSatisfiedBidders
            Iterator<UUID> iterator = notSatisfiedBidders.iterator();
            while (iterator.hasNext()) {
                UUID notSatisfiedBidder = iterator.next();
                if (satisfiedBidders.contains(notSatisfiedBidder)) {
                    iterator.remove();
                }
            }

            if (notSatisfiedBidders.isEmpty()) {
                return notSatisfiedBidders;
            }

            //remove duplicates
            return new ArrayList<>(new HashSet<>(notSatisfiedBidders));
        }

        throw new IllegalSlotException("No timeslot with ID " + timeSlotID + " found");
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

    //TODO: Maybe add winner variable to Auction - to check who are the real losers
    //adds the winning bidder to the list of bidders
    public void checkWithTransactions(Transaction transaction) {
        UUID bidderID = transaction.getBuyerID();
        UUID auctionID = transaction.getAuctionID();

        for (Map.Entry<UUID, Map<UUID, List<UUID>>> entry : auctionsPerTimeSlot.entrySet()) {
            if (entry.getValue().containsKey(auctionID)) {
                entry.getValue().get(auctionID).add(bidderID);
            }
        }
    }
}