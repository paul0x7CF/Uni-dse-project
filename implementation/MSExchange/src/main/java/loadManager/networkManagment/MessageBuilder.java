package loadManager.networkManagment;

import Exceptions.IllegalSendableException;
import mainPackage.ESubCategory;
import mainPackage.IMessageBuilder;
import mainPackage.PropertyFileReader;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.*;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    private final int DURATION;
    CommunicationLoadManager communication;
    private TimeSlot lastSentTimeSlot;

    public MessageBuilder(CommunicationLoadManager communication) {
        this.communication = communication;
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        DURATION = Integer.parseInt(propertyFileReader.getDuration());
    }

    public List<Message> buildMessage(MessageContent messageContent) throws IllegalSendableException {
        List<Message> messages = new ArrayList<>();

        switch (messageContent.getBuildCategory()) {
            case BidToProsumer -> messages.add(buildBidToProsumerMessage(messageContent.getContent()));

            case BidToExchange ->
                    messages.add(buildBidToExchangeMessage(messageContent.getContent(), messageContent.getBuildCategory()));

            case SellToProsumer -> messages.add(buildSellToProsumerMessage(messageContent.getContent()));

            case SellToExchange ->
                    messages.add(buildSellToExchangeMessage(messageContent.getContent(), messageContent.getBuildCategory()));
            case Transaction -> messages.addAll(buildTransactionMessages(messageContent.getContent()));

            default ->
                    throw new IllegalSendableException("Illegal message content type: " + messageContent.getBuildCategory());
        }
        return messages;
    }

    private List<Message> buildTransactionMessages(ISendable content) {
        List<Message> messages = new ArrayList<>();
        List<MSData> receiverMS = new ArrayList<>();
        Transaction transaction = (Transaction) content;

        receiverMS.add(communication.getBroker().findService(transaction.getSellerID()));
        receiverMS.add(communication.getBroker().findService(transaction.getBuyerID()));

        for (MSData msData : communication.getBroker().getServicesByType(EServiceType.Storage)) {
            receiverMS.add(msData);
        }

        for (MSData msData : receiverMS) {
            messages.add(buildTransactionMessage(IMessageBuilder.senderAndReceiverTemplate(msData, communication.getBroker().getCurrentService()), transaction));
        }
        return messages;
    }

    private Message buildTransactionMessage(MessageFactory messageFactory, Transaction transaction) {
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Transaction)).setPayload(transaction);
        return messageFactory.build();
    }

    public List<Message> buildTimeSlotMessages(TimeSlot newTimeSlot) {
        List<Message> messages = new ArrayList<>();

        for (MSData msData : communication.getBroker().getServices()) {
            messages.add(buildSlotMessage(IMessageBuilder.senderAndReceiverTemplate(msData, communication.getBroker().getCurrentService()), newTimeSlot));
        }

        return messages;
    }

    private Message buildSlotMessage(MessageFactory messageFactory, TimeSlot timeSlot) {
        messageFactory.setCategory(ECategory.Exchange, String.valueOf(ESubCategory.TimeSlot)).setPayload(timeSlot);
        return messageFactory.build();
    }


    public Message buildBidToProsumerMessage(ISendable content) {
        Bid bid = (Bid) content;
        MSData receiverMS = communication.getBroker().findService(bid.getBidderID());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);
        return messageFactory.build();
    }

    public Message buildBidToExchangeMessage(ISendable content, EBuildCategory buildCategory) {
        Bid bid = (Bid) content;
        MSData receiverMS = communication.getBroker().findService(buildCategory.getUuid());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);
        return messageFactory.build();
    }

    public Message buildSellToProsumerMessage(ISendable content) {
        Sell sell = (Sell) content;
        MSData receiverMS = communication.getBroker().findService(sell.getSellerID());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(sell);
        return messageFactory.build();
    }

    public Message buildSellToExchangeMessage(ISendable content, EBuildCategory buildCategory) {
        Sell sell = (Sell) content;
        MSData receiverMS = communication.getBroker().findService(buildCategory.getUuid());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(sell);
        return messageFactory.build();
    }


}
