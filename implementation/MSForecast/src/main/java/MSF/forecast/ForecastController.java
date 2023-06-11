/*
package MSF.forecast;

import CF.exceptions.MessageProcessingException;
import CF.protocol.ECategory;
import MSF.communication.ForecastCommunicationHandler;
import CF.protocol.Message;
import MSF.communication.ForecastCommunicationHandler;
import CF.protocol.Message;
import CF.sendable.EServiceType;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ForecastController {
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private BlockingQueue<Message> inputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outputQueue = new LinkedBlockingQueue<>();
    private int port;
    private int amount;
    private int portJump;
    private HashMap<MSForecast, BlockingQueue<Message>> forecasts = new HashMap<>();

    public ForecastController(int port, int amount, int portJump) {
        this.port = port;
        this.amount = amount;
        this.portJump = portJump;
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(inputQueue, outputQueue, port, EServiceType.Forecast);

        forecastCommunicationHandler.addMessageHandler(ECategory.Exchange);
        forecastCommunicationHandler.addMessageHandler(ECategory.Forecast);
    }

    public void processQueue() {
        Message message = (Message) inputQueue.poll();

        if (message != null) {
            if (message.getReceiverID().equals(forecastCommunicationHandler.getBroker().getCurrentService().getId())) {
                try {
                    forecastCommunicationHandler.handleMessage(message);
                } catch (MessageProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void startForecast() {
        for (int i = 0; i < amount; i++) {
            //new Thread(new MSForecast(inputQueue, outputQueue, port + (i * portJump)),"Forecast-"+i).start();

            //start MSForecast thread and add it to the hashmap
            MSForecast forecast = new MSForecast(inputQueue, outputQueue, port + (i * portJump));
            new Thread(forecast, "Forecast-" + i).start();
            forecasts.put(forecast, inputQueue);
        }

        while (true) {
            processQueue();
        }
    }
}
*/
