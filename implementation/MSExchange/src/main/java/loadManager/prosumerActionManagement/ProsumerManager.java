package loadManager.prosumerActionManagement;

import Exceptions.InvalidTimeSlotException;
import Exceptions.PriceNotOKException;
import loadManager.SellInformation;
import loadManager.auctionManagement.AuctionManager;
import loadManager.networkManagment.EBuildCategory;
import loadManager.networkManagment.LoadManagerMessageHandler;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.bidManagement.Bidder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.Bid;
import sendable.Transaction;

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
            //TODO send messagte to prosumer that his bid was negativ or zero
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public void handleNewSell(SellInformation sell) {
        try {
            if (averageMechanism.isAskPriceLowEnough(sell.getSell().getAskPrice())) {
                //TODO: check if the exchange Service has been assigned before
                startNewAuction(sell);
            } else {
                //TODO: send message to prosumer that his ask price was not low enough
            }
        } catch (PriceNotOKException e) {
            //TODO: send messagt to prosumer that his ask price was negative or zero
        }
    }

    private void startNewAuction(SellInformation sell) {
        //TODO: build outgoing message for exchange service
        auctionProsumerTracker.addAuction(sell.getSell().getTimeSlot(), sell.getSell().getSellerID());
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

}
