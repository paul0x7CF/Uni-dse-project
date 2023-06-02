package exchangeTests.ExchangeMessageHandler;

import exceptions.MessageProcessingException;
import loadManager.SellInformation;
import loadManager.networkManagment.ExtendedMessageBuilder;
import loadManager.networkManagment.IMessageBuilder;
import msExchange.messageHandling.ExchangeMessageHandler;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestHandleBidAndSell {

    @Test
    public void receivedSellMessage_handleSell_expectedNewAuction() {
        BlockingQueue<Transaction> outgoingTransactions = new LinkedBlockingQueue<>();
        ExchangeMessageHandler messageHandler = new ExchangeMessageHandler(outgoingTransactions);

        UUID userID = UUID.randomUUID();

        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        UUID slotID = timeSlot.getTimeSlotID();
        UUID auctionID = UUID.randomUUID();

        Sell sell = new Sell(23, 23, slotID, userID);
        sell.setAuctionID(auctionID);
        SellInformation sellInformation = new SellInformation(sell, UUID.randomUUID());

        ExtendedMessageBuilder messageBuilder = new ExtendedMessageBuilder();
        Message message = IMessageBuilder.buildMessageForSell(sellInformation);
        messageHandler.getAuctionManager().addTimeSlots(timeSlot);

        Assertions.assertDoesNotThrow(() -> messageHandler.handleMessage(message));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(1, messageHandler.getAuctionManager().getAuctions().size());
    }

    @Test
    public void receivedBidMessage_handleMessage_expectedBidSetInAuction() {
        BlockingQueue<Transaction> outgoingTransactions = new LinkedBlockingQueue<>();
        ExchangeMessageHandler messageHandler = new ExchangeMessageHandler(outgoingTransactions);

        UUID userID = UUID.randomUUID();

        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        UUID slotID = timeSlot.getTimeSlotID();
        UUID auctionID = UUID.randomUUID();

        Sell sell = new Sell(23, 23, slotID, userID);
        sell.setAuctionID(auctionID);
        SellInformation sellInformation = new SellInformation(sell, UUID.randomUUID());

        ExtendedMessageBuilder messageBuilder = new ExtendedMessageBuilder();
        Message message = IMessageBuilder.buildMessageForSell(sellInformation);
        messageHandler.getAuctionManager().addTimeSlots(timeSlot);

        try {
            messageHandler.handleMessage(message);
        } catch (MessageProcessingException e) {
            throw new RuntimeException(e);
        }

        Bid bid = new Bid(23, 23, slotID, userID);
        bid.setAuctionID(auctionID);

        Message bidMessage = IMessageBuilder.buildMessageSendBid(bid);

        Assertions.assertDoesNotThrow(() -> messageHandler.handleMessage(bidMessage));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(userID, messageHandler.getAuctionManager().getAuctions().get(auctionID).getBidderID());
    }
}
