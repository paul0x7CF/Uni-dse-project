package message;

import sendable.EServiceType;

import java.util.UUID;

/**
 * This class will create a message object. It will also validate the message.
 */
public class MessageBuilder {
    private String category;
    private EServiceType type;
    private UUID senderID;
    private String senderAddress;
    private String senderPort;
    private String receiverAddress;
    private String payload;

    public MessageBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public MessageBuilder setType(EServiceType type) {
        this.type = type;
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

    public MessageBuilder setSenderPort(String senderPort) {
        this.senderPort = senderPort;
        return this;
    }

    public MessageBuilder setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
        return this;
    }

    public MessageBuilder setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public Message reverse(Message message) {
        String temp = message.getSenderAddress();
        message.setSenderAddress(message.getReceiverAddress());
        message.setReceiverAddress(temp);
        return message;
    }

    public boolean validateMessage(Message message) {
        return false;
    }

    public Message build() {
        return new Message(category, type, senderID, senderAddress, senderPort, receiverAddress, payload);
    }
}
