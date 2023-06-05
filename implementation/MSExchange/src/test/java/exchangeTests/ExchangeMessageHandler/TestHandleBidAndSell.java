package exchangeTests.ExchangeMessageHandler;

public class TestHandleBidAndSell {
/*
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

        BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
        CommunicationExchange communication = new CommunicationExchange(incomingMessages, 1);

        CommunicationLoadManager communicationLoadManager = new CommunicationLoadManager(incomingMessages);
        MessageBuilder messageBuilder = new MessageBuilder(communicationLoadManager);

        MessageContent messageContent = getMessageContent(sellInformation);
        Message message = null;
        try {
            List<Message> messages = messageBuilder.buildMessage(messageContent);
            message = messages.get(0);
        } catch (IllegalSendableException e) {
            throw new RuntimeException(e);
        }

        MSData receiver = Mockito.mock(MSData.class); // Create a mock object for the receiver
        Mockito.when(receiver.getId()).thenReturn(UUID.randomUUID()); // Set the required behavior for the mock object

        //TODO: MessageBuilder from LoadManager
        messageHandler.getAuctionManager().addTimeSlots(timeSlot);

        Message finalMessage = message;
        Assertions.assertDoesNotThrow(() -> messageHandler.handleMessage(finalMessage));
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

        BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
        CommunicationLoadManager communication = new CommunicationLoadManager(incomingMessages);
        MessageBuilder messageBuilder = new MessageBuilder(communication);

        MessageContent messageContent = getMessageContent(sellInformation);
        Message message = null;
        try {
            List<Message> messages = messageBuilder.buildMessage(messageContent);
            message = messages.get(0);
        } catch (IllegalSendableException e) {
            throw new RuntimeException(e);
        }
        messageHandler.getAuctionManager().addTimeSlots(timeSlot);


        Bid bid = new Bid(23, 23, slotID, userID);
        bid.setAuctionID(auctionID);

        Message finalMessage = message;
        Assertions.assertDoesNotThrow(() -> messageHandler.handleMessage(finalMessage));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(userID, messageHandler.getAuctionManager().getAuctions().get(auctionID).getBidderID());
    }

    private MessageContent getMessageContent(SellInformation sellInformation) {
        Sell sell = sellInformation.getSell();
        EBuildCategory category = EBuildCategory.SellToExchange;
        category.setUUID(sellInformation.getExchangeID());
        return new MessageContent(sell, category);
    }*/
}
