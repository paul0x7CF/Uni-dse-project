package loadManager.timeSlotManagement;

import sendable.TimeSlot;

import java.util.List;

public class TimeSlotManager {
    private final long DURATION_IN_SECS = 1000;
    private List<TimeSlot> timeSlots;

    private void addNewTimeSlots() {
    }

    private void deleteOldTimeSlots() {
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }
}
