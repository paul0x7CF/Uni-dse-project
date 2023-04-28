package protocol;


import sendable.ISendable;

import java.util.UUID;

public class Message {
    private final UUID messageID = UUID.randomUUID();
    private final String category;
    private final UUID senderID;
    private String senderAddress;
    private final int senderPort;
    private final String receiverID;
    private String receiverAddress;
    private final int receiverPort;
    private final String payload;

    public Message(String category, UUID senderID, String senderAddress, int senderPort, String receiverID, String receiverAddress, int receiverPort, String payload) {
        this.category = category;
        this.senderID = senderID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverID = receiverID;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.payload = payload;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public String getCategory() {
        return category;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public String getPayload() {
        return payload;
    }

    public void setSenderAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setReceiverAddress(String temp) {
        this.receiverAddress = temp;
    }

    // converts the payload using the PayloadConverter class and forwards the class which should be instantiated
    public ISendable getSendable(Class<? extends ISendable> type) {
        return PayloadConverter.fromJSON(payload, type);
    }

    public ECategory getMainCategory() {
        return ECategory.valueOf(category.split(";")[0]);
    }

    public String getSubCategory() {
        return category.split(";")[1];
    }
}
