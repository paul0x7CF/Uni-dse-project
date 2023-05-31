package msExchange.messageHandling;

import Exceptions.InvalidBidException;
import Exceptions.InvalidSellException;
import Exceptions.InvalidTimeSlotException;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import mainPackage.ESubCategory;
import messageHandling.IMessageHandler;
import msExchange.auctionManagement.AuctionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.*;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExchangeMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);
    private BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Sell> sellQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue<>();
    private AuctionManager auctionManager;

    public ExchangeMessageHandler() {
        auctionManager = new AuctionManager(transactionQueue, bidQueue, sellQueue);
    }

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
                case Error -> handleError(message);
                //TODO: only Exception - if the receiver where especially me -> not when broadcasted
                default ->
                        throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
            }
        } catch (InvalidBidException | InvalidSellException | InvalidTimeSlotException e) {
            throw new MessageProcessingException(e.getMessage());
        }

        logger.trace("{} Message processed", message.getCategory());
    }

    private void handleTimeSlot(Message message) throws InvalidTimeSlotException {
        TimeSlot timeSlot = (TimeSlot) message.getSendable(TimeSlot.class);
        TimeSlotValidator timeSlotValidator = new TimeSlotValidator();
        timeSlotValidator.validateTimeSlot(timeSlot, auctionManager.getTimeSlots());

        //add timeSlot to auctionManager
        auctionManager.addTimeSlots(timeSlot);
    }

    private void handleSell(Message message) throws InvalidSellException {
        Sell sell = (Sell) message.getSendable(Sell.class);
        SellValidator sellValidator = new SellValidator();
        sellValidator.validateSell(sell);

        //add sell to queue
        sellQueue.add(sell);
    }

    private void handleBid(Message message) throws InvalidBidException {
        Bid bid = (Bid) message.getSendable(Bid.class);
        BidValidator bidValidator = new BidValidator();
        bidValidator.validateBid(bid);

        //add bid to queue
        bidQueue.add(bid);
    }

    //TODO: check if Günther changet this in inforMessageHandler -> was copied.
    //TODO: maybe declear this as static in IMessageHandler so every implementation can use this
    private void handleError(Message message) throws MessageProcessingException, RemoteException {
        // Error has Error as Payload
        ISendable error = message.getSendable(ErrorInfo.class);
        if (error == null) {
            logger.error("Received Error with null payload");
            throw new MessageProcessingException("Payload is null");
        }
        if (error instanceof ErrorInfo errorInfo) {
            // TODO: How to handle this in each service?
            if (!Objects.equals(errorInfo.getName(), "test")) {
                logger.error("Received remote error: {}", errorInfo.getMessage());
                throw new RemoteException(errorInfo.getName());
            } else {
                logger.error("Received test error: {}", errorInfo.getMessage());
            }
        } else {
            logger.error("Received Error with wrong payload");
            throw new MessageProcessingException("Payload is not of type ErrorInfo");
        }
    }
}
