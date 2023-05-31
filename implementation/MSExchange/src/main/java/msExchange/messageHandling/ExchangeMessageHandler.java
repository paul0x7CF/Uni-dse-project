package msExchange.messageHandling;

import exceptions.MessageProcessingException;
import mainPackage.ESubCategory;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;

public class ExchangeMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);

    public void handleMessage(Message message) throws MessageProcessingException {
        String subcategory = message.getSubCategory();
        if (subcategory.contains(";")) {
            throw new MessageProcessingException("Subcategory has another subcategory: " + subcategory);
        }

        ESubCategory subCategory = ESubCategory.valueOf(subcategory);

        switch (subCategory) {
            case Bid -> handleBid(message);
            case Sell -> handleSell(message);
            case TimeSlot -> handleTimeSlot(message);
            case Error -> handleError(message);
            //TODO: only Exception - if the receiver where especially me -> not when broadcasted
            default -> throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
        }


        logger.trace("{} Message processed", message.getCategory());
    }

    private void handleTimeSlot(Message message) {

    }

    private void handleSell(Message message) {

    }

    private void handleBid(Message message) throws MessageProcessingException {
        Bid bid = (Bid) message.getSendable(Bid.class);
        if (bid == null) {
            logger.error("Received Bid with null payload");
            throw new MessageProcessingException("Payload is null");
        }

    }

    private void handleError(Message message) {

    }
}
