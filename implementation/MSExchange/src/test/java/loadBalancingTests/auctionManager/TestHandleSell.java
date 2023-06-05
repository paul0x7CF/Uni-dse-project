package loadBalancingTests.auctionManager;

import Exceptions.InvalidTimeSlotException;
import loadManager.SellInformation;
import loadManager.auctionManagement.Auction;
import loadManager.networkManagment.EBuildCategory;
import loadManager.networkManagment.MessageContent;
import loadManager.prosumerActionManagement.ProsumerManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import sendable.Sell;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestHandleSell {

    @Test
    public void givenSellInformation_handleNewSell_expectedAuctionStarted() {
        BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();
        ProsumerManager prosumerManager = new ProsumerManager(outgoingQueue);

        UUID timeSlotID = UUID.randomUUID();
        Sell sell = new Sell(20, 10, timeSlotID, UUID.randomUUID());
        SellInformation sellInformation = new SellInformation(sell, UUID.randomUUID());

        prosumerManager.handleNewSell(sellInformation);

        Optional<MessageContent> messageContent = outgoingQueue.stream().filter(message -> message.getBuildCategory().equals(EBuildCategory.SellToExchange)).findFirst();
        assert messageContent.isPresent();
        Assertions.assertEquals(sellInformation.getSell(), messageContent.get().getContent());

        try {
            List<Auction> auctions = prosumerManager.getAuctionManager().getAllAuctionsForSlot(timeSlotID);
            Assertions.assertEquals(1, auctions.size());
            Assertions.assertEquals(sellInformation.getSell().getSellerID(), auctions.get(0).getSellerID());

        } catch (InvalidTimeSlotException e) {
            throw new RuntimeException(e);
        }
    }
}
