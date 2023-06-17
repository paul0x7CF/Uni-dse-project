package loadManager.prosumerActionManagement;

import CF.sendable.Bid;
import CF.sendable.Sell;
import CF.sendable.Transaction;
import MSP.Exceptions.InvalidBidException;
import MSP.Exceptions.InvalidTimeSlotException;
import MSP.Exceptions.PriceNotOKException;
import MSP.Exceptions.ProsumerUnknownException;
import loadManager.SellInformation;
import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.EBuildCategory;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.bidManagement.Bidder;
import loadManager.prosumerActionManagement.priceCalculationStrategy.IPriceMechanism;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProsumerManager {
    private static final Logger logger = LogManager.getLogger(ProsumerManager.class);
    private final AuctionManager auctionManager;
    private final IPriceMechanism priceMechanism;
    private final AuctionProsumerTracker auctionProsumerTracker;
    private final List<Bidder> bidders;
    private final BlockingQueue<MessageContent> outgoingQueue;

    public ProsumerManager(BlockingQueue<MessageContent> outgoingQueue) {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        this.outgoingQueue = outgoingQueue;
        this.auctionManager = new AuctionManager();
        this.priceMechanism = propertyFileReader.getPriceMechanism();
        this.auctionProsumerTracker = new AuctionProsumerTracker();
        this.bidders = new ArrayList<>();
    }

    public void handleNewBid(Bid bid) {
        try {
            if (priceMechanism.isBidPriceHighEnough(bid.getPrice())) {
                for (Bidder bidder : bidders) {
                    if (bidder.getBidderID().equals(bid.getBidderID())) {
                        bidder.handleBid(bid);
                        return;
                    }
                }
                Bidder newBidder = new Bidder(auctionManager, bid.getBidderID(), outgoingQueue, auctionProsumerTracker);
                bidders.add(newBidder);
                newBidder.handleBid(bid);
            } else {
                logger.debug("LOAD_MANAGER: Price did not match the average price {} ... sending Bid back to prosumer: original Price: {}", priceMechanism.getKWPrice(), bid.getPrice());
                if (priceMechanism.getKWPrice() != 0.0) {
                    bid.setPrice(priceMechanism.getKWPrice());
                }
                outgoingQueue.put(new MessageContent(bid, EBuildCategory.BidToProsumer));
            }
        } catch (PriceNotOKException e) {
            logger.warn("LOAD_MANAGER: Prosumer price was not okay. Sending Bid back to prosumer");
            bid.setPrice(0);
            MessageContent messageContent = new MessageContent(bid, EBuildCategory.BidToProsumer);
            try {
                outgoingQueue.put(messageContent);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public void handleNewSell(SellInformation sell) {
        try {
            if (priceMechanism.isAskPriceLowEnough(sell.getSell().getAskPrice())) {
                startNewAuction(sell);
            } else {
                logger.warn("LOAD_MANAGER: Price did not match the average price {} ... sending Sell back to prosumer: original Price: {}", priceMechanism.getKWPrice(), sell.getSell().getAskPrice());

                Sell s = sell.getSell();
                s.setAskPrice(priceMechanism.getKWPrice());
                outgoingQueue.put(new MessageContent(s, EBuildCategory.SellToProsumer));
            }
        } catch (PriceNotOKException e) {
            try {
                outgoingQueue.put(new MessageContent(sell.getSell(), EBuildCategory.SellToProsumer));
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startNewAuction(SellInformation sell) {
        UUID auctionID = UUID.randomUUID();
        logger.debug("LOAD_MANAGER: Starting new Auction with ID: {}", auctionID);
        auctionProsumerTracker.addAuction(sell.getSell().getTimeSlot(), auctionID);
        auctionManager.addAuction(new Auction(auctionID, sell));

        //Build message to exchange
        EBuildCategory category = EBuildCategory.SellToExchange;
        category.setUUID(sell.getExchangeID());
        Sell s = sell.getSell();
        s.setAuctionID(auctionID);
        MessageContent messageContent = new MessageContent(s, category);
        try {
            outgoingQueue.put(messageContent);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleIncomingTransaction(Transaction transaction) throws ProsumerUnknownException {
        logger.debug("LOAD_MANAGER: Incoming Transaction: {} ", transaction.getTransactionID());

        UUID timeSlotID = auctionManager.getAuctionByID(transaction.getAuctionID()).getTIMESLOT_ID();
        Bid bid = new Bid(transaction.getAmount(), transaction.getPrice(), timeSlotID, transaction.getBuyerID());
        try {
            auctionManager.setBidder(transaction.getAuctionID(), bid);
        } catch (InvalidBidException e) {
            throw new RuntimeException(e);
        }

        auctionProsumerTracker.checkWithTransactions(transaction);
    }

    public void endTimeSlot(UUID endedTimeSlotID) throws InvalidTimeSlotException {
        auctionManager.endTimeSlot(endedTimeSlotID);
        for (Bidder bidder : bidders) {
            bidder.endTimeSlot(endedTimeSlotID);
        }

        handleUnsatisfiedSellers(endedTimeSlotID);
    }

    public void handleUnsatisfiedSellers(UUID timeSlotID) throws InvalidTimeSlotException {
        //Unsatisfied Sellers
        Map<UUID, Double> unsatisfiedSellers = auctionManager.getUnsatisfiedSellers(timeSlotID);

        for (Map.Entry<UUID, Double> entry : unsatisfiedSellers.entrySet()) {
            UUID sellerID = entry.getKey();
            Double amount = entry.getValue();
            MessageContent messageContent = new MessageContent(new Sell(amount, priceMechanism.getKWPrice(), timeSlotID, sellerID), EBuildCategory.SellToStorage);
            try {
                outgoingQueue.put(messageContent);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
