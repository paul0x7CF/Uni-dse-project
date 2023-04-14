package sendable;

import java.time.LocalDateTime;
import java.util.UUID;

public class TimeSlot implements ISendable, IRemoteObject {
    private final UUID id = UUID.randomUUID();
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getID() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
