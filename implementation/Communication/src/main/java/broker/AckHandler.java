package broker;

import exceptions.AckTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.AckInfo;

import java.util.UUID;
import java.util.concurrent.*;

public class AckHandler {
    private static final Logger logger = LogManager.getLogger(AckHandler.class);

    private static final int TIMEOUT = 5; // seconds to wait for ack before resending TODO: make configurable
    private final ConcurrentMap<UUID, Message> pendingAcks;
    private final ScheduledExecutorService executorService;
    private final IBroker broker;

    public AckHandler(IBroker broker) {
        this.broker = broker;
        this.pendingAcks = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Tracks a message and schedules a task to resend it if no ack is received within the timeout.
     *
     * @param message The message to track.
     */
    public void trackMessage(Message message) {
        UUID messageId = message.getMessageID();
        logger.trace("Tracking message with id {}", messageId);
        pendingAcks.put(messageId, message);

        executorService.schedule(() -> {
            if (pendingAcks.remove(messageId) != null) {
                try {
                    broker.sendMessage(message);
                    throw new AckTimeoutException("Message could not be acknowledged within the given timeout");
                } catch (AckTimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        }, TIMEOUT, TimeUnit.SECONDS);
    }

    public void ackReceived(AckInfo ack) {
        logger.trace("Received ack for message with id {}", ack.getMessageID());
        pendingAcks.remove(ack.getMessageID());
    }
}