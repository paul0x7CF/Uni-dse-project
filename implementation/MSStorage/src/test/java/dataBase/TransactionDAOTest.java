package dataBase;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TransactionDAOTest {

    private TransactionDAO transactionDAO;
    private UUID buyerID;
    private UUID sellerID;
    private DbTransaction transaction;

    @Before
    public void setUp() {
        transactionDAO = new TransactionDAO();
        buyerID = UUID.randomUUID();
        sellerID = UUID.randomUUID();
        transaction = new DbTransaction(sellerID, buyerID, 100.0, 10.0);
    }

    @After
    public void tearDown() {
        transactionDAO = null;
        buyerID = null;
        sellerID = null;
        transaction = null;
    }

    @Test
    public void testCreateAndRead() {
        transactionDAO.create(transaction);
        DbTransaction retrievedTransaction = transactionDAO.read(transaction.getTransactionID());
        assertEquals(transaction.getTransactionID(), retrievedTransaction.getTransactionID());
        assertEquals(transaction.getAmount(), retrievedTransaction.getAmount(), 0.0);
        assertEquals(transaction.getPrice(), retrievedTransaction.getPrice(), 0.0);
    }

    @Test
    public void testDelete() {
        transactionDAO.create(transaction);
        transactionDAO.delete(transaction.getTransactionID());
        DbTransaction retrievedTransaction = transactionDAO.read(transaction.getTransactionID());
        assertNull(retrievedTransaction);
    }
}