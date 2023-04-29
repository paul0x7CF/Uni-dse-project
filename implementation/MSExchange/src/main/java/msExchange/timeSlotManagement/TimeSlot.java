package msExchange.timeSlotManagement;

import loadManager.auctionManagement.Auction;
import msExchange.timeSlotManagement.auctionManagement.AuctionManager;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TimeSlot implements Runnable {
    private UUID timeSlotId;
    private Date startTime;
    private Date endTime;
    private Date creationTime;
    private AuctionManager auctionManager;
    private boolean isOpen;

    @Override
    public void run() {

    }

    public Date isEmptySince() {
        return null;
    }

    private Date getEmptyDuration() {
        return null;
    }

    public UUID getTimeSlotId() {
        return timeSlotId;
    }

    public Map<UUID, Auction> getAuctions() {
        return null;
    }

    public boolean isOpen() {
        return isOpen;
    }

}
