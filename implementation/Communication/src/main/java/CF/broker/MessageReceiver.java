package CF.broker;

import CF.protocol.Message;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MessageReceiver {
    private Map<UUID, Message> receivedMessages = new HashMap<>();
    private Map<UUID, Instant> messageTimestamps = new HashMap<>();
    private final Duration messageRetentionDuration = Duration.ofSeconds(60);

    public void receiveMessage(Message message) {
        UUID messageID = message.getMessageID();
        if (!receivedMessages.containsKey(messageID)) {
            receivedMessages.put(messageID, message);
            messageTimestamps.put(messageID, Instant.now());
        }
        cleanupExpiredMessages();
    }

    private void cleanupExpiredMessages() {
        Instant currentInstant = Instant.now();
        messageTimestamps.entrySet().removeIf(entry ->
                Duration.between(entry.getValue(), currentInstant).compareTo(messageRetentionDuration) > 0);
        receivedMessages.keySet().retainAll(messageTimestamps.keySet());
    }

    public boolean isMessageAlreadyReceived(Message message) {
        UUID messageID = message.getMessageID();
        return receivedMessages.containsKey(messageID);
    }
}
