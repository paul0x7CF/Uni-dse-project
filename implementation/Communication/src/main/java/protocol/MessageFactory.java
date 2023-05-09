package protocol;

import sendable.ISendable;

import java.util.UUID;

/**
 * This class will create a message object. It will also validate the message. The setter classes all return the
 * MessageFactory object, so that the builder can be used in a fluent way.
 */
public class MessageFactory { // TODO: rename to MessageFactory?
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

    public static Message reverse(Message message) {
        return new Message(message.getCategory(), message.getReceiverID(), message.getReceiverAddress(),
                message.getReceiverPort(), message.getSenderID(), message.getSenderAddress(),
                message.getSenderPort(), message.getPayload());
    }

    public static boolean validateMessage(Message message) {
        // TODO: if payload is e.g. Ack, category should be Info;Ack
        // TODO: category can only have 0 or 1 semicolons
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
            // TODO: throw exception
            return null;
        }
    }
}
