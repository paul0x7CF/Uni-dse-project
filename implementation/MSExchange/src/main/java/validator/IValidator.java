package validator;

import CF.sendable.EServiceType;
import CF.sendable.ISendable;
import MSP.Exceptions.IllegalSendableException;
import MSP.Exceptions.InvalidBidException;
import MSP.Exceptions.InvalidSellException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

public interface IValidator {
    static final Logger logger = LogManager.getLogger(IValidator.class);

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
            logger.warn("Price is not larger than 0: " + price);
            throw new IllegalSendableException("Price is <= 0");
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

    public void validateSendable(ISendable sendable) throws IllegalSendableException, InvalidBidException, InvalidSellException;

}
