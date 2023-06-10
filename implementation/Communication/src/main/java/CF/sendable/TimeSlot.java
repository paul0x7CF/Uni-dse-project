package CF.sendable;

import java.time.LocalDateTime;
import java.util.UUID;

public class TimeSlot implements ISendable {
    private final UUID timeSlotID = UUID.randomUUID();
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getTimeSlotID() {
        return timeSlotID;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
