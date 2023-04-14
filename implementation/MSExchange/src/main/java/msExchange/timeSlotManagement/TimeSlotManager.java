package msExchange.timeSlotManagement;

import msExchange.timeSlotManagement.auctionManagement.AuctionThread;
import sendable.Sell;
import sendable.Timeslot;
import sendable.Transaction;

import java.util.*;

public class TimeSlotManager implements Runnable{
    private final Map<UUID, TimeSlotThread> timeSlots = new HashMap<>();

    public Optional<Transaction> updateTimeSlots(List<Timeslot> timeSlots) {return null;
    }
    public void addAuctionToSlot(UUID timeSlotId, Sell sellPosition) {
    }
    public Map<UUID, Double> getAuctionsForTimeSlot(UUID slotId) {
        return null;
    }

    private void addTimeSlot(TimeSlotThread timeSlot) {
    }
    private Transaction closeTimeSlot(TimeSlotThread timeSlot) {return null;
    }

    public void run() {
    }

}
