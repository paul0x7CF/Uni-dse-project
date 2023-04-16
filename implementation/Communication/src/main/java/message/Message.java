package message;


import sendable.EServiceType;

import java.util.UUID;

public class Message {
    private final UUID id = UUID.randomUUID();
    private String category;
    private EServiceType type;
    private UUID senderID;
    private String senderAddress;
    private String senderPort;
    private String receiverAddress;
    private String payload;

    public Message(String category, EServiceType type, UUID senderID, String senderAddress, String senderPort, String receiverAddress, String payload) {
        this.category = category;
        this.type = type;
        this.senderID = senderID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverAddress = receiverAddress;
        this.payload = payload;
    }

    public UUID getID() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public EServiceType getType() {
        return type;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getSenderPort() {
        return senderPort;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getPayload() {
        return payload;
    }

    public <T> T getXmlClass() {
        // TODO: implement
        return null;
    }

    public void setSenderAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setReceiverAddress(String temp) {
        this.receiverAddress = temp;
    }
}
