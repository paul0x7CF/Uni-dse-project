package msExchange.messageHandling;

import msExchange.Exceptions.InvalidBidException;
import sendable.Bid;

import java.util.Optional;
import java.util.UUID;

public final class BidValidator {

    public static void validateBid(Bid bid) throws InvalidBidException {
        validateBidNotNull(bid);
        validateBidderIDNotNull(bid.getBidderID());
        validatePriceNotNegative(bid.getPrice(), bid.getBidderID());
        validateVolumeNotNegative(bid.getVolume(), bid.getBidderID());
        validateAuctionIDNotNull(bid.getAuctionID(), bid.getBidderID());
        validateTimeSlotNotNull(bid.getTimeSlot(), bid.getBidderID());
    }

    private static void validateBidNotNull(Bid bid) throws InvalidBidException {
        if (bid == null) {
            throw new InvalidBidException("Bid is null", null);
        }
    }

    private static void validateBidderIDNotNull(UUID bidderID) throws InvalidBidException {
        if (bidderID == null) {
            throw new InvalidBidException("BidderID is null", null);
        }
    }

    private static void validatePriceNotNegative(double price, UUID bidderID) throws InvalidBidException {
        if (price <= 0) {
            throw new InvalidBidException("Price is smaller than zero", bidderID);
        }
    }

    private static void validateVolumeNotNegative(double volume, UUID bidderID) throws InvalidBidException {
        if (volume <= 0) {
            throw new InvalidBidException("Volume is smaller than zero", bidderID);
        }
    }

    private static void validateAuctionIDNotNull(Optional<UUID> auctionID, UUID bidderID) throws InvalidBidException {
        if (auctionID.isEmpty()) {
            throw new InvalidBidException("AuctionID is null", bidderID);
        }
    }

    private static void validateTimeSlotNotNull(UUID timeSlot, UUID bidderID) throws InvalidBidException {
        if (timeSlot == null) {
            throw new InvalidBidException("TimeSlot is null", bidderID);
        }
    }

}
