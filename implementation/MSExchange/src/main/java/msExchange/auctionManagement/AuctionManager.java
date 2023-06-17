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
    private void checkTimeSlots() {

        Iterator<Map.Entry<UUID, TimeSlot>> iterator = timeSlots.entrySet().iterator();
        TimeSlot openTimeSlot = null;

        while (iterator.hasNext()) {
            Map.Entry<UUID, TimeSlot> entry = iterator.next();

            if (entry.getValue().getEndTime().isBefore(LocalDateTime.now())) {
                if (entry.getValue().getEndTime().isAfter(LocalDateTime.now())) {
                    if (openTimeSlot != null) {
                        if (openTimeSlot != entry.getValue()) {
                            entry.getValue().openTimeSlot();
                            openTimeSlot = entry.getValue();
                        }
                    } else {
                        entry.getValue().openTimeSlot();
                        openTimeSlot = entry.getValue();
                    }
                } else {
                    assert openTimeSlot != null;
                    if (entry.getValue().getEndTime() == openTimeSlot.getStartTime()) {
                        logger.debug("EXCHANGE: Close TimeSlot: " + entry.getValue().getTimeSlotId());
                        List<Auction> filteredAuctions = auctions.values().stream()
                                .filter(auction -> auction.getTimeSlotID().equals(entry.getValue().getTimeSlotId())).toList();
                        filteredAuctions.forEach(Auction::endAuction);

                    }
                    if (entry.getValue().getEndTime().plusMinutes(MINUTES_TO_LIVE_AFTER_EXPIRING).isAfter(LocalDateTime.now())) {
                        iterator.remove(); // Remove the entry
                    }
                }
            }
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
            }
            if (!timeSlots.containsKey(bid.getTimeSlot())) {
                throw new InvalidTimeSlotException("EXCHANGE: TimeSlot doesn't exist, therefore the UUID was invalid.", Optional.of(bid.getTimeSlot()));
            }
            logger.debug("EXCHANGE: Add Bid " + bid.getBidderID() + "to Auction" + bid.getAuctionID());
            addBidToAuction(auctionID.get(), bid);
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