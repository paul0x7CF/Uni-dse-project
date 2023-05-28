package sendable;

import protocol.ECategory;

import java.util.UUID;

public class AckInfo implements ISendable {
    private final UUID messageID;
    private final String category;

    public AckInfo(UUID messageID, String category) {
        this.messageID = messageID;
        this.category = category;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public String getCategory() {
        return category;
    }
}
