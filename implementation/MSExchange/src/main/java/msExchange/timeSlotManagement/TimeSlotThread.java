package msExchange.timeSlotManagement;

import msExchange.timeSlotManagement.auctionManagement.AuctionManager;

import java.util.Date;
import java.util.UUID;

public class TimeSlotThread extends Thread {
    private UUID timeSlotId;
    private Date startTime;
    private Date endTime;
    private Date creationTime;
    private AuctionManager auctionManager;

    public Date isEmptySince() {
        return null;
    }

    private Date getEmptyDuration() {
        return null;
    }

    public UUID getTimeSlotId() {
        return timeSlotId;
    }
}
