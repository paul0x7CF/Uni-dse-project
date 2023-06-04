package Validator;

import Exceptions.InvalidSellException;
import sendable.EServiceType;
import sendable.Sell;

import java.util.Optional;
import java.util.UUID;

public final class SellValidator {
    public static void validateSell(Sell sell, EServiceType serviceType) throws InvalidSellException {
        validateSellNotNull(sell);
        validateSellerIDNotNull(sell.getSellerID());
        validatePriceNotNegative(sell.getAskPrice(), sell.getSellerID());
        validateVolumeNotNegative(sell.getVolume(), sell.getSellerID());
        if (serviceType == EServiceType.ExchangeWorker) {
            validateAuctionIDNotNull(sell.getAuctionID(), sell.getSellerID());
        }
        validateTimeSlotNotNull(sell.getTimeSlot(), sell.getSellerID());
    }

    private static void validateSellNotNull(Sell sell) throws InvalidSellException {
        if (sell == null) {
            throw new InvalidSellException("Bid is null", null);
        }
    }

    private static void validateSellerIDNotNull(UUID sellerID) throws InvalidSellException {
        if (sellerID == null) {
            throw new InvalidSellException("sellerID is null", null);
        }
    }

    private static void validatePriceNotNegative(double price, UUID sellerID) throws InvalidSellException {
        if (price <= 0) {
            throw new InvalidSellException("Price is smaller than zero", sellerID);
        }
    }

    private static void validateVolumeNotNegative(double volume, UUID sellerID) throws InvalidSellException {
        if (volume <= 0) {
            throw new InvalidSellException("Volume is smaller than zero", sellerID);
        }
    }

    private static void validateAuctionIDNotNull(Optional<UUID> auctionID, UUID sellerID) throws InvalidSellException {
        if (auctionID.isPresent()) {
            throw new InvalidSellException("AuctionID should not exist.", sellerID);
        }
    }

    private static void validateTimeSlotNotNull(UUID timeSlot, UUID sellerID) throws InvalidSellException {
        if (timeSlot == null) {
            throw new InvalidSellException("TimeSlot is null", sellerID);
        }
    }
}
