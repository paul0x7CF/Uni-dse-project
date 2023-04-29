package protocol;

import sendable.ISendable;

import java.util.UUID;

/**
 * This class will create a message object. It will also validate the message. The setter classes all return the
 * MessageBuilder object, so that the builder can be used in a fluent way.
 */
public class MessageBuilder {
    private String category;
    private UUID senderID;
    private String senderAddress;
    private int senderPort;
    private UUID receiverID;
    private String receiverAddress;
    private int receiverPort;
    private ISendable payload;

    public MessageBuilder() {
    }

    public static Message reverse(Message message) {
        UUID tempID = message.getSenderID();
        String tempAddress = message.getSenderAddress();
        int tempPort = message.getSenderPort();
        message.setSenderID(message.getReceiverID());
        message.setSenderAddress(message.getReceiverAddress());
        message.setSenderPort(message.getReceiverPort());
        message.setReceiverID(tempID);
        message.setReceiverAddress(tempAddress);
        message.setReceiverPort(tempPort);
        return message;
    }

    public static boolean validateMessage(Message message) {
        // TODO: if payload is e.g. Ack, category should be Info;Ack
        // TODO: category can only have 0 or 1 semicolons
        return true;
    }

    public MessageBuilder setCategory(ECategory mainCat, String subCat) {
        this.category = mainCat.toString() + ";" + subCat;
        return this;
    }

    public MessageBuilder setSenderID(UUID senderID) {
        this.senderID = senderID;
        return this;
    }

    public MessageBuilder setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public MessageBuilder setSenderPort(int senderPort) {
        this.senderPort = senderPort;
        return this;
    }

    public MessageBuilder setReceiverID(UUID receiverID) {
        this.receiverID = receiverID;
        return this;
    }

    public MessageBuilder setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
        return this;
    }

    public MessageBuilder setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
        return this;
    }

    public MessageBuilder setPayload(ISendable payload) {
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
