package main;

import sendable.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class MSStorageManager {
    private UUID msStorageID;
    private List<StorageCell> storageCells;
    private BlockingQueue<Transaction> incomingTransactionQueue;
    private BlockingQueue<Double> incomingEnergyRequestQueue;
    private Wallet wallet;
    //private Communication communication;

    public MSStorageManager() {

    }

    public void createStorageCells() {

    }

    public void handleFinishedTransaction(Transaction finishedTransaction) {

    }

    private Transaction saveFinishedTransaction(Transaction transactionToAdd) {
        return null;
    }
}
