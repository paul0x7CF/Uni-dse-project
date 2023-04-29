package loadManager.prosumerActionManagement;

import loadManager.SellInformation;
import loadManager.auctionManagement.AuctionManager;
import protocol.Message;
import sendable.Bid;
import sendable.Transaction;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProsumerManager implements Runnable {
    AuctionManager auctionManager;
    AverageMechanism averageMechanism;
    BlockingQueue<Message> prosumerQueue;
    BlockingQueue<Message> outgoingQueue;

    public ProsumerManager(BlockingQueue<Message> prosumerQueue, BlockingQueue<Message> outgoingQueue) {
        this.prosumerQueue = prosumerQueue;
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void run() {

    }

    private void handleNewBid(Bid bid) {
    }

    private void handleNewSell(SellInformation sell) {
    }

    private boolean priceOK(double price) {
        return false;
    }

    private void startNewAuction(SellInformation sell) {
    }

    public void handleUnsatisfiedBiddersAndSellers(UUID timeSlotID) {
    }

    public void handleTransaction(Transaction transaction) {
    }

}
