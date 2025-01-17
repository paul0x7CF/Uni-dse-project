package CF.sendable;

import java.util.UUID;

/**
 * AckInfo is a class that contains the information for an Acknowledgement message.
 */
public class AckInfo implements ISendable {
    private final UUID messageID;
    private final String category;
    private final int senderPort;
    private final int receiverPort;

    public AckInfo(UUID messageID, String category, int senderPort, int receiverPort) {
        this.messageID = messageID;
        this.category = category;
        this.senderPort = senderPort;
        this.receiverPort = receiverPort;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public String getCategory() {
        return category;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public int getReceiverPort() {
        return receiverPort;
    }
}
