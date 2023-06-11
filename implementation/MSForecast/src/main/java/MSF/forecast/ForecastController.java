package MSF.forecast;

import MSF.communication.ForecastCommunicationHandler;
import CF.protocol.Message;
import MSF.communication.ForecastCommunicationHandler;
import CF.protocol.Message;
import CF.sendable.EServiceType;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ForecastController {
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private String ipAddress;
    private int port;
    private int amount;
    private int portJump;
    private HashMap<MSForecast, BlockingQueue<Message>> forecasts = new HashMap<>();

    public ForecastController(int port, int amount, int portJump) {
        this.port = port;
        this.amount = amount;
        this.portJump = portJump;
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(inputQueue, outputQueue, port, EServiceType.Forecast);
    }

    public void processQueue() {

    }

    public void startForecast() {
        for (int i = 0; i < amount; i++) {
            final int PORT = port + (i * portJump);
            new Thread(new MSForecast(inputQueue, outputQueue),"Forecast-"+i).start();
        }
    }

    private void updateTimeSlots() {

    }
}
