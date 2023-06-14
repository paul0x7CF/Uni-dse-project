package MSP.Communication;

public class PollProductionForecast implements PollForecast {

    private boolean isAvailable;
    private double response;

    public PollProductionForecast() {
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
    public void setPollResult(Object response) {
        this.response = (double) response;
    }

    @Override
    public Object getForecastResult() {
        return this.response;
    }
}
