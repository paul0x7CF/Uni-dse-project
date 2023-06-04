package loadManager.prosumerActionManagement;

import Exceptions.InvalidTimeSlotException;
import Exceptions.PriceNotOKException;
import loadManager.SellInformation;
import loadManager.auctionManagement.AuctionManager;
import loadManager.prosumerActionManagement.bidManagement.Bidder;
import protocol.Message;
import sendable.Bid;
import sendable.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProsumerManager {
    AuctionManager auctionManager = new AuctionManager();
    AverageMechanism averageMechanism = new AverageMechanism();
    AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();
    List<Bidder> bidders = new ArrayList<>();
    BlockingQueue<Message> outgoingQueue;

    public ProsumerManager(BlockingQueue<Message> outgoingQueue) {

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
                //TODO send message to prosumer that his bid was not high enough

            }
        } catch (PriceNotOKException e) {
            //TODO send messagte to prosumer that his bid was negativ or zero
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
