package broker;

import exceptions.AckTimeoutException;
import mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.AckInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class AckHandler {
    private static final Logger log = LogManager.getLogger(AckHandler.class);

    private final int timeout; // seconds to wait for ack before resending
    private final Map<UUID, Message> pendingAcks;
    private final ScheduledExecutorService scheduler;
    private final IBroker broker;

    public AckHandler(IBroker broker) {
        this.broker = broker;
        this.pendingAcks = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(30);

        ConfigReader configReader = new ConfigReader();
        this.timeout = Integer.parseInt(configReader.getProperty("ackTimeout"));
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

        scheduler.schedule(() -> {
            if (pendingAcks.remove(messageId) != null) {
                try {
                    broker.sendMessage(message);
                    log.warn("No Ack received, resending {} from: {} to {} mid: {}",
                            message.getSubCategory(), message.getSenderPort(), message.getReceiverPort(), message.getMessageID());
                    throw new AckTimeoutException("Message could not be acknowledged within the given timeout");
                } catch (AckTimeoutException e) {
                    // TODO: don't send an infinite amount of messages
                    broker.sendMessage(message);
                    throw new RuntimeException(e);
                }
            }
        }, timeout, TimeUnit.SECONDS);
    }

    public void ackReceived(AckInfo ack) {
        if (!pendingAcks.containsKey(ack.getMessageID())) {
            log.warn("Received ack for unknown message {}, {}", ack.getCategory(), ack.getMessageID());
            return;
        } else {
            if (pendingAcks.remove(ack.getMessageID()) == null) {
                log.warn("Unknown message {}", ack.getMessageID());
                return;
            }
        }

        log.trace("Received ack for message {}", ack.getMessageID());
    }
}