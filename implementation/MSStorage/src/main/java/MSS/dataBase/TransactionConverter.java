package MSS.dataBase;

import CF.sendable.Transaction;

public class TransactionConverter {
    public static Transaction convertToTransaction(DbTransaction dbTransaction) {
        return new Transaction(
                dbTransaction.getSellerId(),
                dbTransaction.getBuyerId(),
                dbTransaction.getAmount(),
                dbTransaction.getPrice(),
                dbTransaction.getAuctionID()
        );
    }

    public static DbTransaction convertToDbTransaction(Transaction transaction) {
        return new DbTransaction(
                transaction.getSellerID(),
                transaction.getBuyerID(),
                transaction.getAmount(),
                transaction.getPrice(),
                transaction.getAuctionID()
        );
    }
}
