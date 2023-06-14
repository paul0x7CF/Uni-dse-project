package MSP.Communication.polling;

import MSP.Data.EConsumerType;

import java.util.HashMap;

public class PollConsumptionForecast implements PollForecast{
    private boolean isAvailable;
    private HashMap<EConsumerType, Double> responseMap;

    public PollConsumptionForecast() {
        this.isAvailable = false;
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
    public void setPollResult(Object responseMap) {
        this.responseMap = (HashMap<EConsumerType, Double>) responseMap;
    }

    @Override
    public Object getForecastResult() {
       return this.responseMap;
    }
}
 