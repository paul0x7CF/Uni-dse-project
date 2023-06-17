package MSS.storage;
;
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

    public StorageCell(Duration period, double maxVolumeInWh, CallbackStorageCellTerminated callbackTermination, int storageCellID) {
        this.idleDuration = period;
        this.maxVolumeInWh = maxVolumeInWh;
        this.currentVolume = 0;
        this.callbackTermination = callbackTermination;
        this.storageCellID = storageCellID;
        this.isRunning = true;
    }

    public void setLastUsed() {
        if(!isRunning) throw new IllegalStateException("StorageCell is not running");
        this.lastUsed = Instant.now();
        String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(lastUsed);
        logger.trace("StorageCell lastUsed set to: " + formattedTime);
    }
    private void isTerminated() {
        if(!isRunning) throw new IllegalStateException("StorageCell is not running");
    }

    public int getStorageCellID() {
        isTerminated();
        return storageCellID;
    }

    private boolean isEnoughSpace(double volumeToAdd) {
        isTerminated();
        return (this.currentVolume + volumeToAdd) <= this.maxVolumeInWh;
    }

    public double increaseVolume(double volumeToAdd) {
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

    public double decrementVolume(double volumeToDecrement) {
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
