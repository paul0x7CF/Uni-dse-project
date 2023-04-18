package loadManager.timeSlotManagement;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TimeSlotManager {
    private Map<UUID, Date> timeSlots;
    private long durationInSecs = 1000;

    public TimeSlotManager() {
        timeSlots.put(generateRandomUUID(), new Date());
        process();
    }

    private void process() {

    }

    private void addNewTimeSlots() {
    }

    private void deleteOldTimeSlots() {
    }

    private UUID generateRandomUUID() {
        return null;
    }

    public Map<UUID, Date> getTimeSlots() {
        return timeSlots;
    }
}
