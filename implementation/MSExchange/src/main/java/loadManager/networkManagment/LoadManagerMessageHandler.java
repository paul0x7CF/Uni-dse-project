package loadManager.networkManagment;

import Exceptions.InvalidBidException;
import Validator.BidValidator;
import broker.IServiceBroker;
import exceptions.MessageProcessingException;
import loadManager.prosumerActionManagement.ProsumerManager;
import mainPackage.ESubCategory;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;
import sendable.MSData;


public class LoadManagerMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(LoadManagerMessageHandler.class);
    private final MSData currentService;
    private IServiceBroker broker;
    private ProsumerManager prosumerManager;

    public LoadManagerMessageHandler() {
        this.currentService = broker.getCurrentService();
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException {
        String subCategory = message.getSubCategory();
        if (subCategory.contains(";")) {
            throw new RuntimeException("Subcategory has another subcategory: " + subCategory);
        }

        ESubCategory subCategoryEnum = ESubCategory.valueOf(subCategory);

        try {
            switch (subCategoryEnum) {
                case Bid -> handleBid(message);
                case Sell -> handleSell(message);
                case Transaction -> handleTransaction(message);
                case Capacity -> handleCapacity(message);
                default ->
                        throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
            }
        } catch (InvalidBidException e) {
            throw new MessageProcessingException(e.getMessage());
        }

        logger.trace("{} Message processed", message.getCategory());
    }

    private void handleBid(Message message) throws InvalidBidException {
        logger.trace("Handling bid");
        Bid bid = (Bid) message.getSendable(Bid.class);
        BidValidator bidValidator = new BidValidator();
        bidValidator.validateBid(bid);

        //TODO: implement
    }

    private void handleSell(Message message) {
        logger.info("Handling sell");
    }

    private void handleCapacity(Message message) {
        logger.info("Handling capacity");
    }

    private void handleTransaction(Message message) {
        logger.info("Handling transaction");
    }


}
