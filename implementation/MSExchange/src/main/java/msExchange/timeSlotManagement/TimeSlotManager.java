package msExchange.timeSlotManagement;

import msExchange.timeSlotManagement.auctionManagement.AuctionThread;
import sendable.Sell;
import sendable.Timeslot;

import java.util.*;

public class TimeSlotManager implements Runnable{
    private final Map<UUID, TimeSlotThread> timeSlots = new HashMap<>();

    public void updateTimeSlots(List<Timeslot> timeSlots) {
    }
    public void addAuctionToSlot(UUID timeSlotId, Sell sellPosition) {
    }
    public List<AuctionThread> getAuctionsForTimeSlot(UUID slotId) {
        return null;
    }

    private void addTimeSlot(TimeSlotThread timeSlot) {
    }
    private void closeTimeSlot(TimeSlotThread timeSlot) {
    }

    public void run() {
    }

}
