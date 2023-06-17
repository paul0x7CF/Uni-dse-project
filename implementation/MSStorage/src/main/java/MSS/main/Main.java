package MSS.main;

import MSS.dataBase.DbTransaction;

import MSS.configuration.ConfigFileReader;
import MSS.dataBase.DbTransaction;
import MSS.dataBase.TransactionDAO;
import MSS.storage.MSStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);


    private static final Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        // Create a new transaction
        TransactionDAO transactionDAO = new TransactionDAO();
        UUID buyerID = UUID.randomUUID();
        UUID sellerID = UUID.randomUUID();


        DbTransaction transaction = new DbTransaction(sellerID, buyerID, 100.0, 10.0, UUID.randomUUID());
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
        System.out.println("Deleted transaction");


        int count = transactionDAO.deleteAll();
        logger.warn("Deleting all transactions{}" + count);

       final int STORAGE_START_PORT = Integer.parseInt(ConfigFileReader.getCommunicationProperty("storagePort"));
        final double WALLET_START_MONEY = Double.parseDouble(ConfigFileReader.getProperty("walletStartMoney"));

        new Thread(new MSStorageManager(STORAGE_START_PORT, WALLET_START_MONEY),"Storage").start();
        System.out.println("----------------MAIN Thread Ended----------------");

    }
}
