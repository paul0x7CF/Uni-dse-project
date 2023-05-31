package msExchange.messageHandling;

import msExchange.Exceptions.InvalidBidException;
import sendable.Bid;

public class BidValidator {
    public static void checkBid(Bid bid) throws InvalidBidException {
        if (bid == null) {
            throw new InvalidBidException("Bid is null", null);
        }
        if (bid.getBidderID() == null) {
            throw new InvalidBidException("BidderID is null", null);
        }
        //TODO: check if BidderID same as serviceID
        if (bid.getPrice() < 0) {
            throw new InvalidBidException("Price is null", bid.getBidderID());
        }


    }

}
