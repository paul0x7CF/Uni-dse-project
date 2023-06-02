package msExchange.messageHandling;

import broker.BrokerRunner;
import exceptions.MessageProcessingException;
import mainPackage.ESubCategory;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.EServiceType;
import sendable.MSData;
import sendable.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    private BrokerRunner broker;

    public MessageBuilder(BrokerRunner broker) {
        this.broker = broker;
    }

    public void sendErrorMessage(Message message, MessageProcessingException e) {
        //TODO: implement
    }

    public void sendCapacityMessage() {
        //TODO: implement
    }

    public List<Message> buildMessage(Transaction transaction) {
        List<Message> messages = new ArrayList<>();
        List<MSData> receiverMS = new ArrayList<>();

        receiverMS.add(broker.findService(transaction.getSellerID()));
        receiverMS.add(broker.findService(transaction.getBuyerID()));

        for (MSData msData : broker.getServicesByType(EServiceType.Storage)) {
            receiverMS.add(msData);
        }

        for (MSData msData : receiverMS) {
            messages.add(buildTransactionMessage(senderAndReceiverTemplate(msData), transaction));
        }

        return messages;
    }

    private Message buildTransactionMessage(MessageFactory messageFactory, Transaction transaction) {
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Transaction)).setPayload(transaction);
        return messageFactory.build();
    }

    public MessageFactory senderAndReceiverTemplate(MSData reciever) {
        MessageFactory messageFactory = new MessageFactory();
        MSData sender = broker.getCurrentService();
        return messageFactory.setSenderID(sender.getId())
                .setSenderAddress(sender.getAddress())
                .setSenderPort(sender.getPort())
                .setReceiverID(reciever.getId())
                .setReceiverAddress(reciever.getAddress())
                .setReceiverPort(reciever.getPort());
    }
}
