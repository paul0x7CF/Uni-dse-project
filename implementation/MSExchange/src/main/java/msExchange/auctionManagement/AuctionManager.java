package msExchange.auctionManagement;

import CF.sendable.Bid;
import CF.sendable.Sell;
import CF.sendable.Transaction;
import MSP.Exceptions.AuctionNotFoundException;
import MSP.Exceptions.InvalidTimeSlotException;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AuctionManager implements Runnable {
    private static final Logger logger = LogManager.getLogger(AuctionManager.class);
    private final int CHECK_DURATION; //in millisecs
    private final int MINUTES_TO_LIVE_AFTER_EXPIRING; //in millisecs
    private final Map<UUID, Auction> auctions;    //key: auctionId, value: auction
    private final Map<UUID, TimeSlot> timeSlots;
    private final BlockingQueue<Transaction> transactionQueue;
    private final BlockingQueue<Bid> bidQueue;
    private final BlockingQueue<Sell> sellQueue;

    public AuctionManager(BlockingQueue<Transaction> transactionQueue, BlockingQueue<Bid> bidQueue, BlockingQueue<Sell> sellQueue) {
        this.auctions = new ConcurrentHashMap<>();
        this.timeSlots = new ConcurrentHashMap<>();
        this.transactionQueue = transactionQueue;
        this.bidQueue = bidQueue;
        this.sellQueue = sellQueue;

        PropertyFileReader propertyFileReader = new PropertyFileReader();
        CHECK_DURATION = Integer.parseInt(propertyFileReader.getCheckDuration());
        MINUTES_TO_LIVE_AFTER_EXPIRING = Integer.parseInt(propertyFileReader.getMinutesToLiveAfterExpiring());
    }

    @Override
    public void run() {
        logger.trace("EXCHANGE: AuctionManager started");
        long lastCheckTime = System.currentTimeMillis();
        while (true) {
            try {
                processQueues();
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - lastCheckTime;

                if (elapsedTime >= CHECK_DURATION) {
                    checkTimeSlots();
                    lastCheckTime = currentTime;
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                Thread.currentThread().interrupt();
            } catch (AuctionNotFoundException | InvalidTimeSlotException e) {
                logger.error(e.getMessage());
            }
        }
    }


    /**
     * Checks the time slots and performs necessary actions based on their current state. - If a time slot is currently
     * within its start and end time, it is opened. - If a time slot has already ended, it is removed from the list if
     * it has exceeded the specified minutes to live after expiring.
     */

    /*
    private void checkTimeSlots() {

        Iterator<Map.Entry<UUID, TimeSlot>> iterator = timeSlots.entrySet().iterator();
        TimeSlot openTimeSlot = null;

        while (iterator.hasNext()) {
            Map.Entry<UUID, TimeSlot> entry = iterator.next();

            // Check if the start time of the time slot is before the current time
            if (entry.getValue().getStartTime().isBefore(LocalDateTime.now())) {
                // Check if the end time of the time slot is after the current time
                if (entry.getValue().getEndTime().isAfter(LocalDateTime.now())) {
                    // If there is an open time slot and it is different from the current time slot
                    if (openTimeSlot != null) {
                        if (openTimeSlot.getTimeSlotId() != entry.getKey()) {
                            UUID oldTimeSlotID = openTimeSlot.getTimeSlotId();

                            entry.getValue().openTimeSlot();
                            openTimeSlot = entry.getValue();

                            List<Auction> oldAuctions = new ArrayList<>();
                            for (Auction auction : auctions.values()) {
                                if (auction.getTimeSlotID().equals(oldTimeSlotID)) {
                                    oldAuctions.add(auction);
                                }
                            }
                            for (Auction auction : oldAuctions) {
                                auction.endAuction();
                            }
                        }
                    }
                } else {
                    // If there is no open time slot
                    entry.getValue().openTimeSlot();
                    openTimeSlot = entry.getValue();
                }
            } else {
                // Remove the time slot if it has exceeded the specified minutes to live after expiring
                if (entry.getValue().getEndTime().plusMinutes(MINUTES_TO_LIVE_AFTER_EXPIRING).isAfter(LocalDateTime.now())) {
                    iterator.remove(); // Remove the entry
                }
            }
        }

    }*/
    private void checkTimeSlots() {
        Iterator<Map.Entry<UUID, TimeSlot>> iterator = timeSlots.entrySet().iterator();
        TimeSlot openTimeSlot = null;

        while (iterator.hasNext()) {
            Map.Entry<UUID, TimeSlot> entry = iterator.next();
            TimeSlot currentSlot = entry.getValue();

            // Check if the time slot is currently active
            if (currentSlot.getStartTime().isBefore(LocalDateTime.now()) && currentSlot.getEndTime().isAfter(LocalDateTime.now())) {
                // If there is no open time slot or the current time slot is different from the open time slot
                if (openTimeSlot == null || openTimeSlot.getTimeSlotId() != entry.getKey()) {
                    UUID oldTimeSlotID = openTimeSlot != null ? openTimeSlot.getTimeSlotId() : null;

                    currentSlot.openTimeSlot();
                    openTimeSlot = currentSlot;

                    // End auctions associated with the previous open time slot
                    if (oldTimeSlotID != null) {
                        List<Auction> oldAuctions = filterAuctionsByTimeSlot(oldTimeSlotID);
                        endAuctions(oldAuctions);
                    }
                }
            } else {
                // Remove the time slot if it has exceeded the specified minutes to live after expiring
                if (currentSlot.getEndTime().plusMinutes(MINUTES_TO_LIVE_AFTER_EXPIRING).isBefore(LocalDateTime.now())) {
                    iterator.remove(); // Remove the entry
                }
            }
        }
    }

    private List<Auction> filterAuctionsByTimeSlot(UUID timeSlotID) {
        return auctions.values().stream()
                .filter(auction -> auction.getTimeSlotID().equals(timeSlotID))
                .collect(Collectors.toList());
    }

    private void endAuctions(List<Auction> auctions) {
        for (Auction auction : auctions) {
            auction.endAuction();
        }
    }


    private void processQueues() throws AuctionNotFoundException, InterruptedException, InvalidTimeSlotException {
        //Process sellQueue
        processSellQueue();

        //Process bidQueue
        processBidQueue();


        //Check for ended auctions
        checkForEndedAuctions();
    }

    private void checkForEndedAuctions() {
        List<UUID> auctionsToRemove = new ArrayList<>();
        for (Auction auction : auctions.values()) {
            if (auction.isAuctionEnded()) {
                auctionsToRemove.add(auction.getAuctionId());
            }
        }
        for (UUID auctionId : auctionsToRemove) {
            auctions.remove(auctionId);
        }
    }

    /**
     * Processes the bid queue by retrieving the next bid and adding it to the corresponding auction.
     *
     * @throws AuctionNotFoundException if the auction ID is missing or doesn't exist
     * @throws InvalidTimeSlotException if the time slot is invalid
     */
    private void processBidQueue() throws AuctionNotFoundException, InvalidTimeSlotException {
        Bid bid = bidQueue.poll(); // Non-blocking call, retrieves the next bid or null if empty
        if (bid != null) {
            Optional<UUID> auctionID = bid.getAuctionID();
            if (auctionID.isEmpty()) {
                throw new AuctionNotFoundException("EXCHANGE: The Auction ID is missing", Optional.empty(), Optional.empty(), Optional.of(bid));
            }
            if (!auctionExists(auctionID.get())) {
                logger.warn("EXCHANGE: Auction doesn't exist yet?");
                try {
                    bidQueue.put(bid);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (!timeSlots.containsKey(bid.getTimeSlot())) {
                    throw new InvalidTimeSlotException("EXCHANGE: TimeSlot doesn't exist, therefore the UUID was invalid.", Optional.of(bid.getTimeSlot()));
                }
                logger.debug("EXCHANGE: Add Bid " + bid.getBidderID() + "to Auction" + bid.getAuctionID());
                addBidToAuction(auctionID.get(), bid);
            }

        }
    }

    /**
     * Processes the sell queue by retrieving the next sell from the queue and creating a new auction for it.
     *
     * @throws AuctionNotFoundException if the auction is not found
     * @throws InvalidTimeSlotException if the time slot is invalid
     */
    private void processSellQueue() throws AuctionNotFoundException, InvalidTimeSlotException {
        Sell sell = sellQueue.poll(); // Non-blocking call, retrieves the next sell or null if empty
        if (sell != null) {
            if (timeSlots.containsKey(sell.getTimeSlot())) {
                logger.debug("EXCHANGE: Creating new Auction...");
                addNewAuction(sell);
            } else {
                throw new InvalidTimeSlotException("EXCHANGE: TimeSlot doesn't exist, therefore the UUID was invalid.", Optional.of(sell.getTimeSlot()));
            }
        }
    }

    private boolean auctionExists(UUID auctionID) {
        return auctions.containsKey(auctionID);
    }

    /**
     * Adds a new auction based on the provided Sell object.
     *
     * @param sell the Sell object containing auction details
     * @throws AuctionNotFoundException if the auction is not found
     */
    private void addNewAuction(Sell sell) throws AuctionNotFoundException {
        if (sell.getAuctionID().isEmpty()) {
            throw new AuctionNotFoundException("EXCHANGE: The Auction ID is missing", Optional.empty(), Optional.of(sell), Optional.empty());
        }

        Auction auction = new Auction(sell.getAuctionID().get(), sell, transactionQueue);
        auctions.put(sell.getAuctionID().get(), auction);
        logger.info("EXCHANGE: Auction has been added: {} for sell from {} with askPrice {} and totalVolume of {}.", auctions.get(sell.getAuctionID().get()), sell.getSellerID(), sell.getAskPrice(), sell.getVolume());
    }

    /**
     * Adds a bid to the specified auction.
     *
     * @param auctionID the ID of the auction
     * @param bid       the bid to be added
     */
    private void addBidToAuction(UUID auctionID, Bid bid) {
        Auction auction = auctions.get(auctionID);
        auction.setBid(bid);

        // Log the successful addition of the bid
        logger.info("EXCHANGE: Bid from {} has been added to auction {} with a price of {} and volume {}. The occupancy rate of the auction is {}/{}.", bid.getBidderID(), auctionID, bid.getPrice(), bid.getVolume(), bid.getVolume(), auction.getVolume());
    }

    public Map<UUID, Auction> getAuctions() {
        return Collections.unmodifiableMap(auctions);
    }

    public Map<UUID, TimeSlot> getTimeSlots() {
        return Collections.unmodifiableMap(timeSlots);
    }

    //used from MSExchange - when Message with new TimeSlots from Exchange comes in
    public void addTimeSlots(CF.sendable.TimeSlot newTimeSlot) {
        if (timeSlots.get(newTimeSlot.getTimeSlotID()) == null) {
            timeSlots.put(newTimeSlot.getTimeSlotID(), new TimeSlot(newTimeSlot.getTimeSlotID(), newTimeSlot.getStartTime(), newTimeSlot.getEndTime()));
            logger.debug("EXCHANGE: TimeSlot has been added: " + timeSlots.get(newTimeSlot.getTimeSlotID()));
        }
    }
}