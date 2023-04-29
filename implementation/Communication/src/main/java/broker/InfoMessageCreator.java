package broker;

import mainPackage.MainForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageBuilder;
import sendable.AckInfo;
import sendable.ErrorInfo;
import sendable.MSData;

import java.util.UUID;

public class InfoMessageCreator {
    private static final Logger logger = LogManager.getLogger(InfoMessageCreator.class);

    private InfoMessageCreator() {
    }

    public static Message createPingMessage(MSData sender, MSData receiver) {
        MessageBuilder messageBuilder = senderAndReceiverTemplate(sender, receiver);
        messageBuilder.setCategory(ECategory.Info, "Ping").
                setPayload(sender);
        return messageBuilder.build();
    }

   public static Message createRegisterMessage(MSData sender, MSData receiver) {
       MessageBuilder messageBuilder = senderAndReceiverTemplate(sender, receiver);
        messageBuilder.setCategory(ECategory.Info, "Register").
            setPayload(sender);
        logger.trace("Register message created");
        return messageBuilder.build();
    }

    public static Message createUnregisterMessage(MSData sender, MSData receiver) {
        MessageBuilder messageBuilder = senderAndReceiverTemplate(sender, receiver);
        messageBuilder.setCategory(ECategory.Info, "Unregister").
            setPayload(sender);
        return messageBuilder.build();
    }

    public static Message createErrorMessage(MSData sender, MSData receiver, String errorName, String errorMessage) {
        MessageBuilder messageBuilder = senderAndReceiverTemplate(sender, receiver);
        messageBuilder.setCategory(ECategory.Info, "Error").
                setPayload(new ErrorInfo(errorName, errorMessage));
        return messageBuilder.build();
    }

    public static Message createAckMessage(Message message) {
        Message ackMessage = MessageBuilder.reverse(message);
        ackMessage.setCategory(ECategory.Info, "Ack");
        AckInfo ack = new AckInfo(message.getMessageID());
        ackMessage.setPayload(ack);

        return ackMessage;
    }

    private static MessageBuilder senderAndReceiverTemplate(MSData sender, MSData receiver) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return messageBuilder.setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort());
    }
}
