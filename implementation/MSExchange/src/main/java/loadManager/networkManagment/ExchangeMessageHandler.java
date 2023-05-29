package loadManager.networkManagment;

import broker.IServiceBroker;
import mainPackage.ESubCategory;
import messageHandling.IMessageHandler;
import protocol.Message;
import sendable.MSData;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ExchangeMessageHandler implements IMessageHandler {
    private static final Logger logger = Logger.getLogger(ExchangeMessageHandler.class.getName());
    private final MSData currentService;
    private BlockingQueue<Message> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;
    private IServiceBroker broker;

    public ExchangeMessageHandler(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages, IServiceBroker communicationBroker) {
        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
        this.broker = communicationBroker;
        this.currentService = broker.getCurrentService();
    }

    @Override
    public void handleMessage(Message message) {
        String subCategory = message.getSubCategory();
        if (subCategory.contains(";")) {
            throw new RuntimeException("Subcategory has another subcategory: " + subCategory);
        }

        ESubCategory subCategoryEnum = ESubCategory.valueOf(subCategory);
        switch (subCategoryEnum) {
            case Bid -> handleBid(message);
            case Sell -> handleSell(message);
            case Transaction -> handleTransaction(message);
            case Capacity -> handleCapacity(message);
        }


    }

    public void handleBid(Message message) {
        logger.info("Handling bid");
    }

    public void handleSell(Message message) {
        logger.info("Handling sell");
    }

    public void handleCapacity(Message message) {
        logger.info("Handling capacity");
    }

    public void handleTransaction(Message message) {
        logger.info("Handling transaction");
    }


}
