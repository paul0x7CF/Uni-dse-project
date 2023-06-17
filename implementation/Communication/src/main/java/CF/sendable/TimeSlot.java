package CF.sendable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A time slot is a period of time with a start and end time. It is the central component of the auction and forecast
 * system.
 */
public class TimeSlot implements ISendable {
    private final UUID timeSlotID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSlotID = UUID.randomUUID();
    }

    // Constructor for testing
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime, UUID timeSlotID) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSlotID = timeSlotID;
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
