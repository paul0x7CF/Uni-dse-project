package msExchange.auctionManagement;

import Exceptions.AuctionNotFoundException;
import Exceptions.InvalidTimeSlotException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager implements Runnable {
    private static final Logger logger = LogManager.getLogger(AuctionManager.class);
    private final int CHECK_DURATION; //in millisecs
    private final int MINUTES_TO_LIVE_AFTER_EXPIRING; //in millisecs

    private Map<UUID, Auction> auctions;
    private Map<UUID, TimeSlot> timeSlots;
    private BlockingQueue<Transaction> transactionQueue;
    private BlockingQueue<Bid> bidQueue;
    private BlockingQueue<Sell> sellQueue;

    public AuctionManager(final BlockingQueue<Transaction> transactionQueue, final BlockingQueue<Bid> bidQueue, final BlockingQueue<Sell> sellQueue) {
        this.auctions = new ConcurrentHashMap<>();
        this.timeSlots = new ConcurrentHashMap<>();
        this.transactionQueue = transactionQueue;
        this.bidQueue = bidQueue;
        this.sellQueue = sellQueue;

        //read Properties
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties");
            properties.load(configFile);
            configFile.close();

            CHECK_DURATION = Integer.parseInt(properties.getProperty("exchange.checkDuration"));
            MINUTES_TO_LIVE_AFTER_EXPIRING = Integer.parseInt(properties.getProperty("exchange.minutesToLiveAfterExpiring"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
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
                //build Message to tell exchange - that it was invalid!
            }
        }
    }

    private void checkTimeSlots() {
        Iterator<Map.Entry<UUID, TimeSlot>> iterator = timeSlots.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, TimeSlot> entry = iterator.next();
            if (entry.getValue().getStartTime().isBefore(LocalDateTime.now())) {
                if (entry.getValue().getEndTime().isAfter(LocalDateTime.now())) {
                    entry.getValue().openTimeSlot();
                } else {
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

    private void processBidQueue() throws AuctionNotFoundException, InterruptedException {
        Bid bid = bidQueue.poll(); // Non-blocking call, retrieves the next bid or null if empty
        if (bid != null) {
            Optional<UUID> auctionID = bid.getAuctionID();
            if (auctionID.isEmpty()) {
                throw new AuctionNotFoundException("The Auction ID is missing", Optional.empty(), Optional.empty(), Optional.of(bid));
            }
            if (!auctionExists(auctionID.get())) {
                logger.debug("Auction doesn't exist yet.");
                bidQueue.put(bid);
                //?? throw new AuctionNotFoundException("The Auction ID is incorrect.", auctionID, Optional.empty(), Optional.of(bid));
            } else {
                logger.debug("Add Bid to Auction");
                addBidToAuction(auctionID.get(), bid);
            }
        }
    }

    private void processSellQueue() throws AuctionNotFoundException, InvalidTimeSlotException {
        Sell sell = sellQueue.poll(); // Non-blocking call, retrieves the next sell or null if empty
        if (sell != null) {
            if (timeSlots.containsKey(sell.getTimeSlot())) {
                TimeSlot timeSlot = timeSlots.get(sell.getTimeSlot());
                if (timeSlot.getEndTime().isBefore(LocalDateTime.now())) {
                    //endTime is in the past
                    //send Message to Storage -> Message Builder!
                } else {
                    logger.info("Creating new Auction...");
                    addNewAuction(sell);
                }
            } else {
                throw new InvalidTimeSlotException("TimeSlot doesn't exist, therefore the UUID was invalid.", Optional.of(sell.getTimeSlot()), Optional.of(sell), Optional.empty());
            }
        }
    }

    private boolean auctionExists(UUID auctionID) {
        return auctions.containsKey(auctionID);
    }

    private void addNewAuction(Sell sell) throws AuctionNotFoundException {
        if (sell.getAuctionID().isEmpty()) {
            throw new AuctionNotFoundException("Auction ID is missing", Optional.empty(), Optional.of(sell), Optional.empty());
        }
        UUID AuctionUuid = sell.getAuctionID().get();
        Auction auction = new Auction(AuctionUuid, sell, transactionQueue);
        auctions.put(AuctionUuid, auction);
        logger.info("Auction has been added: " + auctions.get(AuctionUuid));
    }

    private void addBidToAuction(UUID auctionID, Bid bid) {
        TimeSlot timeSlot = timeSlots.get(bid.getTimeSlot());

        if (timeSlot.getEndTime().isBefore(LocalDateTime.now())) {
            //endTime is in the past
            logger.info("Bid was for a previous slot. Sending the Bid to the Storage...");
            //send Message to Storage -> Message Builder!

            logger.error("Not implemented yet!");
        } else {
            Auction auction = auctions.get(auctionID);
            auction.setBid(bid);
            logger.info("Bid has been set");
        }
    }

    public void endAllAuctions() {
        for (Auction auction : auctions.values()) {
            auction.endAuction();
        }
    }

    public Map<UUID, Auction> getAuctions() {
        return Collections.unmodifiableMap(auctions);
    }

    public Map<UUID, TimeSlot> getTimeSlots() {
        return Collections.unmodifiableMap(timeSlots);
    }

    //used from MSExchange - when Message with new TimeSlots from Exchange comes in
    public void addTimeSlots(sendable.TimeSlot newTimeSlot) {
        if (timeSlots.get(newTimeSlot.getTimeSlotID()) == null) {
            timeSlots.put(newTimeSlot.getTimeSlotID(), new TimeSlot(newTimeSlot.getTimeSlotID(), newTimeSlot.getStartTime(), newTimeSlot.getEndTime()));
        }
    }
}