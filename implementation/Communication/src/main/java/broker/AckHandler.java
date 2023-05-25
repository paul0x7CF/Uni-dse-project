package broker;

import exceptions.AckTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.AckInfo;

import java.util.UUID;
import java.util.concurrent.*;

public class AckHandler {
    private static final Logger log = LogManager.getLogger(AckHandler.class);

    private final int timeout; // seconds to wait for ack before resending
    private final ConcurrentMap<UUID, Message> pendingAcks;
    private final ScheduledExecutorService executorService;
    private final IBroker broker;

    public AckHandler(IBroker broker, int timeout) {
        this.timeout = timeout;
        this.broker = broker;
        this.pendingAcks = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(10);
    }

    /**
     * Tracks a message and schedules a task to resend it if no ack is received within the timeout.
     *
     * @param message The message to track.
     */
    public void trackMessage(Message message) {
        UUID messageId = message.getMessageID();
        log.trace("Tracking message {}", messageId);
        pendingAcks.put(messageId, message);

        executorService.schedule(() -> {
            if (pendingAcks.remove(messageId) != null) {
                try {
                    broker.sendMessage(message);
                    log.warn("No Ack received, resending {} from: {} to {}",
                            message.getSubCategory(), message.getSenderPort(), message.getReceiverPort());
                    throw new AckTimeoutException("Message could not be acknowledged within the given timeout");
                } catch (AckTimeoutException e) {
                    // TODO: send again?
                    broker.sendMessage(message);
                    throw new RuntimeException(e);
                }
            }
        }, timeout, TimeUnit.SECONDS);
    }

    public void ackReceived(AckInfo ack) {
        // TODO: Redundant, but I want to make sure that the message is removed from pendingAcks
        if (!pendingAcks.containsKey(ack.getMessageID())) {
            log.warn("Received ack for unknown message {}", ack.getMessageID());
            return;
        }

        if (pendingAcks.remove(ack.getMessageID()) == null) {
            log.warn("Unknown message {}", ack.getMessageID());
            return;
        }

        log.info("Received ack for message {}", ack.getMessageID());
    }
}