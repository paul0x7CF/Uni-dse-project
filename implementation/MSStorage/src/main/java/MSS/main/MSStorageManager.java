package MSS.main;

import CF.sendable.Transaction;
import MSS.communication.Communication;
import MSS.exceptions.StorageEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class MSStorageManager implements Runnable {


    private static final Logger logger = LogManager.getLogger(MSStorageManager.class);

    private final LinkedHashMap<Integer, StorageCell> storageCells = new LinkedHashMap<>();
    private BlockingQueue<Transaction> incomingTransactionQueue;
    private BlockingQueue<Double> incomingEnergyRequestQueue;
    private Wallet wallet;
    private Communication communication;
    private CallbackStorageCellTerminated callbackTermination;

    public MSStorageManager() {
        this.callbackTermination = actOnCallback();

    }

    private void createStorageCell(Duration period, double maxVolume) {
        int nextStorageCellID = this.storageCells.size() + 1;
        StorageCell storageCellToStart = new StorageCell(period,maxVolume, this.callbackTermination, nextStorageCellID);
        this.storageCells.put(nextStorageCellID, storageCellToStart);
        new Thread(storageCellToStart,"Storage-Cell").start();
    }
    private void createStorageCell() {
        createStorageCell(Duration.ofSeconds(30), 10);
    }

    private CallbackStorageCellTerminated actOnCallback() {
        return storageCellID -> {
            logger.warn("callback from Storage Cell about termination with Id {}", storageCellID);
            this.storageCells.remove(storageCellID);
        };
    }

    private Optional<StorageCell> getStorageCell(){
        Map.Entry<Integer, StorageCell> lastEntry = null;
        for (var entry : this.storageCells.entrySet()) {
            lastEntry = entry;
        }
        if (lastEntry != null) {
            logger.trace("StorageCell with ID {} is the last one", lastEntry.getValue().getStorageCellID());
            return Optional.of(lastEntry.getValue());
        }

        return Optional.empty();
    }

    private void increaseStorageCellVolume(double volumeToAdd) {
        Optional<StorageCell> storageCell = getStorageCell();
        if(storageCell.isPresent()){
            if(!storageCell.get().increaseVolume(volumeToAdd)){
                logger.debug("Volume could not be increased by {} because Not Enough Space", volumeToAdd);
                createStorageCell();
                increaseStorageCellVolume(volumeToAdd);
            }
        }
        else {
            logger.warn("No StorageCell available; creating new one");
            createStorageCell();
            increaseStorageCellVolume(volumeToAdd);
        }
    }

    private void decrementStorageCellVolume(double volumeToDecrement) {
        Optional<StorageCell> storageCell = getStorageCell();
        if(storageCell.isPresent()) {
            double result = storageCell.get().decrementVolume(volumeToDecrement);
            if (result != 0) {
                // Durchlaufe die LinkedHashMap von hinten nach vorne mit einem Iterator
                ListIterator<Map.Entry<Integer, StorageCell>> iterator = new ArrayList<>(this.storageCells.entrySet()).listIterator(this.storageCells.size()-1);
                while (iterator.hasPrevious()) {
                    Map.Entry<Integer, StorageCell> entry = iterator.previous();
                    Integer key = entry.getKey();
                    StorageCell value = entry.getValue();
                    result = value.decrementVolume(result);
                    if (result == 0) {
                        break;
                    }
                }
                if (result != 0) {
                    double decrementedVolume = volumeToDecrement - result;
                    try {
                        throw new StorageEmptyException("Not enough volume in StorageCells to decrement " + volumeToDecrement + " volume " + decrementedVolume + " where decremented " + result + " remaining");
                    } catch (StorageEmptyException e) {
                        logger.warn(e.getMessage());
                        createStorageCell();
                        decrementStorageCellVolume(result);
                    }

                }

            }
        }

    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            createStorageCell();
        }
        decrementStorageCellVolume(213);

        /*while (true) {
            try {
                Thread.sleep(2000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println("---------------END-----------------");

    }

    public void handleFinishedTransaction(Transaction finishedTransaction) {

    }

    private Transaction saveFinishedTransaction(Transaction transactionToAdd) {
        return null;
    }
}
