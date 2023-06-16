package loadManager.networkManagment;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.*;
import MSP.Exceptions.IllegalSendableException;
import mainPackage.ESubCategory;
import mainPackage.IMessageBuilder;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageBuilder {
    private static final Logger logger = LogManager.getLogger(MessageBuilder.class);
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
            case BidToStorage -> messages.addAll(buildBidToStorageMessage(messageContent.getContent()));
            case SellToStorage -> messages.addAll(buildSellToStorageMessage(messageContent.getContent()));
            default ->
                    throw new IllegalSendableException("Illegal message content type: " + messageContent.getBuildCategory());
        }
        return messages;
    }

    private List<Message> buildSellToStorageMessage(ISendable content) {
        Sell sell = (Sell) content;
        List<Message> messages = new ArrayList<>();
        List<MSData> receiverMS = new ArrayList<>();
        //TODO: Stoarage ID - wait till Stoarge is there ->

        Transaction transaction;
        UUID buyerID = UUID.randomUUID();
        if (!communication.getBroker().getServicesByType(EServiceType.Storage).isEmpty()) {
            MSData storage = communication.getBroker().getServicesByType(EServiceType.Storage).get(0);
            buyerID = storage.getId();
        }

        transaction = new Transaction(sell.getSellerID(), buyerID, sell.getVolume(), sell.getAskPrice(), UUID.randomUUID());

        receiverMS.add(communication.getBroker().findService(transaction.getSellerID()));

        for (MSData msData : communication.getBroker().getServicesByType(EServiceType.Storage)) {
            receiverMS.add(msData);
        }

        for (MSData msData : receiverMS) {
            messages.add(buildTransactionMessage(IMessageBuilder.senderAndReceiverTemplate(msData, communication.getBroker().getCurrentService()), transaction));
        }
        return messages;
    }

    private List<Message> buildBidToStorageMessage(ISendable content) {
        List<Message> messages = new ArrayList<>();
        List<MSData> receiverMS = new ArrayList<>();
        Bid bid = (Bid) content;

        Transaction transaction;
        UUID sellerID = UUID.randomUUID();
        if (!communication.getBroker().getServicesByType(EServiceType.Storage).isEmpty()) {
            MSData storage = communication.getBroker().getServicesByType(EServiceType.Storage).get(0);
            sellerID = storage.getId();
        }

        transaction = new Transaction(sellerID, bid.getBidderID(), bid.getVolume(), bid.getPrice(), UUID.randomUUID());

        receiverMS.add(communication.getBroker().findService(transaction.getBuyerID()));

        for (MSData msData : communication.getBroker().getServicesByType(EServiceType.Storage)) {
            receiverMS.add(msData);
        }

        for (MSData msData : receiverMS) {
            messages.add(buildTransactionMessage(IMessageBuilder.senderAndReceiverTemplate(msData, communication.getBroker().getCurrentService()), transaction));
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
        messageFactory.setCategory(ECategory.Exchange, String.valueOf(ESubCategory.Transaction)).setPayload(transaction);
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


    private Message buildBidToProsumerMessage(ISendable content) {
        Bid bid = (Bid) content;
        MSData receiverMS = communication.getBroker().findService(bid.getBidderID());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);
        return messageFactory.build();
    }

    private Message buildBidToExchangeMessage(ISendable content, EBuildCategory buildCategory) {
        Bid bid = (Bid) content;
        MSData receiverMS = communication.getBroker().findService(buildCategory.getUuid());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);
        return messageFactory.build();
    }

    private Message buildSellToProsumerMessage(ISendable content) {
        logger.debug("Building sell to prosumer message");
        Sell sell = (Sell) content;
        MSData receiverMS = communication.getBroker().findService(sell.getSellerID());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(sell);
        return messageFactory.build();
    }

    private Message buildSellToExchangeMessage(ISendable content, EBuildCategory buildCategory) {
        logger.debug("building sell to exchange message");
        Sell sell = (Sell) content;
        MSData receiverMS = communication.getBroker().findService(buildCategory.getUuid());
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS, communication.getBroker().getCurrentService());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(sell);
        return messageFactory.build();
    }

}
