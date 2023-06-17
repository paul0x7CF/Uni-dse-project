package validator;

import CF.sendable.TimeSlot;
import MSP.Exceptions.InvalidTimeSlotException;
import msExchange.messageHandling.ExchangeMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

public class TimeSlotValidator {
    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);

    public void validateTimeSlot(TimeSlot timeSlot, Map<UUID, msExchange.auctionManagement.TimeSlot> timeSlots) throws InvalidTimeSlotException {
        logger.debug("TIMESLOT_VALIDATOR: Validating timeSlot");
        validateTimeSlotNotNull(timeSlot);
        validateTimeSlotIDNotNull(timeSlot);
        validateTimeSlotIDNotInUse(timeSlot.getTimeSlotID(), timeSlots);
        validateTimeSlotStartBeforeEnd(timeSlot);
        logger.debug("TIMESLOT_VALIDATOR: Validated timeSlot successfully");
    }

    private void validateTimeSlotNotNull(TimeSlot timeSlot) throws InvalidTimeSlotException {
        logger.trace("TIMESLOT_VALIDATOR: Validating timeSlot not null");
        if (timeSlot == null) {
            throw new InvalidTimeSlotException("TimeSlot is null", null);
        }
        logger.trace("TIMESLOT_VALIDATOR: Validated timeSlot not null successfully");
    }

    private void validateTimeSlotIDNotNull(TimeSlot timeSlot) throws InvalidTimeSlotException {
        logger.trace("TIMESLOT_VALIDATOR: Validating timeSlotID not null");
        if (timeSlot.getTimeSlotID() == null) {
            throw new InvalidTimeSlotException("TIMESLOT_VALIDATOR: TimeSlotID is null", null);
        }
        logger.trace("TIMESLOT_VALIDATOR: Validated timeSlotID not null successfully");
    }

    private void validateTimeSlotIDNotInUse(UUID timeSlotID, Map<UUID, msExchange.auctionManagement.TimeSlot> timeSlots) throws InvalidTimeSlotException {
        logger.trace("TIMESLOT_VALIDATOR: Validating timeSlotID not in use");
        for (msExchange.auctionManagement.TimeSlot timeSlot : timeSlots.values()) {
            if (timeSlot.getTimeSlotId().equals(timeSlotID)) {
                throw new InvalidTimeSlotException("TIMESLOT_VALIDATOR: TimeSlotID is already in use", null);
            }
        }
        logger.trace("TIMESLOT_VALIDATOR: Validated timeSlotID not in use successfully");
    }


    private void validateTimeSlotStartBeforeEnd(TimeSlot timeSlot) throws InvalidTimeSlotException {
        logger.trace("TIMESLOT_VALIDATOR: Validating timeSlot start before end");
        if (timeSlot.getStartTime().isAfter(timeSlot.getEndTime())) {
            throw new InvalidTimeSlotException("TIMESLOT_VALIDATOR: TimeSlot start is after end", null);
        }
        logger.trace("TIMESLOT_VALIDATOR: Validated timeSlot start before end successfully");
    }

}
