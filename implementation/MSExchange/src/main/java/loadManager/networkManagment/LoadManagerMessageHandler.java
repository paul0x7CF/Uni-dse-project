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
import mainPackage.ESubCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validator.BidValidator;
import validator.IValidator;
import validator.SellValidator;
import validator.TransactionValidator;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;


public class LoadManagerMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(LoadManagerMessageHandler.class);
    private BlockingQueue<MessageContent> outgoingQueue;
    private MSData myMSData;
    private ProsumerManager prosumerManager;
    private LoadManager loadManager;


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
            logger.warn("Bid has to be sent back to prosumer");
            MessageContent messageContent = new MessageContent(e.getBid(), EBuildCategory.BidToProsumer);
            try {
                outgoingQueue.put(messageContent);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InvalidSellException e) {
            logger.warn("Sell has to be sent back to prosumer");
            MessageContent messageContent = new MessageContent(e.getSell(), EBuildCategory.SellToProsumer);
            try {
                outgoingQueue.put(messageContent);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IllegalSendableException | ProsumerUnknownException e) {
            logger.warn("Message processing exception: " + e);
        } catch (MessageProcessingException e) {
            throw new RuntimeException(e);
        }

        logger.trace("{} Message processed", message.getCategory());
    }

    private void handleBid(Message message) throws InvalidBidException, IllegalSendableException {
        logger.info("Handling bid");

        BidValidator bidValidator = new BidValidator();
        bidValidator.validateSendable(message.getSendable(Bid.class));
        Bid bid = (Bid) message.getSendable(Bid.class);
        IValidator.validateAuctionID(bid.getAuctionID(), myMSData.getType());
        logger.debug("Bid is valid");

        prosumerManager.handleNewBid(bid);
    }

    private void handleSell(Message message) throws InvalidSellException, IllegalSendableException {
        logger.info("Handling sell");
        SellValidator sellValidator = new SellValidator();
        sellValidator.validateSendable(message.getSendable(Sell.class));

        Sell sell = (Sell) message.getSendable(Sell.class);
        IValidator.validateAuctionID(sell.getAuctionID(), myMSData.getType());
        logger.debug("Sell is valid");

        AtomicReference<ExchangeServiceInformation> exchangeServiceInformation = null;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> {
            try {
                while (true) {
                    try {
                        exchangeServiceInformation.set(loadManager.getFreeExchange());
                        break; //Exits the loop, if no exception 
                    } catch (AllExchangesAtCapacityException e) {
                        logger.debug("All exchanges at capacity, waiting for free exchange");
                        Thread.sleep(1000); //wait 1 sec, then retry
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SellInformation sellInformation = new SellInformation(sell, exchangeServiceInformation.get().getExchangeId());
        prosumerManager.handleNewSell(sellInformation);
    }

    private void handleCapacity(Message message) throws MessageProcessingException {
        logger.info("Handling capacity");
        loadManager.setExchangeAtCapacity(message.getSenderID());
    }

    private void handleTransaction(Message message) throws IllegalSendableException, ProsumerUnknownException {
        logger.info("Handling transaction");
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
