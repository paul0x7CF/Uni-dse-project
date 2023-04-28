package broker;

import exceptions.AckTimeoutException;
import protocol.Message;
import java.util.UUID;
import java.util.concurrent.*;

public class AckHandler {
    private static final int TIMEOUT = 5; // seconds to wait for ack before resending
    private final ConcurrentMap<UUID, Message> pendingAcks;
    private final ScheduledExecutorService executorService;
    private final IBroker callback;

    public AckHandler(IBroker callback) {
        this.callback = callback;
        this.pendingAcks = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Tracks a message and schedules a task to resend it if no ack is received within the timeout.
     *
     * @param message   The message to track.
     *
     */
    public void trackMessage(Message message) {
        UUID messageId = message.getMessageID();
        pendingAcks.put(messageId, message);

        executorService.schedule(() -> {
            if (pendingAcks.remove(messageId) != null) {
                try {
                    callback.resendMessage(message);
                    throw new AckTimeoutException("Message could not be acknowledged within the given timeout");
                } catch (AckTimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        }, TIMEOUT, TimeUnit.SECONDS);
        /* TODO: When calling the method, do this:

        try {
            trackMessage(message);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof AckTimeoutException) {
            AckTimeoutException ackTimeoutException = (AckTimeoutException) e.getCause();
    }
}


         */
    }

    public void ackReceived(UUID messageId) {
        pendingAcks.remove(messageId);
    }
}