package loadManager.auctionManagement;

import loadManager.Exceptions.CommandNotPossibleException;
import loadManager.Exceptions.IllegalAuctionException;
import loadManager.Exceptions.IllegalSlotException;
import sendable.Transaction;

import java.util.*;
import java.util.logging.Logger;

public class AuctionManager {
    //key: slotId, value: list of auctions
    Logger logger = Logger.getLogger(getClass().getName());
    private Map<UUID, List<Auction>> auctionsPerSlot = new HashMap<>();

    public void addAuction(UUID slotId, Auction auction) {
        // Adds an auction with the specified UUID
        List<Auction> auctions = auctionsPerSlot.getOrDefault(slotId, new ArrayList<>());
        auctions.add(auction);
        auctionsPerSlot.put(slotId, auctions);
    }

    /*
    public void removeAuction(UUID auctionId) throws CommandNotPossibleException {
        // Removes the auction with the specified UUID
        boolean auctionRemoved = false;

        for (List<Auction> auctions : auctionsPerSlot.values()) {
            if (auctions.removeIf(auction -> auction.getAuctionId().equals(auctionId))) {
                auctionRemoved = true;
            }
        }

        if (!auctionRemoved) {
            throw new CommandNotPossibleException("Auction not found with ID: " + auctionId);
        }
    }
*/
    private void removeAllAuctionsFromSlot(UUID slotId) throws CommandNotPossibleException {
        // Removes all auctions from the specified slot
        if (!auctionsPerSlot.containsKey(slotId)) {
            throw new CommandNotPossibleException("Slot ID not found: " + slotId);
        }
        auctionsPerSlot.remove(slotId);
    }

    public Auction getAuctionByID(UUID auctionId) throws IllegalAuctionException {
        // Returns the auction with the specified UUID
        for (List<Auction> auctions : auctionsPerSlot.values()) {
            for (Auction auction : auctions) {
                if (auction.getAuctionId().equals(auctionId)) {
                    return auction;
                }
            }
        }
        throw new IllegalAuctionException("Auction not found with ID: " + auctionId);
    }

    public List<Auction> getAllAuctionsForSlot(UUID slotId) throws IllegalSlotException {
        // Returns a copy of the entire auction list
        if (auctionsPerSlot.containsKey(slotId)) {
            return auctionsPerSlot.get(slotId);
        }
        throw new IllegalSlotException("Slot not found with ID: " + slotId);
    }

    public List<Transaction> getTransactionsForSlot(UUID slotId) throws IllegalSlotException {
        if (auctionsPerSlot.containsKey(slotId)) {
            List<Transaction> transactions = new ArrayList<>();

            for (Auction auction : auctionsPerSlot.get(slotId)) {
                try {
                    Transaction transaction = auction.getTransaction();
                    if (transaction != null) {
                        transactions.add(transaction);
                    } else {
                        //Handle the case where the transaction is not available
                        throw new RuntimeException("Transaction not available for auction: " + auction.getAuctionId());
                    }
                } catch (CommandNotPossibleException e) {
                    logger.severe(e.getMessage());
                }
            }

            try {
                removeAllAuctionsFromSlot(slotId);
            } catch (CommandNotPossibleException e) {
                throw new RuntimeException(e);
            }

            return transactions;
        }
        //Throw an exception if the slot is not found
        throw new IllegalSlotException("Slot not found with ID: " + slotId);
    }

    public void endTimeSlot(UUID slotId) {
        if (auctionsPerSlot.containsKey(slotId)) {
            for (Auction auction : auctionsPerSlot.get(slotId)) {
                auction.endAuction();
            }
        }
        throw new IllegalSlotException("Slot not found with ID: " + slotId);
    }

    public List<Auction> getBiddersAuctions(UUID bidderID) {
        List<Auction> biddersAuctions = new ArrayList<>();

        for (List<Auction> auctions : auctionsPerSlot.values()) {
            for (Auction auction : auctions) {
                if (auction.getBidderID().equals(bidderID)) {
                    biddersAuctions.add(auction);
                }
            }
        }
        return biddersAuctions;
    }

    public List<UUID> getUnsatisfiedSellers(List<UUID> auctionsWithoutBidders, UUID timeSlotID) {
        List<Auction> auctions = auctionsPerSlot.get(timeSlotID);
        List<UUID> unsatisfiedSellers = new ArrayList<>();
        if (auctions != null) {
            for (Auction auction : auctions) {
                unsatisfiedSellers.add(auction.getSellerID());
            }
            return unsatisfiedSellers;
        }

        throw new IllegalArgumentException("The given Auctions doesn't exist");
    }
}