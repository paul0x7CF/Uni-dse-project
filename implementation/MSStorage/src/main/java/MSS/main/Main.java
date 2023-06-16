package MSS.main;

import MSS.dataBase.DbTransaction;
import MSS.dataBase.TransactionDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        // Create a new transaction
        /*TransactionDAO transactionDAO = new TransactionDAO();
        UUID buyerID = UUID.randomUUID();
        UUID sellerID = UUID.randomUUID();

        DbTransaction transaction = new DbTransaction(sellerID, buyerID, 100.0, 10.0);
        transactionDAO.create(transaction);
        log.info("Created transactionID: " + transaction.getTransactionID());
        log.info("Created transaction amount: " + transaction.getAmount());
        log.info("Created transaction price: " + transaction.getPrice());

        // Read the transaction
        DbTransaction retrievedTransaction = transactionDAO.read(transaction.getTransactionID());
        System.out.println("Retrieved transaction ID: " + retrievedTransaction.getTransactionID());
        System.out.println("Retrieved amount: " + retrievedTransaction.getAmount());
        System.out.println("Retrieved price: " + retrievedTransaction.getPrice());



        // Delete the transaction
        transactionDAO.delete(retrievedTransaction.getTransactionID());
        System.out.println("Deleted transaction");*/

        new Thread(new MSStorageManager(),"Storage").start();

    }
}
