package MSF.forecast;

import MSF.communication.ForecastCommunicationHandler;
import CF.protocol.Message;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ForecastController {
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private String ipAddress;
    private int port;
    private HashMap<MSForecast, BlockingQueue<Message>> forecasts = new HashMap<>();

    public ForecastController() {
    }

    public void processQueue() {

    }

    public void startForecast() {

    }

    private void updateTimeSlots() {

    }
}
