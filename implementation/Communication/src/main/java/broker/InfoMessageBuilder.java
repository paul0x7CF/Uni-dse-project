package broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.AckInfo;
import sendable.ErrorInfo;
import sendable.MSData;

public class InfoMessageBuilder { // TODO: rename to InfoMessageTemplate
    private static final Logger log = LogManager.getLogger(InfoMessageBuilder.class);

    private InfoMessageBuilder() {
    }

    public static Message createPingMessage(MSData sender, MSData receiver) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Ping").setPayload(sender);
        log.trace("Ping message created");
        return messageFactory.build();
    }

   public static Message createRegisterMessage(MSData sender, MSData receiver) {
       MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Register").setPayload(sender);
        log.trace("Register message created");
        return messageFactory.build();
    }

    public static Message createUnregisterMessage(MSData sender, MSData receiver) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Unregister").setPayload(sender);
        log.trace("Unregister message created");
        return messageFactory.build();
    }

    public static Message createErrorMessage(MSData sender, MSData receiver, String errorName, String errorMessage) {
        MessageFactory messageFactory = senderAndReceiverTemplate(sender, receiver);
        messageFactory.setCategory(ECategory.Info, "Error").setPayload(new ErrorInfo(errorName, errorMessage));
        log.trace("Error message created");
        return messageFactory.build();
    }

    public static Message createAckMessage(Message message) {
        Message ackMessage = MessageFactory.reverse(message);
        ackMessage.setCategory(ECategory.Info, "Ack");
        AckInfo ack = new AckInfo(message.getMessageID());
        ackMessage.setPayload(ack);
        log.trace("Ack message created");

        return ackMessage;
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
