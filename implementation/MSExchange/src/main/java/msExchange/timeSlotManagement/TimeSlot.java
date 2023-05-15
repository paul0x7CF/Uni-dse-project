package msExchange.timeSlotManagement;

import msExchange.timeSlotManagement.auctionManagement.Auction;
import msExchange.timeSlotManagement.auctionManagement.AuctionManager;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TimeSlot implements Runnable {
    private UUID timeSlotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime creationTime;
    private AuctionManager auctionManager;
    private boolean isOpen = false;

    public TimeSlot(UUID timeSlotID, LocalDateTime startTime, LocalDateTime endTime) {
        this.timeSlotId = timeSlotID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void run() {
        while (true) {
            if (!isOpen) {
                setIsOpen();
            }


        }
    }

    private void setIsOpen() {
       /* if (startTime.after(new Date())) {
            isOpen = true;
        }*/
    }

    public synchronized long isEmptySinceInMillisecs() {
        Date lastTimeUsed = auctionManager.getLastAuctionTime();
        Date currentTime = new Date();

        return currentTime.getTime() - lastTimeUsed.getTime();
    }

    public UUID getTimeSlotId() {
        return timeSlotId;
    }

    public Map<UUID, Auction> getAuctions() {
        return auctionManager.getAuctions();
    }

    public boolean isOpen() {
        return isOpen;
    }

}
