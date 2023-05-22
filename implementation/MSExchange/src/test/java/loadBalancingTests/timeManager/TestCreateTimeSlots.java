package loadBalancingTests.timeManager;

import loadManager.timeSlotManagement.TimeSlotManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import sendable.TimeSlot;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class TestCreateTimeSlots {
    private long DURATION_IN_SECS;
    private int NUM_NEW_TIME_SLOTS;
    private int MAX_NUM_TIME_SLOTS_SAVED;


    @Test
    public void callMethodxTimes_addNewTimeSlots_expectedMaxNumTimeSlotsSaved() {
        // Arrange
        readProperties();
        TimeSlotManager timeSlotManager = new TimeSlotManager((int) DURATION_IN_SECS, NUM_NEW_TIME_SLOTS);

        // Act
        for (int i = 0; i < (MAX_NUM_TIME_SLOTS_SAVED / NUM_NEW_TIME_SLOTS) + 2; i++) {
            // addNewTimeSlots();
            timeSlotManager.addNewTimeSlots();
        }

        // Assert
        Assertions.assertTrue(timeSlotManager.getTimeSlots().size() <= MAX_NUM_TIME_SLOTS_SAVED);
        Assertions.assertEquals(MAX_NUM_TIME_SLOTS_SAVED, timeSlotManager.getTimeSlots().size());
        checkTimeCorrect(timeSlotManager.getTimeSlots());
    }

    private void checkTimeCorrect(List<TimeSlot> timeSlots) {
        for (int i = 1; i < timeSlots.size(); i++) {
            TimeSlot previousSlot = timeSlots.get(i - 1);
            TimeSlot currentSlot = timeSlots.get(i);

            //check duration between slots
            LocalDateTime differenceBetweenStartTimes = currentSlot.getStartTime().minusSeconds(previousSlot.getStartTime().getSecond());
            assert differenceBetweenStartTimes.getSecond() == DURATION_IN_SECS :
                    "the difference between the start times is not the duration of the time slot.";

            //check duration of a slot
            LocalDateTime differentBetweenEndAndStartTime = currentSlot.getEndTime().minusSeconds(currentSlot.getStartTime().getSecond());
            assert differentBetweenEndAndStartTime.getSecond() == DURATION_IN_SECS :
                    "the difference between the end time and the start time is not the duration of the time slot.";

            //check start time
            assert currentSlot.getStartTime().isAfter(previousSlot.getStartTime()) :
                    "the start time " + i + " isn't after the previous time slot's starttime.";

            //check end time
            assert currentSlot.getEndTime().isAfter(previousSlot.getEndTime()) :
                    "the end time " + i + " isn't after the previous time slot's endtime.";
        }
    }


    private void readProperties() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("src/main/java/config.properties");
            properties.load(configFile);
            configFile.close();

            DURATION_IN_SECS = Long.parseLong(properties.getProperty("timeslot.duration"));
            NUM_NEW_TIME_SLOTS = Integer.parseInt(properties.getProperty("timeslot.numNewTimeSlots"));
            MAX_NUM_TIME_SLOTS_SAVED = Integer.parseInt(properties.getProperty("timeslot.maxNumTimeSlotSaved"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
