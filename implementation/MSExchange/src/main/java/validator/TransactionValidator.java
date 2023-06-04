package validator;

import Exceptions.IllegalSendableException;
import sendable.ISendable;
import sendable.Transaction;

import java.util.Optional;

public class TransactionValidator implements IValidator {

    @Override
    public void validateSendable(ISendable sendable) {
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

    private void validateIDNotNULL(Transaction transaction) {
        if (transaction.getTransactionID() == null) {
            throw new IllegalSendableException("Transaction ID shouldn't be null");
        }
    }
}
