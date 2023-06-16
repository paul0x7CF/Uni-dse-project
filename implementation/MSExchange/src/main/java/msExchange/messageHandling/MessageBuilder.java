package msExchange.messageHandling;

import CF.exceptions.MessageProcessingException;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import CF.sendable.Transaction;
import mainPackage.networkHelper.ESubCategory;
import mainPackage.networkHelper.IMessageBuilder;
import msExchange.networkCommunication.CommunicationExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    private static final Logger logger = LogManager.getLogger(MessageBuilder.class);
    private CommunicationExchange communicationExchange;

    public MessageBuilder(CommunicationExchange communication) {
        this.communicationExchange = communication;
    }

    public void sendErrorMessage(Message message, MessageProcessingException e) {
        //TODO: implement
    }

    public Message buildCapacityMessage() {
        MSData receiverMS = communicationExchange.getBroker().getServicesByType(EServiceType.Exchange).get(0);
        return IMessageBuilder.senderAndReceiverTemplate(receiverMS, communicationExchange.getBroker().getCurrentService()).setCategory(ECategory.Exchange, String.valueOf(ESubCategory.Capacity)).build();
    }

    public List<Message> buildMessage(Transaction transaction) {
        logger.debug("Exchange: sending Transaction, seller: " + transaction.getSellerID() + ", buyer: " + transaction.getBuyerID() + ", price: " + transaction.getPrice() + ", volume: " + transaction.getAmount());
        List<Message> messages = new ArrayList<>();
        List<MSData> receiverMS = new ArrayList<>();

        receiverMS.add(communicationExchange.getBroker().findService(transaction.getSellerID()));
        receiverMS.add(communicationExchange.getBroker().findService(transaction.getBuyerID()));

        for (MSData msData : communicationExchange.getBroker().getServicesByType(EServiceType.Storage)) {
            receiverMS.add(msData);
        }

        for (MSData msData : receiverMS) {
            messages.add(buildTransactionMessage(IMessageBuilder.senderAndReceiverTemplate(msData, communicationExchange.getBroker().getCurrentService()), transaction));
        }

        return messages;
    }

    private Message buildTransactionMessage(MessageFactory messageFactory, Transaction transaction) {
        logger.debug("Exchange: in transaction Message");
        messageFactory.setCategory(ECategory.Exchange, String.valueOf(ESubCategory.Transaction)).setPayload(transaction);
        return messageFactory.build();
    }
}
