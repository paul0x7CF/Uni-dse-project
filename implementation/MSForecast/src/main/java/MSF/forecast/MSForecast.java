package MSF.forecast;

import CF.exceptions.MessageProcessingException;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.communication.messageHandler.ProsumerRequest;
import MSF.mainPackage.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSForecast implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSForecast.class);
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private EForecastType forecastType;
    private UUID forecastId;
    private BlockingQueue<ProsumerRequest> inputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outputQueue = new LinkedBlockingQueue<>();

    public MSForecast(int port) {
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(inputQueue, outputQueue, port, EServiceType.Forecast);
    }

    public EForecastType getForecastType() {
        return forecastType;
    }

    @Override
    public void run() {
        forecastCommunicationHandler.startBrokerRunner();
        forecastCommunicationHandler.addMessageHandler(ECategory.Exchange);
        forecastCommunicationHandler.addMessageHandler(ECategory.Forecast);

        logger.info("MSForecast started");

        while (true)
        {
            handleRequests();
        }
    }

    private void updateTimeSlots() {

    }

    private void handleRequests() {
        //TODO: Proper message handling!!

        logger.info("MSForecast received message: {}", inputQueue.poll());

        /*Message message = (Message) inputQueue.poll();

        if (message != null) {
            if (message.getReceiverID().equals(forecastCommunicationHandler.getBroker().getCurrentService().getId())) {
                logger.info("MSForecast received message: {}", message);
            }
        }*/
    }
}
