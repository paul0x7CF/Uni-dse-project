package sendable;

import java.util.UUID;

public class AckInfo implements ISendable {
    private final UUID messageID;

    public AckInfo(UUID messageID) {
        this.messageID = messageID;
    }

    public UUID getMessageID() {
        return messageID;
    }
}
