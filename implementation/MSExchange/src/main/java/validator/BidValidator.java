package validator;

import Exceptions.IllegalSendableException;
import sendable.Bid;
import sendable.ISendable;

import java.util.Optional;

public final class BidValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) throws IllegalSendableException {
        IValidator.validateSendableNotNull(sendable);

        if (sendable instanceof Bid) {
            Bid bid = (Bid) sendable;
            IValidator.validateReceiverIDNotNull(bid.getBidderID());
            IValidator.validatePriceNotNegative(bid.getPrice());
            IValidator.validateVolumeNotNegative(bid.getVolume());
            IValidator.validateTimeSlotNotNull(Optional.ofNullable(bid.getTimeSlot()));
        } else {
            throw new IllegalSendableException("The ISendable object was not an instance of Bid");
        }
    }


}
