package CF.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.AckInfo;
import CF.sendable.ErrorInfo;
import CF.sendable.MSData;
import CF.sendable.MSDataList;

/**
 * Class for creating messages with main category Info.
 */
public class InfoMessageBuilder {
    private static final Logger log = LogManager.getLogger(InfoMessageBuilder.class);

    private InfoMessageBuilder() {
    }

    public static Message createPingMessage(MSData sender, MSData receiver) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Ping").setPayload(sender);
        log.trace("Ping message created");
        return messageFactory.build();
    }

   public static Message createRegisterMessage(MSData sender, String receiverAddress, int receiverPort) {
        MessageFactory messageFactory = new MessageFactory();
        messageFactory.setCategory(ECategory.Info, "Register")
                .setSenderID(sender.getId())
                .setSenderAddress(sender.getAddress())
                .setSenderPort(sender.getPort())
                .setReceiverAddress(receiverAddress)
                .setReceiverPort(receiverPort)
                .setPayload(sender);
        log.trace("Register message created");
        return messageFactory.build();
    }

    public static Message createUnregisterMessage(MSData sender, MSData receiver) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Unregister").setPayload(sender);
        log.trace("Unregister message created");
        return messageFactory.build();
    }

    /**
     * Creates an error message with the given error name and error message.
     */
    public static Message createErrorMessage(MSData sender, MSData receiver, String errorName, String errorMessage) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Error").setPayload(new ErrorInfo(errorName, errorMessage));
        log.trace("Error message created");
        return messageFactory.build();
    }

    /**
     * Creates an ack message for the given message.
     */
    public static Message createAckMessage(Message message) {
        AckInfo ack = new AckInfo(message.getMessageID(), message.getCategory(), message.getSenderPort(), message.getReceiverPort());
        Message ackMessage = MessageFactory.reverse(message);
        ackMessage.setCategory(ECategory.Info, "Ack");
        ackMessage.setPayload(ack);
        log.trace("Ack created for {}", ack.getMessageID());
        return ackMessage;
    }

    public static Message createSyncMessage(MSData sender, MSData receiver, MSDataList msDataList) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Sync").setPayload(msDataList);
        log.trace("Sync message created");
        return messageFactory.build();
    }

    private static MessageFactory senderAndReceiverTemplate(MSData sender, MSData receiver) {
        MessageFactory messageFactory = new MessageFactory();
        return messageFactory.setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort());
    }
}
