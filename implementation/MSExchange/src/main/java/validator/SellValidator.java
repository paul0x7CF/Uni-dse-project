package validator;

import Exceptions.IllegalSendableException;
import sendable.ISendable;
import sendable.Sell;

public final class SellValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) {
        if (sendable instanceof Sell) {
            Sell sell = (Sell) sendable;
            IValidator.validateSendableNotNull(sell);
            IValidator.validateSenderIDNotNull(sell.getSellerID());
            IValidator.validatePriceNotNegative(sell.getAskPrice());
            IValidator.validateVolumeNotNegative(sell.getVolume());
            IValidator.validateTimeSlotNotNull(sell.getTimeSlot());
        } else {
            throw new IllegalSendableException("Sendable was not an instance of Sell.");
        }
    }
    
}
