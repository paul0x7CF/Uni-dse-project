package validator;

import CF.sendable.Bid;
import CF.sendable.ISendable;
import MSP.Exceptions.IllegalSendableException;
import MSP.Exceptions.InvalidBidException;

import java.util.Optional;

public final class BidValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) throws InvalidBidException, IllegalSendableException {
        IValidator.validateSendableNotNull(sendable);

        if (sendable instanceof Bid) {
            Bid bid = (Bid) sendable;
            IValidator.validateReceiverIDNotNull(bid.getBidderID());
            try {
                IValidator.validatePriceNotNegative(bid.getPrice());
                IValidator.validateVolumeNotNegative(bid.getVolume());
                IValidator.validateTimeSlotNotNull(Optional.ofNullable(bid.getTimeSlot()));
            } catch (IllegalSendableException e) {
                throw new InvalidBidException(e.getMessage(), bid);
            }

        } else {
            throw new IllegalSendableException("VALIDATOR: The ISendable object was not an instance of Bid");
        }
    }


}
