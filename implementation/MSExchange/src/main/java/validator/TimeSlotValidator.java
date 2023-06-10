package validator;

import MSP.Exceptions.InvalidTimeSlotException;
import msExchange.messageHandling.ExchangeMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.TimeSlot;

import java.util.Map;
import java.util.UUID;

public class TimeSlotValidator {
    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);

    public void validateTimeSlot(TimeSlot timeSlot, Map<UUID, msExchange.auctionManagement.TimeSlot> timeSlots) throws InvalidTimeSlotException {
        validateTimeSlotNotNull(timeSlot);
        validateTimeSlotIDNotNull(timeSlot);
        validateTimeSlotIDNotInUse(timeSlot.getTimeSlotID(), timeSlots);
        validateTimeSlotStartBeforeEnd(timeSlot);
    }

    private void validateTimeSlotNotNull(TimeSlot timeSlot) throws InvalidTimeSlotException {
        logger.trace("Validating timeSlot not null");
        if (timeSlot == null) {
            throw new InvalidTimeSlotException("TimeSlot is null", null);
        }
        logger.trace("Validated timeSlot not null successfully");
    }

    private void validateTimeSlotIDNotNull(TimeSlot timeSlot) throws InvalidTimeSlotException {
        logger.trace("Validating timeSlotID not null");
        if (timeSlot.getTimeSlotID() == null) {
            throw new InvalidTimeSlotException("TimeSlotID is null", null);
        }
        logger.trace("Validated timeSlotID not null successfully");
    }

    private void validateTimeSlotIDNotInUse(UUID timeSlotID, Map<UUID, msExchange.auctionManagement.TimeSlot> timeSlots) throws InvalidTimeSlotException {
        logger.trace("Validating timeSlotID not in use");
        for (msExchange.auctionManagement.TimeSlot timeSlot : timeSlots.values()) {
            if (timeSlot.getTimeSlotId().equals(timeSlotID)) {
                throw new InvalidTimeSlotException("TimeSlotID is already in use", null);
            }
        }
        logger.trace("Validated timeSlotID not in use successfully");
    }


    private void validateTimeSlotStartBeforeEnd(TimeSlot timeSlot) throws InvalidTimeSlotException {
        logger.trace("Validating timeSlot start before end");
        if (timeSlot.getStartTime().isAfter(timeSlot.getEndTime())) {
            throw new InvalidTimeSlotException("TimeSlot start is after end", null);
        }
        logger.trace("Validated timeSlot start before end successfully");
    }

}
