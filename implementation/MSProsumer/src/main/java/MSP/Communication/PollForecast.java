package MSP.Communication;

public class PollForecast {
    private boolean isAvailable;
    private double forecastResult;

    public PollForecast() {
        this.isAvailable = false;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setForecastResult(double forecastResult) {
        this.forecastResult = forecastResult;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public double getForecastResult() {
        return forecastResult;
    }
}
 