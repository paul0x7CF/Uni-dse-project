package MSF.forecast;

import CF.exceptions.MessageProcessingException;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.communication.messageHandler.EProsumerRequestType;
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
    private TimeSlot currentTimeSlot;
    private BlockingQueue<ProsumerRequest> inputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outputQueue = new LinkedBlockingQueue<>();

    public MSForecast(int port) {
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(inputQueue, outputQueue, port, EServiceType.Forecast, this);
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
        logger.info("MSForecast received message: {}", inputQueue.poll());

        ProsumerRequest request = (ProsumerRequest) inputQueue.poll();

        if (request.getType() == EProsumerRequestType.CONSUMPTION) {
            //TODO: Calculate Consumption
        }
        else if (request.getType() == EProsumerRequestType.PRODUCTION) {
            //TODO: Calculate Production
        }
        else {
            logger.warn("Request type not supported");
        }
    }

    public void setCurrentTimeSlot(TimeSlot currentTimeSlot) {
        this.currentTimeSlot = currentTimeSlot;
    }
}
