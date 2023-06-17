package CF.broker;

import CF.mainPackage.ConfigReader;
import CF.protocol.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to keep track of received messages and to check if a message has already been received. This is
 * necessary because the broker may receive the same message multiple times due to resending of messages through the
 * AckHandler. The MessageHandler should only process a message once.
 */
public class ReceivedMessages {
    private static final Logger log = LogManager.getLogger(ReceivedMessages.class);

    private final Map<UUID, Message> receivedMessages;
    private final Map<UUID, Instant> messageTimestamps;
    private final Duration messageRetentionDuration;

    public ReceivedMessages() {
        receivedMessages = new HashMap<>();
        messageTimestamps = new HashMap<>();

        ConfigReader configReader = new ConfigReader();
        int duration = Integer.parseInt(configReader.getProperty("ackTimeout")) * 10; // 10 times the ack timeout
        messageRetentionDuration = Duration.ofSeconds(duration);
    }

    public void saveMessage(Message message) {
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
        boolean isPresent = receivedMessages.containsKey(messageID);
        log.debug("{}: Message {} already received: {}", message.getReceiverPort(), message.getSubCategory(), isPresent);
        return isPresent;
    }
}
