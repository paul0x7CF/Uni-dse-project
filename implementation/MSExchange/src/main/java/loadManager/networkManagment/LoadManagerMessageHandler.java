package loadManager.networkManagment;

import CF.sendable.*;
import MSP.Exceptions.AllExchangesAtCapacityException;
import MSP.Exceptions.IllegalSendableException;
import MSP.Exceptions.InvalidBidException;
import MSP.Exceptions.InvalidSellException;
import CF.exceptions.MessageProcessingException;
import loadManager.SellInformation;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.prosumerActionManagement.ProsumerManager;
import mainPackage.ESubCategory;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
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
        } catch (InvalidBidException | InvalidSellException | IllegalSendableException e) {
            throw new MessageProcessingException(e.getMessage());
        }

        logger.trace("{} Message processed", message.getCategory());
    }

    private void handleBid(Message message) throws InvalidBidException, IllegalSendableException {
        logger.info("Handling bid");

        BidValidator bidValidator = new BidValidator();
        bidValidator.validateSendable(message.getSendable(ISendable.class));
        Bid bid = (Bid) message.getSendable(Bid.class);
        IValidator.validateAuctionID(bid.getAuctionID(), myMSData.getType());
        logger.trace("Bid is valid");

        prosumerManager.handleNewBid(bid);
    }

    private void handleSell(Message message) throws InvalidSellException, IllegalSendableException {
        logger.trace("Handling sell");
        SellValidator sellValidator = new SellValidator();
        sellValidator.validateSendable(message.getSendable(ISendable.class));

        Sell sell = (Sell) message.getSendable(Sell.class);
        IValidator.validateAuctionID(sell.getAuctionID(), myMSData.getType());
        logger.trace("Sell is valid");

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

    private void handleTransaction(Message message) throws IllegalSendableException {
        logger.info("Handling transaction");
        TransactionValidator transactionValidator = new TransactionValidator();
        transactionValidator.validateSendable(message.getSendable(Transaction.class));

        Transaction transaction = (Transaction) message.getSendable(Transaction.class);
        prosumerManager.handleIncomingTransaction(transaction);
    }

    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        loadManager.addExchangeServiceInformation(exchangeServiceInformation);
    }

    public void endTimeSlot(UUID timeSlotID){
        //TODO: Implement
    }
}
