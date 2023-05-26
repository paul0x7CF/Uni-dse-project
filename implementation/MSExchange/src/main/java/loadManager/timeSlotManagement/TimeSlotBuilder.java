package loadManager.timeSlotManagement;

import sendable.TimeSlot;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TimeSlotBuilder {
    private final long DURATION_IN_SECS;
    private final int NUM_NEW_TIME_SLOTS;
    private final int MAX_NUM_TIME_SLOTS_SAVED;
    private List<TimeSlot> timeSlots;


    public TimeSlotBuilder(int timeSlotDuration, int numNewTimeSlots) {
        this.DURATION_IN_SECS = timeSlotDuration;
        this.NUM_NEW_TIME_SLOTS = numNewTimeSlots;
        this.MAX_NUM_TIME_SLOTS_SAVED = getMAX_NUM_TIME_SLOTS_SAVED();
    }

    /* reads the config.properties file*/
    private int getMAX_NUM_TIME_SLOTS_SAVED() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("src/main/java/config.properties");
            properties.load(configFile);
            configFile.close();

            return Integer.parseInt(properties.getProperty("timeslot.maxNumTimeSlotSaved"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*needs to be called every duration
     * adds NUM_NEW_TIME_SLOTS */
    public void addNewTimeSlots() {
        if (timeSlots == null) {
            timeSlots = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            addNewTimeSlots(now);
        } else {
            LocalDateTime start = timeSlots.get(timeSlots.size() - 1).getEndTime();
            addNewTimeSlots(start);
        }
    }

    /*called from addNewTimeSlots
     * creating NUM_NEW_TIME_SLOTS starting with startTime
     * calls method for deleting old timeSlots*/
    private void addNewTimeSlots(LocalDateTime startTime) {
        LocalDateTime start = startTime;
        for (int i = 0; i < NUM_NEW_TIME_SLOTS; i++) {
            LocalDateTime end = start.plusSeconds(DURATION_IN_SECS);
            TimeSlot timeSlot = new TimeSlot(start, end);
            timeSlots.add(timeSlot);
            start = end;
        }
        deleteOldTimeSlots();
    }

    /*deletes old timeSlots if there are more than MAX_NUM_TIME_SLOTS_SAVED*/
    private void deleteOldTimeSlots() {
        while (timeSlots.size() > MAX_NUM_TIME_SLOTS_SAVED) {
            timeSlots.remove(0);
        }
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }
}
