package MSP.Communication;

import MSP.Data.EConsumerType;

import java.util.HashMap;

public class PollForecast {
    private boolean isAvailable;
    private HashMap<EConsumerType, Double> responseMap;

    public PollForecast() {
        this.isAvailable = false;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setPollResult(HashMap<EConsumerType, Double> responseMap) {
        this.responseMap = responseMap;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public HashMap<EConsumerType, Double> getForecastResult() {
       return this.responseMap;
    }
}
 