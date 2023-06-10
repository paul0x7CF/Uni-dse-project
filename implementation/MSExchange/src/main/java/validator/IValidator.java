package validator;

import MSP.Exceptions.IllegalSendableException;
import CF.sendable.EServiceType;
import CF.sendable.ISendable;

import java.util.Optional;
import java.util.UUID;

public interface IValidator {
    static void validateSendableNotNull(ISendable sendable) throws IllegalSendableException {
        if (sendable == null) {
            throw new IllegalSendableException("Sendable is null");
        }
    }

    static void validateAuctionID(Optional<UUID> auctionID, EServiceType serviceType) throws IllegalSendableException {
        if (serviceType == EServiceType.ExchangeWorker) {
            validateAuctionIDExists(auctionID);
        }
    }

    static void validateSenderIDNotNull(UUID senderID) throws IllegalSendableException {
        if (senderID == null) {
            throw new IllegalSendableException("SenderID is null");
        }
    }

    static void validateReceiverIDNotNull(UUID receiverID) throws IllegalSendableException {
        if (receiverID == null) {
            throw new IllegalSendableException("ReceiverID is null");
        }
    }

    static void validatePriceNotNegative(double price) throws IllegalSendableException {
        if (price <= 0) {
            throw new IllegalSendableException("Price is smaller than zero");
        }
    }

    static void validateVolumeNotNegative(double volume) throws IllegalSendableException {
        if (volume <= 0) {
            throw new IllegalSendableException("Volume is smaller than zero");
        }
    }

    static void validateAuctionIDExists(Optional<UUID> auctionID) throws IllegalSendableException {
        if (auctionID.isEmpty()) {
            throw new IllegalSendableException("AuctionID is null");
        }
    }

    static void validateTimeSlotNotNull(Optional<UUID> timeSlot) throws IllegalSendableException {
        if (timeSlot.isEmpty()) {
            throw new IllegalSendableException("TimeSlot is null");
        }
    }

    static void validateServiceTypeNotNull(EServiceType serviceType) throws IllegalSendableException {
        if (serviceType == null) {
            throw new IllegalSendableException("ServiceType is null");
        }
    }

    public void validateSendable(ISendable sendable) throws IllegalSendableException;

}
