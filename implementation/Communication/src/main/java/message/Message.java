package message;


import sendable.EMSType;

import java.util.UUID;

public class Message {
    private String category;
    private EMSType type;
    private UUID senderID;
    private String senderAddress;
    private String senderPort;
    private String receiverAddress;







    @Override
    public String toString() {
        return "Message{" +
                "category='" + category + '\'' +
                ", senderID=" + senderID +
                '}';
    }
}
