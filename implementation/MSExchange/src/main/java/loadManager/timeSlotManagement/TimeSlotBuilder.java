package loadManager.timeSlotManagement;

import CF.sendable.TimeSlot;
import MSP.Exceptions.InvalidTimeSlotException;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TimeSlotBuilder {
    private static final Logger log = LogManager.getLogger(TimeSlotBuilder.class);
    private final long DURATION_IN_SECS;
    private final int MAX_NUM_TIME_SLOTS_SAVED;
    private List<TimeSlot> timeSlots;


    public TimeSlotBuilder() {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        this.DURATION_IN_SECS = Long.parseLong(propertyFileReader.getDuration());
        this.MAX_NUM_TIME_SLOTS_SAVED = Integer.parseInt(propertyFileReader.getMaxNumTimeSlotSaved());
    }


    /*needs to be called every duration
     * adds NUM_NEW_TIME_SLOTS */
    public TimeSlot addNewTimeSlot() throws InvalidTimeSlotException {
        TimeSlot resultTimeSlot;
        if (timeSlots == null) {
            timeSlots = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            resultTimeSlot = addNewTimeSlot(now);
        } else {
            if (timeSlots.get(timeSlots.size() - 1).getEndTime().isAfter(LocalDateTime.now())) {
                log.error("TimeSlotBuilder: addNewTimeSlot: last timeSlot is not finished yet");
                throw new InvalidTimeSlotException("TimeSlotBuilder: addNewTimeSlot: last timeSlot is not finished yet", Optional.ofNullable(timeSlots.get(timeSlots.size() - 1).getTimeSlotID()));
            }

            LocalDateTime start = timeSlots.get(timeSlots.size() - 1).getEndTime();

            resultTimeSlot = addNewTimeSlot(start);
        }
        return resultTimeSlot;
    }

    private TimeSlot addNewTimeSlot(LocalDateTime startTime) {
        LocalDateTime start = startTime;
        LocalDateTime end = start.plusSeconds(DURATION_IN_SECS);

        TimeSlot timeSlot = new TimeSlot(start, end);
        timeSlots.add(timeSlot);

        deleteOldTimeSlots();
        return timeSlot;
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

    public LocalDateTime getLastSlotsEndtime() {
        if (timeSlots == null || timeSlots.size() == 0) {
            log.info("in getLastSlot -> is empty");
            return LocalDateTime.now();
        }

        return timeSlots.get(timeSlots.size() - 1).getEndTime();
    }

    public Optional<UUID> getLastTimeSlot() {
        if (getLastSlotsEndtime().isBefore(LocalDateTime.now())) {
            return Optional.ofNullable(timeSlots.get(timeSlots.size() - 1).getTimeSlotID());
        }
        return Optional.empty();
    }
}
