package msExchange.messageHandling;

import exceptions.MessageProcessingException;
import mainPackage.ESubCategory;
import msExchange.networkCommunication.CommunicationExchange;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.EServiceType;
import sendable.MSData;
import sendable.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    private CommunicationExchange communicationExchange;

    public MessageBuilder(CommunicationExchange communication) {
        this.communicationExchange = communication;
    }

    public void sendErrorMessage(Message message, MessageProcessingException e) {
        //TODO: implement
    }

    public Message buildCapacityMessage() {
        MSData receiverMS = communicationExchange.getBroker().getServicesByType(EServiceType.Exchange).get(0);
        return senderAndReceiverTemplate(receiverMS).setCategory(ECategory.Exchange, String.valueOf(ESubCategory.Capacity)).build();
    }

    public List<Message> buildMessage(Transaction transaction) {
        List<Message> messages = new ArrayList<>();
        List<MSData> receiverMS = new ArrayList<>();

        receiverMS.add(communicationExchange.getBroker().findService(transaction.getSellerID()));
        receiverMS.add(communicationExchange.getBroker().findService(transaction.getBuyerID()));

        for (MSData msData : communicationExchange.getBroker().getServicesByType(EServiceType.Storage)) {
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
        MSData sender = communicationExchange.getBroker().getCurrentService();
        return messageFactory.setSenderID(sender.getId())
                .setSenderAddress(sender.getAddress())
                .setSenderPort(sender.getPort())
                .setReceiverID(reciever.getId())
                .setReceiverAddress(reciever.getAddress())
                .setReceiverPort(reciever.getPort());
    }
}
