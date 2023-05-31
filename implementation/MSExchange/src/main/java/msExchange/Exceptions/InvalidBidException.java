package msExchange.Exceptions;

import java.util.UUID;

public class InvalidBidException extends Exception {
    private UUID bidderID;

    public InvalidBidException(String message, UUID bidderID) {
        super(message);
        this.bidderID = bidderID;
    }

    public UUID getBidderID() {
        return bidderID;
    }
}
