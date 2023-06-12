package loadManager.prosumerActionManagement;

import MSP.Exceptions.InvalidTimeSlotException;
import MSP.Exceptions.PriceNotOKException;
import loadManager.SellInformation;
import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.EBuildCategory;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.bidManagement.Bidder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.Bid;
import CF.sendable.Sell;
import CF.sendable.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProsumerManager {
    private static final Logger logger = LogManager.getLogger(ProsumerManager.class);
    AuctionManager auctionManager = new AuctionManager();
    AverageMechanism averageMechanism = new AverageMechanism();
    AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();
    List<Bidder> bidders = new ArrayList<>();
    BlockingQueue<MessageContent> outgoingQueue;

    public ProsumerManager(BlockingQueue<MessageContent> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }

    public void handleNewBid(Bid bid) {

        try {
            if (averageMechanism.isBidPriceHighEnough(bid.getPrice())) {
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
                bid.setPrice(averageMechanism.getAveragePrice());
                outgoingQueue.put(new MessageContent(bid, EBuildCategory.BidToProsumer));
            }
        } catch (PriceNotOKException e) {
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
            if (averageMechanism.isAskPriceLowEnough(sell.getSell().getAskPrice())) {
                startNewAuction(sell);
            } else {
                Sell s = sell.getSell();
                s.setAskPrice(averageMechanism.getAveragePrice());
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
        auctionProsumerTracker.addAuction(sell.getSell().getTimeSlot(), sell.getSell().getSellerID());
        UUID auctionID = UUID.randomUUID();
        auctionManager.addAuction(new Auction(auctionID, sell));

        //build message to exchange
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

    public void handleUnsatisfiedBiddersAndSellers(UUID timeSlotID) throws InvalidTimeSlotException {
        //Unsatisfied Bidders
        List<UUID> unsatisfiedBidders = auctionProsumerTracker.getBiddersNotSatisfied(timeSlotID);
        //TODO: handle storage for unsatisfied bidders

        //Unsatisfied Sellers
        List<UUID> unsatisfiedSellers = auctionManager.getUnsatisfiedSellers(timeSlotID);
        //TODO: handle storage for unsatisfied sellers
    }

    public void handleIncomingTransaction(Transaction transaction) {
        //TODO: set Bidders / Sellers as satisfied
        //TODO: end the auctions

    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public AuctionProsumerTracker getAuctionProsumerTracker() {
        return auctionProsumerTracker;
    }
}
