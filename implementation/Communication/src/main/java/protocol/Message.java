package protocol;


import sendable.ISendable;

import java.util.UUID;

public class Message {
    private final UUID messageID = UUID.randomUUID();
    private String category;
    private UUID senderID;
    private String senderAddress;
    private int senderPort;
    private String receiverAddress;
    private String payload;

    public Message(String category, UUID senderID, String senderAddress, int senderPort, String receiverAddress, int receiverPort, String payload) {
        this.category = category;
        this.senderID = senderID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverAddress = receiverAddress;
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

    public String getReceiverAddress() {
        return receiverAddress;
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
