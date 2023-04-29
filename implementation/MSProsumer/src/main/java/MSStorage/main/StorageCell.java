package MSStorage.main;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class StorageCell implements Runnable{
    private double currentVolume;
    private double maxVolume;
    private Date lastUsed;
    private final Date MAX_UNUSED_LIFETIME = new Date(1000 * 60); // 1 Minute
    private BlockingQueue<Double> incomingEnergyRequests;

    public StorageCell(BlockingQueue<Double> incomingEnergyRequests) {
        this.incomingEnergyRequests = incomingEnergyRequests;
    }

    @Override
    public void run() {

    }

    private void checkStopTimer(Date currentTime) {

    }

    private void setLastUsed(Date currentTime) {

    }

    private void stopStorageCell() {

    }
}
