package msExchange.timeSlotManagement;

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
    private boolean isOpen = false;

    @Override
    public void run() {
        while (true) {
            if (!isOpen) {
                setIsOpen();
            }


        }
    }

    private void setIsOpen() {
        if (startTime.after(new Date())) {
            isOpen = true;
        }
    }

    public synchronized long isEmptySinceInMillisecs() {
        Date lastTimeUsed = auctionManager.getLastAuctionTime();
        Date currentTime = new Date();

        return currentTime.getTime() - lastTimeUsed.getTime();
    }

    public UUID getTimeSlotId() {
        return timeSlotId;
    }

    public Map<UUID, Double> getAuctions() {
        return auctionManager.getAuctions();
    }

    public boolean isOpen() {
        return isOpen;
    }

}
