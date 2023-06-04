package loadManager.networkManagment;

import Exceptions.AllExchangesAtCapacityException;
import Exceptions.InvalidBidException;
import Exceptions.InvalidSellException;
import Validator.BidValidator;
import Validator.SellValidator;
import broker.IServiceBroker;
import exceptions.MessageProcessingException;
import loadManager.SellInformation;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.prosumerActionManagement.ProsumerManager;
import mainPackage.ESubCategory;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;


public class LoadManagerMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(LoadManagerMessageHandler.class);
    private BlockingQueue<Message> outgoingQueue;
    private IServiceBroker broker;
    private ProsumerManager prosumerManager;
    private LoadManager loadManager;


    public LoadManagerMessageHandler(BlockingQueue<Message> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
        this.prosumerManager = new ProsumerManager(outgoingQueue);
        this.loadManager = new LoadManager();
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
        } catch (InvalidBidException | InvalidSellException e) {
            throw new MessageProcessingException(e.getMessage());
        }

        logger.trace("{} Message processed", message.getCategory());
    }

    private void handleBid(Message message) throws InvalidBidException {
        logger.trace("Handling bid");
        Bid bid = (Bid) message.getSendable(Bid.class);
        BidValidator bidValidator = new BidValidator();

        bidValidator.validateBid(bid, broker.getCurrentService().getType());

        prosumerManager.handleNewBid(bid);
    }

    private void handleSell(Message message) throws InvalidSellException {
        logger.trace("Handling sell");
        Sell sell = (Sell) message.getSendable(Sell.class);
        SellValidator sellValidator = new SellValidator();

        sellValidator.validateSell(sell, broker.getCurrentService().getType());

        AtomicReference<ExchangeServiceInformation> exchangeServiceInformation = null;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> {
            try {
                while (true) {
                    try {
                        exchangeServiceInformation.set(loadManager.getFreeExchange());
                        break; //Exits the loop, if no exception 
                    } catch (AllExchangesAtCapacityException e) {
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

    private void handleCapacity(Message message) {
        logger.info("Handling capacity");
    }

    private void handleTransaction(Message message) {
        logger.info("Handling transaction");
    }


}
