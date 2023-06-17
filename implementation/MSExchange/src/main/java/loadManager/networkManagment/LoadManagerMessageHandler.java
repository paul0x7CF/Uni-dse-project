package loadManager.networkManagment;

import CF.exceptions.MessageProcessingException;
import CF.messageHandling.IMessageHandler;
import CF.protocol.Message;
import CF.sendable.Bid;
import CF.sendable.MSData;
import CF.sendable.Sell;
import CF.sendable.Transaction;
import MSP.Exceptions.*;
import loadManager.SellInformation;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.prosumerActionManagement.ProsumerManager;
import mainPackage.networkHelper.ESubCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validator.BidValidator;
import validator.IValidator;
import validator.SellValidator;
import validator.TransactionValidator;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;


public class LoadManagerMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(LoadManagerMessageHandler.class);
    private final BlockingQueue<MessageContent> outgoingQueue;
    private final MSData myMSData;
    private final ProsumerManager prosumerManager;
    private final LoadManager loadManager;


    public LoadManagerMessageHandler(BlockingQueue<MessageContent> outgoingQueue, MSData msData) {
        this.outgoingQueue = outgoingQueue;
        this.prosumerManager = new ProsumerManager(outgoingQueue);
        this.loadManager = new LoadManager();
        this.myMSData = msData;
    }

    @Override
    public void handleMessage(Message message) {
        String subCategory = message.getSubCategory();
        if (subCategory.contains(";")) {
            throw new RuntimeException("LOAD_MANAGER: Subcategory has another subcategory: " + subCategory);
        }

        ESubCategory subCategoryEnum = ESubCategory.valueOf(subCategory);

        try {
            switch (subCategoryEnum) {
                case Bid -> handleBid(message);
                case Sell -> handleSell(message);
                case Transaction -> handleTransaction(message);
                case Capacity -> handleCapacity(message);
                default ->
                        throw new MessageProcessingException("LOAD_MANAGER: Unknown message subCategory: {}" + message.getSubCategory());
            }
        } catch (InvalidBidException e) {
            logger.error("LOAD_MANAGER: Bid has to be sent back to prosumer {}. Error Message: {}", e.getBid().getPrice(), e.getMessage());
            MessageContent messageContent = new MessageContent(e.getBid(), EBuildCategory.BidToProsumer);
            try {
                outgoingQueue.put(messageContent);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InvalidSellException e) {
            logger.error("LOAD_MANAGER: Sell has to be sent back to prosumer {}. Error Message: {}", e.getSell().getAskPrice(), e.getMessage());
            MessageContent messageContent = new MessageContent(e.getSell(), EBuildCategory.SellToProsumer);
            try {
                outgoingQueue.put(messageContent);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IllegalSendableException | ProsumerUnknownException e) {
            logger.warn("LOAD_MANAGER: Message processing exception: {}", e.getMessage());
        } catch (MessageProcessingException e) {
            throw new RuntimeException(e);
        }

        logger.trace("LOAD_MANAGER: {} Message processed", message.getCategory());
    }

    private void handleBid(Message message) throws InvalidBidException, IllegalSendableException {
        logger.trace("LOAD_MANAGER: Handling bid");
        BidValidator bidValidator = new BidValidator();
        bidValidator.validateSendable(message.getSendable(Bid.class));
        Bid bid = (Bid) message.getSendable(Bid.class);
        IValidator.validateAuctionID(bid.getAuctionID(), myMSData.getType());
        logger.info("LOAD_MANAGER: Bid is valid: price: {}, volume: {} for TimeSlot: {}", bid.getPrice(), bid.getVolume(), bid.getTimeSlot());
        prosumerManager.handleNewBid(bid);
    }

    private void handleSell(Message message) throws InvalidSellException, IllegalSendableException {
        logger.trace("LOAD_MANAGER: Handling sell");
        SellValidator sellValidator = new SellValidator();
        sellValidator.validateSendable(message.getSendable(Sell.class));

        Sell sell = (Sell) message.getSendable(Sell.class);
        IValidator.validateAuctionID(sell.getAuctionID(), myMSData.getType());
        logger.info("LOAD_MANAGER: Sell is valid: price: {}, volume: {} for TimeSlot: {} ", sell.getAskPrice(), sell.getVolume(), sell.getTimeSlot());

        SellInformation sellInformation = waitForFreeExchange();
        sellInformation.setSell(sell);
        prosumerManager.handleNewSell(sellInformation);
    }

    private SellInformation waitForFreeExchange() {
        SellInformation sellInformation = null;
        boolean exchangeFound = false;

        while (!exchangeFound) {
            try {
                ExchangeServiceInformation exchangeServiceInformation = loadManager.getFreeExchange();
                sellInformation = new SellInformation(exchangeServiceInformation.getExchangeId());
                exchangeFound = true;
            } catch (AllExchangesAtCapacityException e) {
                logger.debug("LOAD_MANAGER: All exchanges at capacity, waiting for free exchange");
                try {
                    Thread.sleep(1000); //wait 1 sec, then retry
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return sellInformation;
    }

    private void handleCapacity(Message message) {
        logger.trace("LOAD_MANAGER: Handling capacity");
        loadManager.setExchangeCapacity(message.getSenderID());
    }

    private void handleTransaction(Message message) throws IllegalSendableException, ProsumerUnknownException {
        logger.trace("LOAD_MANAGER: Handling transaction");
        TransactionValidator transactionValidator = new TransactionValidator();
        transactionValidator.validateSendable(message.getSendable(Transaction.class));

        Transaction transaction = (Transaction) message.getSendable(Transaction.class);
        prosumerManager.handleIncomingTransaction(transaction);
    }

    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        loadManager.addExchangeServiceInformation(exchangeServiceInformation);
    }

    public void endTimeSlot(UUID endedTimeSlotID) throws InvalidTimeSlotException {
        prosumerManager.endTimeSlot(endedTimeSlotID);
    }
}
