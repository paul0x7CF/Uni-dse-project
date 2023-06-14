package MSP.Communication.polling;

public interface PollForecast {

    public boolean isAvailable();

    public void setAvailable(boolean available);

    public void setPollResult(Object response);

    public Object getForecastResult();
}
