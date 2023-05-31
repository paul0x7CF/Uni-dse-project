package Exceptions;

import sendable.Bid;
import sendable.Sell;

import java.util.Optional;
import java.util.UUID;

public class InvalidTimeSlotException extends Exception {
    UUID timeSlotID;
    Sell sell;
    Bid bid;

    public InvalidTimeSlotException(String message, Optional<UUID> timeSlotID, Optional<Sell> sell, Optional<Bid> bid) {
        super(message);
        this.bid = bid.get();
        this.sell = sell.get();
        this.timeSlotID = timeSlotID.get();
    }

    public Optional<UUID> getTimeSlotID() {
        return Optional.of(timeSlotID);
    }

    public Optional<Sell> getSell() {
        return Optional.of(sell);
    }

    public Optional<Bid> getBid() {
        return Optional.of(bid);
    }
}
