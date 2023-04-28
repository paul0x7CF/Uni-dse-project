package broker;

import protocol.Message;
import java.util.UUID;
import java.util.concurrent.*;

public class AckHandler {
    private static final int TIMEOUT = 5; // seconds to wait for ack before resending
    private final ConcurrentMap<UUID, Message> pendingAcks;
    private final ScheduledExecutorService executorService;
    private final IAckCallback callback;

    public AckHandler(IAckCallback callback) {
        this.callback = callback;
        this.pendingAcks = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    public void trackMessage(Message message) {
        UUID messageId = message.getMessageID();
        pendingAcks.put(messageId, message);

        executorService.schedule(() -> {
            if (pendingAcks.remove(messageId) != null) {
                callback.resendMessage(message);
            }
        }, TIMEOUT, TimeUnit.SECONDS);
    }

    public void ackReceived(UUID messageId) {
        pendingAcks.remove(messageId);
    }
}