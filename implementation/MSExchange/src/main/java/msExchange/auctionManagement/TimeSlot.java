package msExchange.auctionManagement;

import java.time.LocalDateTime;
import java.util.UUID;

public class TimeSlot {
    private static TimeSlot openTimeSlot;
    private UUID timeSlotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isOpen = false;

    public TimeSlot(UUID timeSlotID, LocalDateTime startTime, LocalDateTime endTime) {
        this.timeSlotId = timeSlotID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void openTimeSlot() {
        if (openTimeSlot != null) {
            openTimeSlot.closeTimeSlot();
        }
        openTimeSlot = this;
        isOpen = true;
    }

    private void closeTimeSlot() {
        if(openTimeSlot == this){
            openTimeSlot=null;
        }
        isOpen = false;
    }

    public UUID getTimeSlotId() {
        return timeSlotId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
