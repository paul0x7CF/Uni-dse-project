package message;


import sendable.EMSType;
import sendable.ISendable;

import java.util.UUID;

public class Message {
    private final UUID id = UUID.randomUUID();
    private String category;
    private EMSType type;
    private UUID senderID;
    private String senderAddress;
    private String senderPort;
    private String receiverAddress;
    private String xml;

    public Message(String category, EMSType type, UUID senderID, String senderAddress, String senderPort, String receiverAddress, String xml) {
        this.category = category;
        this.type = type;
        this.senderID = senderID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverAddress = receiverAddress;
        this.xml = xml;
    }

    public UUID getID() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public EMSType getType() {
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

    public String getXml() {
        return xml;
    }

    public <T> T getXmlClass() {
        return null;
    }

    public void setSenderAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setReceiverAddress(String temp) {
        this.receiverAddress = temp;
    }
}
