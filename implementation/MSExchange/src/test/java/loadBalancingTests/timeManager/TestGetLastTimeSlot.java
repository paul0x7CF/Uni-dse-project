package loadBalancingTests.timeManager;

import CF.sendable.TimeSlot;
import MSP.Exceptions.InvalidTimeSlotException;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import mainPackage.PropertyFileReader;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.UUID;

public class TestGetLastTimeSlot {
    private int DURATION_IN_SECS;

    @Test
    public void givenTimeSlots_getLastTimeSlot_expectedCorrectUUID() throws InterruptedException {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        DURATION_IN_SECS = Integer.parseInt(propertyFileReader.getDuration());
        //arrange
        TimeSlotBuilder timeSlotBuilder = new TimeSlotBuilder();
        TimeSlot[] newTimeSlots = new TimeSlot[5];

        for (int i = 0; i < 5; i++) {
            try {
                newTimeSlots[i] = timeSlotBuilder.addNewTimeSlot();
            } catch (InvalidTimeSlotException e) {
                throw new RuntimeException(e);
            }

            Thread.sleep(DURATION_IN_SECS * 1000);

            Optional<UUID> timeSlotID = timeSlotBuilder.getLastTimeSlot();
            Assertions.assertEquals(newTimeSlots[i].getTimeSlotID(), timeSlotID.get());
        }
    }
}
