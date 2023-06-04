package validator;

import Exceptions.IllegalSendableException;
import sendable.ISendable;
import sendable.Sell;

import java.util.Optional;

public final class SellValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) throws IllegalSendableException {
        if (sendable instanceof Sell) {
            Sell sell = (Sell) sendable;
            IValidator.validateSendableNotNull(sell);
            IValidator.validateSenderIDNotNull(sell.getSellerID());
            IValidator.validatePriceNotNegative(sell.getAskPrice());
            IValidator.validateVolumeNotNegative(sell.getVolume());
            IValidator.validateTimeSlotNotNull(Optional.ofNullable(sell.getTimeSlot()));
        } else {
            throw new IllegalSendableException("Sendable was not an instance of Sell.");
        }
    }
    
}
