package validator;

import Exceptions.InvalidTimeSlotException;
import sendable.TimeSlot;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class TimeSlotValidator {

    public void validateTimeSlot(TimeSlot timeSlot, Map<UUID, msExchange.auctionManagement.TimeSlot> timeSlots) throws InvalidTimeSlotException {
        validateTimeSlotNotNull(timeSlot);
        validateTimeSlotIDNotNull(timeSlot);
        validateTimeSlotIDNotInUse(timeSlot.getTimeSlotID(), timeSlots);
        validateTimeSlotStartBeforeEnd(timeSlot);
        validateTimeSlotIsInFuture(timeSlot);
    }

    private void validateTimeSlotNotNull(TimeSlot timeSlot) throws InvalidTimeSlotException {
        if (timeSlot == null) {
            throw new InvalidTimeSlotException("TimeSlot is null", null);
        }
    }

    private void validateTimeSlotIDNotNull(TimeSlot timeSlot) throws InvalidTimeSlotException {
        if (timeSlot.getTimeSlotID() == null) {
            throw new InvalidTimeSlotException("TimeSlotID is null", null);
        }
    }

    private void validateTimeSlotIDNotInUse(UUID timeSlotID, Map<UUID, msExchange.auctionManagement.TimeSlot> timeSlots) throws InvalidTimeSlotException {
        for (msExchange.auctionManagement.TimeSlot timeSlot : timeSlots.values()) {
            if (timeSlot.getTimeSlotId().equals(timeSlotID)) {
                throw new InvalidTimeSlotException("TimeSlotID is already in use", null);
            }
        }
    }


    private void validateTimeSlotStartBeforeEnd(TimeSlot timeSlot) throws InvalidTimeSlotException {
        if (timeSlot.getStartTime().isAfter(timeSlot.getEndTime())) {
            throw new InvalidTimeSlotException("TimeSlot start is after end", null);
        }
    }

    private void validateTimeSlotIsInFuture(TimeSlot timeSlot) throws InvalidTimeSlotException {
        if (timeSlot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTimeSlotException("TimeSlot start is in the past", null);
        }
    }


}
