package msExchange.timeSlotManagement;

import msExchange.timeSlotManagement.auctionManagement.AuctionThread;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.util.*;

public class TimeSlotManager {
    private final Map<UUID, TimeSlotThread> timeSlots = new HashMap<>();
    public Optional<Transaction> updateTimeSlots(List<TimeSlot> timeSlots) {return null;
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
}
