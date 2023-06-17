package MSS.storage;
;
import MSS.exceptions.StorageIsNotRunnigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StorageCell implements Runnable {

    private final Logger logger = LogManager.getLogger(StorageCell.class);

    private volatile boolean isRunning;
    private final int storageCellID;
    private double currentVolume;
    private final double maxVolumeInWh;
    private final CallbackStorageCellTerminated callbackTermination;
    private Instant lastUsed;
    private final Duration idleDuration;

    public StorageCell(Duration period, double maxVolumeInWh, double currentVolume, CallbackStorageCellTerminated callbackTermination, int storageCellID) {
        this.idleDuration = period;
        this.maxVolumeInWh = maxVolumeInWh;
        this.currentVolume = currentVolume;
        this.callbackTermination = callbackTermination;
        this.storageCellID = storageCellID;
        this.isRunning = true;
        logger.debug("New Storage Cell ID:{} Cell created with maxVolume: {} and currentVolume: {}", storageCellID, maxVolumeInWh, currentVolume);
    }

    public void setLastUsed() throws StorageIsNotRunnigException {
        if(!isRunning) throw new StorageIsNotRunnigException();
        this.lastUsed = Instant.now();
        String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(lastUsed);
        logger.trace("StorageCell lastUsed set to: " + formattedTime);
    }
    private void isTerminated() throws StorageIsNotRunnigException {
        if(!isRunning) throw new StorageIsNotRunnigException();
    }

    public int getStorageCellID() throws StorageIsNotRunnigException {
        isTerminated();
        return storageCellID;
    }

    private boolean isEnoughSpace(double volumeToAdd) throws StorageIsNotRunnigException {
        isTerminated();
        if(this.currentVolume + volumeToAdd <= this.maxVolumeInWh) return true;
        else return false;
    }

    public double increaseVolume(double volumeToAdd) throws StorageIsNotRunnigException {
        isTerminated();
        if(this.currentVolume == this.maxVolumeInWh) return volumeToAdd;
        if(isEnoughSpace(volumeToAdd)) {
            this.currentVolume += volumeToAdd;
            logger.debug("StorageCell with ID {} volume increased by {}; new Volume: {}", storageCellID,volumeToAdd, this.currentVolume);
            return 0;
        }
        else {
            double addedVolume = this.maxVolumeInWh - this.currentVolume;
            this.currentVolume = this.maxVolumeInWh;
            logger.debug("StorageCell with ID {} volume increased by {}; new Volume: {}", storageCellID,addedVolume, this.currentVolume);
            return volumeToAdd - addedVolume;
        }
    }

    public double decrementVolume(double volumeToDecrement) throws StorageIsNotRunnigException {
        isTerminated();
        if(this.currentVolume == 0) return volumeToDecrement;
        if(this.currentVolume - volumeToDecrement >= 0) {
            this.currentVolume -= volumeToDecrement;
            logger.debug("StorageCell with ID {} volume decremented by {}; new Volume: {}", storageCellID,volumeToDecrement, this.currentVolume);
            return 0;
        } else {
            double decrementedVolume = this.currentVolume;
            this.currentVolume = 0;
            logger.debug("StorageCell with ID {} volume decremented by {}; new Volume: {}", storageCellID,decrementedVolume, this.currentVolume);
            return volumeToDecrement - decrementedVolume;
        }
    }


    @Override
    public void run() {
        this.lastUsed = Instant.now();
        logger.info("StorageCell with ID {} started", storageCellID);
        while (isRunning) {
            if(this.currentVolume == 0) {
                Instant currentTime = Instant.now();
                Duration elapsedDuration = Duration.between(lastUsed, currentTime);

                if (elapsedDuration.compareTo(idleDuration) >= 0) {
                    isRunning = false;
                    logger.info("StorageCell stopped because of inactivity");
                    this.callbackTermination.callback(storageCellID);
                    break;
                }
            }
        }
    }

}
