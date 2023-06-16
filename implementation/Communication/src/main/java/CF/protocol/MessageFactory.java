package CF.protocol;

import CF.exceptions.InvalidMessageException;
import CF.messageHandling.InfoMessageHandler;
import CF.sendable.ISendable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * This class will create a message object. It will also validate the message.
 * <p>
 * The setter classes all return the MessageFactory object, so that the builder can be used in a fluent way.
 */
public class MessageFactory {
    private static final Logger log = LogManager.getLogger(MessageFactory.class);

    private String category;
    private UUID senderID;
    private String senderAddress;
    private int senderPort;
    private UUID receiverID;
    private String receiverAddress;
    private int receiverPort;
    private ISendable payload;

    public MessageFactory() {
    }

    /**
     * This method will reverse the sender and receiver of a message.
     *
     * @param   message Message to be reversed
     * @return  Message with sender and receiver reversed
     */
    public static Message reverse(Message message) {
        return new Message(message.getCategory(), message.getReceiverID(), message.getReceiverAddress(),
                message.getReceiverPort(), message.getSenderID(), message.getSenderAddress(),
                message.getSenderPort(), message.getPayload());
    }

    public static boolean validateMessage(Message message) {
        // TODO: if payload is e.g. Ack, category should be Info;Ack
        // TODO: category can only have 0 or 1 semicolons
        if (message.getCategory() == null) {
            log.error("Category is null");
            return false;
        }
        return true;
    }

    public MessageFactory setCategory(ECategory mainCat, String subCat) {
        this.category = mainCat.toString() + ";" + subCat;
        return this;
    }

    public MessageFactory setSenderID(UUID senderID) {
        this.senderID = senderID;
        return this;
    }

    public MessageFactory setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public MessageFactory setSenderPort(int senderPort) {
        this.senderPort = senderPort;
        return this;
    }

    public MessageFactory setReceiverID(UUID receiverID) {
        this.receiverID = receiverID;
        return this;
    }

    public MessageFactory setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
        return this;
    }

    public MessageFactory setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
        return this;
    }

    public MessageFactory setPayload(ISendable payload) {
        this.payload = payload;
        return this;
    }

    public Message build() {
        Message message = new Message(category, senderID, senderAddress, senderPort,
                receiverID, receiverAddress, receiverPort, PayloadConverter.toJSON(payload));
        if (validateMessage(message)) {
            return message;
        } else {
            log.error("Message is not valid, returning null");
            return null;
        }
    }
}
