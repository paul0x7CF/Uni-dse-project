package loadManager.auctionManagement;

import Exceptions.CommandNotPossibleException;
import Exceptions.IllegalAuctionException;
import Exceptions.InvalidTimeSlotException;
import sendable.Bid;
import sendable.Transaction;

import java.util.*;
import java.util.logging.Logger;

public class AuctionManager {
    //key: slotId, value: list of auctions
    Logger logger = Logger.getLogger(getClass().getName());
    private Map<UUID, List<Auction>> auctionsPerSlot = new HashMap<>();

    public void addAuction(Auction auction) {
        // Adds an auction with the specified UUID
        List<Auction> auctions = auctionsPerSlot.getOrDefault(auction.getTimeSlotID(), new ArrayList<>());
        auctions.add(auction);
        auctionsPerSlot.put(auction.getTimeSlotID(), auctions);
    }

    //really needed? -> yes to be able to declare either the given timeSlot is incorrect, or just not used
    public void addNewTimeSlots(List<UUID> timeSlotIds) {
        // Adds a new time slot to the auction manager
        for (UUID timeSlotId : timeSlotIds) {
            auctionsPerSlot.put(timeSlotId, new ArrayList<>());
        }
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

    public List<Auction> getAllAuctionsForSlot(UUID slotId) throws InvalidTimeSlotException {
        // Returns a copy of the entire auction list
        if (auctionsPerSlot.containsKey(slotId)) {
            return auctionsPerSlot.get(slotId);
        }
        throw new InvalidTimeSlotException("Slot not found", Optional.of(slotId));
    }

    public List<Transaction> getTransactionsForSlot(UUID slotId) throws InvalidTimeSlotException {
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
        throw new InvalidTimeSlotException("Slot not found with ID: " + slotId, Optional.of(slotId));
    }

    //expected to get called, after receiving the transaction from the market
    public void setBidder(UUID auctionId, Bid bid) {
        Auction auction = getAuctionByID(auctionId);
        auction.setBid(bid);
    }

    public void endTimeSlot(UUID slotId) throws InvalidTimeSlotException {
        if (auctionsPerSlot.containsKey(slotId)) {
            for (Auction auction : auctionsPerSlot.get(slotId)) {
                auction.endAuction();
            }
        }
        throw new InvalidTimeSlotException("Slot not found with ID: " + slotId, Optional.of(slotId));
    }

    //Tested and is working
    public List<UUID> getUnsatisfiedSellers(UUID timeSlotID) throws InvalidTimeSlotException {
        if (!auctionsPerSlot.containsKey(timeSlotID)) {
            throw new InvalidTimeSlotException("Slot not found with ID: " + timeSlotID, Optional.of(timeSlotID));
        }

        List<Auction> auctions = auctionsPerSlot.get(timeSlotID);
        List<UUID> unsatisfiedSellers = new ArrayList<>();
        if (auctions != null) {
            for (Auction auction : auctions) {
                if (auction.getBidderID() == null) {
                    unsatisfiedSellers.add(auction.getSellerID());
                }
            }
            return unsatisfiedSellers;
        }

        return Collections.emptyList();
    }

    public List<Auction> getAuctions(List<UUID> auctionIDs) {
        List<Auction> auctions = new ArrayList<>();
        for (UUID auctionID : auctionIDs) {
            try {
                auctions.add(getAuctionByID(auctionID));
            } catch (IllegalAuctionException e) {
                logger.severe(e.getMessage());
            }
        }
        return auctions;
    }
}