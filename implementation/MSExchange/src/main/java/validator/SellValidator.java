package validator;

import CF.sendable.ISendable;
import CF.sendable.Sell;
import MSP.Exceptions.IllegalSendableException;
import MSP.Exceptions.InvalidSellException;

import java.util.Optional;

public final class SellValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) throws IllegalSendableException, InvalidSellException {
        if (sendable instanceof Sell) {
            Sell sell = (Sell) sendable;
            IValidator.validateSendableNotNull(sell);
            IValidator.validateSenderIDNotNull(sell.getSellerID());
            try {
                IValidator.validatePriceNotNegative(sell.getAskPrice());
                IValidator.validateVolumeNotNegative(sell.getVolume());
                IValidator.validateTimeSlotNotNull(Optional.ofNullable(sell.getTimeSlot()));
            } catch (IllegalSendableException e) {
                throw new InvalidSellException(e.getMessage(), sell);
            }
        } else {
            throw new IllegalSendableException("Sendable was not an instance of Sell.");
        }
    }

}
