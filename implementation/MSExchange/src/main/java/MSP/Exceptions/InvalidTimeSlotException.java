package MSP.Exceptions;

import java.util.Optional;
import java.util.UUID;

public class InvalidTimeSlotException extends Exception {
    UUID timeSlotID;

    public InvalidTimeSlotException(String message, Optional<UUID> timeSlotID) {
        super(message);
        if (timeSlotID.isPresent()) {
            this.timeSlotID = timeSlotID.get();
        }

    }

    public Optional<UUID> getTimeSlotID() {
        return Optional.of(timeSlotID);
    }
}
