package MSP.Communication.polling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PollProductionForecast implements PollForecast {

    private static final Logger logger = LogManager.getLogger(PollProductionForecast.class);

    private boolean isAvailable;
    private List<Double> response = new ArrayList<>();
    private boolean atLeastOneResponse;

    public PollProductionForecast() {
        this.isAvailable = false;
        this.atLeastOneResponse = false;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }
    @Override
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public void setPollResult(Object response) {
        this.response.add((Double) response);
        if (!this.atLeastOneResponse) {
            Thread timer = new Thread(this::setIsAvailableTrueAfterTime);
            timer.start();
            this.atLeastOneResponse = true;
        }

    }

    @Override
    public Object getForecastResult() {
        return this.response;
    }

    public int getResponseSize() {
        return this.response.size();
    }

    private void setIsAvailableTrueAfterTime() {
        try {
            Thread.sleep(5000);
            if (!isAvailable){
                logger.warn("Polling for ProductionForecast timed out and is now available because at least one response was received");
                this.isAvailable = true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
