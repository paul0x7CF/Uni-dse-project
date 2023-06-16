package MSP.Exceptions;

import CF.sendable.Bid;

public class InvalidBidException extends Exception {
    private Bid bid;

    public InvalidBidException(String message, Bid bid) {
        super(message);
        this.bid = bid;
    }

    public Bid getBid() {
        return this.bid;
    }

}
