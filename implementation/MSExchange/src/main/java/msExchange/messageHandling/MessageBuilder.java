package msExchange.messageHandling;

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

    public Message buildCapacityMessage() {
        while (communicationExchange.getBroker().getServicesByType(EServiceType.Exchange).isEmpty()) {
            logger.trace("EXCHANGE: Waiting for Exchange to register.");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MSData receiverMS = communicationExchange.getBroker().getServicesByType(EServiceType.Exchange).get(0);
        logger.trace("EXCHANGE: Sending capacity message.");
        return IMessageBuilder.senderAndReceiverTemplate(receiverMS, communicationExchange.getBroker().getCurrentService()).setCategory(ECategory.Exchange, String.valueOf(ESubCategory.Capacity)).build();
    }

    public List<Message> buildMessage(Transaction transaction) {
        logger.info("EXCHANGE: Transaction: seller: " + transaction.getSellerID() + ", buyer: " + transaction.getBuyerID() + ", price: " + transaction.getPrice() + ", volume: " + transaction.getAmount());
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
        logger.trace("Exchange: in transaction Message");
        messageFactory.setCategory(ECategory.Exchange, String.valueOf(ESubCategory.Transaction)).setPayload(transaction);
        return messageFactory.build();
    }
}
