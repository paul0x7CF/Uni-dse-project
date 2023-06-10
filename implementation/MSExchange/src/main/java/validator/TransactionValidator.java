package validator;

import MSP.Exceptions.IllegalSendableException;
import CF.sendable.ISendable;
import CF.sendable.Transaction;

import java.util.Optional;

public class TransactionValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) throws IllegalSendableException {
        IValidator.validateSendableNotNull(sendable);

        if (sendable instanceof Transaction) {
            Transaction transaction = (Transaction) sendable;
            validateIDNotNULL(transaction);
            IValidator.validatePriceNotNegative(transaction.getPrice());
            IValidator.validateSenderIDNotNull(transaction.getSellerID());
            IValidator.validateReceiverIDNotNull(transaction.getBuyerID());
            IValidator.validateVolumeNotNegative(transaction.getAmount());
            IValidator.validateAuctionIDExists(Optional.ofNullable(transaction.getAuctionID()));
            //TODO: check TimeSlot
        } else {
            throw new IllegalSendableException("The ISendable object was not an instance of Transaction");
        }
    }

    private void validateIDNotNULL(Transaction transaction) throws IllegalSendableException {
        if (transaction.getTransactionID() == null) {
            throw new IllegalSendableException("Transaction ID shouldn't be null");
        }
    }
}
