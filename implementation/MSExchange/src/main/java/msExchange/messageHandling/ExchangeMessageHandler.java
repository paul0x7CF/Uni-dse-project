package msExchange.messageHandling;

import Exceptions.IllegalSendableException;
import Exceptions.InvalidBidException;
import Exceptions.InvalidSellException;
import Exceptions.InvalidTimeSlotException;
import exceptions.MessageProcessingException;
import mainPackage.ESubCategory;
import msExchange.auctionManagement.AuctionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.*;
import validator.BidValidator;
import validator.IValidator;
import validator.SellValidator;
import validator.TimeSlotValidator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExchangeMessageHandler {
    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);
    private BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Sell> sellQueue = new LinkedBlockingQueue<>();
    private AuctionManager auctionManager;

    public ExchangeMessageHandler(BlockingQueue<Transaction> outgoingTransactions) {
        auctionManager = new AuctionManager(outgoingTransactions, bidQueue, sellQueue);
        Thread auctionManagerThread = new Thread(auctionManager);
        auctionManagerThread.start();
    }

    /**
     * Handles the incoming message and performs the appropriate actions based on the message's subcategory.
     *
     * @param message The incoming message to handle
     * @throws MessageProcessingException if there is an error processing the message
     */
    public void handleMessage(Message message) throws MessageProcessingException {
        String subcategory = message.getSubCategory();
        if (subcategory.contains(";")) {
            throw new MessageProcessingException("Subcategory has another subcategory: " + subcategory);
        }

        ESubCategory subCategory = ESubCategory.valueOf(subcategory);

        try {
            switch (subCategory) {
                case Bid -> handleBid(message);
                case Sell -> handleSell(message);
                case TimeSlot -> handleTimeSlot(message);
                default ->
                        throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
            }
        } catch (InvalidBidException | InvalidSellException | InvalidTimeSlotException | IllegalSendableException e) {
            throw new MessageProcessingException(e.getMessage());
        }

        logger.trace("{} Message processed", message.getCategory());
    }

    /**
     * Handles the TimeSlot message by validating the TimeSlot and adding it to the AuctionManager.
     *
     * @param message The TimeSlot message to handle
     * @throws InvalidTimeSlotException if the TimeSlot is invalid
     */
    private void handleTimeSlot(Message message) throws InvalidTimeSlotException {
        logger.trace("Handling TimeSlot message");
        TimeSlot timeSlot = (TimeSlot) message.getSendable(TimeSlot.class);
        TimeSlotValidator timeSlotValidator = new TimeSlotValidator();
        timeSlotValidator.validateTimeSlot(timeSlot, auctionManager.getTimeSlots());

        //add timeSlot to auctionManager
        auctionManager.addTimeSlots(timeSlot);
        logger.trace("Added TimeSlot: " + timeSlot);
    }

    /**
     * Handles the Sell message by validating the Sell and adding it to the sellQueue.
     *
     * @param message The Sell message to handle
     * @throws InvalidSellException if the Sell is invalid
     */
    private void handleSell(Message message) throws InvalidSellException, IllegalSendableException {
        SellValidator sellValidator = new SellValidator();
        sellValidator.validateSendable(message.getSendable(Sell.class));

        Sell sell = (Sell) message.getSendable(Sell.class);
        IValidator.validateAuctionID(sell.getAuctionID(), EServiceType.ExchangeWorker);

        //add sell to queue
        sellQueue.add(sell);
        logger.trace("Added Sell: " + sell);
    }

    /**
     * Handles the Bid message by validating the Bid and adding it to the bidQueue.
     *
     * @param message The Bid message to handle
     * @throws InvalidBidException if the Bid is invalid
     */
    private void handleBid(Message message) throws InvalidBidException, IllegalSendableException {
        BidValidator bidValidator = new BidValidator();
        bidValidator.validateSendable(message.getSendable(Bid.class));

        Bid bid = (Bid) message.getSendable(Bid.class);
        IValidator.validateAuctionID(bid.getAuctionID(), EServiceType.ExchangeWorker);

        //add bid to queue
        bidQueue.add(bid);
        logger.trace("Added Bid: " + bid);
    }

    /**
     * Returns the AuctionManager.
     *
     * @return The AuctionManager
     */
    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
}
