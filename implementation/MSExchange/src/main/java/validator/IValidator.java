package validator;

import sendable.EServiceType;
import sendable.ISendable;

import java.util.Optional;
import java.util.UUID;

public interface IValidator {
    static void validateSendableNotNull(ISendable sendable) {
        if (sendable == null) {
            throw new IllegalStateException("Sendable is null");
        }
    }

    static void validateAuctionID(Optional<UUID> auctionID, EServiceType serviceType) {
        if (serviceType == EServiceType.ExchangeWorker) {
            validateAuctionIDExists(auctionID);
        }
    }

    static void validateSenderIDNotNull(UUID senderID) {
        if (senderID == null) {
            throw new IllegalStateException("SenderID is null");
        }
    }

    static void validateReceiverIDNotNull(UUID receiverID) {
        if (receiverID == null) {
            throw new IllegalStateException("ReceiverID is null");
        }
    }

    static void validatePriceNotNegative(double price) {
        if (price <= 0) {
            throw new IllegalStateException("Price is smaller than zero");
        }
    }

    static void validateVolumeNotNegative(double volume) {
        if (volume <= 0) {
            throw new IllegalStateException("Volume is smaller than zero");
        }
    }

    static void validateAuctionIDExists(Optional<UUID> auctionID) {
        if (auctionID.isEmpty()) {
            throw new IllegalStateException("AuctionID is null");
        }
    }

    static void validateTimeSlotNotNull(UUID timeSlot) {
        if (timeSlot == null) {
            throw new IllegalStateException("TimeSlot is null");
        }
    }

    static void validateServiceTypeNotNull(EServiceType serviceType) {
        if (serviceType == null) {
            throw new IllegalStateException("ServiceType is null");
        }
    }

    public void validateSendable(ISendable sendable);

}
