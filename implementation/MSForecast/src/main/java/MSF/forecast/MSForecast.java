package MSF.forecast;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.TimeSlot;
import MSF.calculation.ConsumptionForecast;
import MSF.calculation.ProductionForecast;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerRequest;
import MSF.data.ProsumerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;

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
    //private BlockingQueue<ProsumerResponse> outputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<TimeSlot> inputQueueTimeSlots = new LinkedBlockingQueue<>();

    public MSForecast(int port) {
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(inputQueue, inputQueueTimeSlots, port, EServiceType.Forecast, this);
    }

    public EForecastType getForecastType() {
        return forecastType;
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(() -> {
            forecastCommunicationHandler.startBrokerRunner();
        }, "ForecastCommunicationThread");
        communicationThread.start();

        //forecastCommunicationHandler.startBrokerRunner();

        forecastCommunicationHandler.addMessageHandler(ECategory.Exchange);
        forecastCommunicationHandler.addMessageHandler(ECategory.Forecast);

        logger.info("MSForecast started");

        for (int i = 0; i < 5; i++) {
            new Thread(new ConsumptionForecast(inputQueue, this.forecastCommunicationHandler), "ConsumptionForecast-" + i).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(new ProductionForecast(inputQueue, this.forecastCommunicationHandler), "ProductionForecast-" + i).start();
        }

        while (true)
        {
            try {
                TimeSlot currentTimeSlot = inputQueueTimeSlots.take();
                updateTimeSlots(currentTimeSlot);
            } catch (InterruptedException e) {
                logger.error("Error while taking from inputQueueTimeSlot: {}", e.getMessage());
            }
        }
    }

    private void updateTimeSlots(TimeSlot currentTimeSlot) {
        this.currentTimeSlot = currentTimeSlot;
    }

    /*private void handleRequests() {
        logger.info("MSForecast received message: {}", inputQueue.poll());

        ProsumerRequest request = (ProsumerRequest) inputQueue.poll();

        if (request.getType() == EProsumerRequestType.CONSUMPTION) {
            //TODO: Calculate Consumption (Also check the UUID for the TimeSlot)
        }
        else if (request.getType() == EProsumerRequestType.PRODUCTION) {
            //TODO: Calculate Production (Also check the UUID for the TimeSlot)
        }
        else {
            logger.warn("Request type not supported");
        }
    }*/

    /*public void setCurrentTimeSlot(TimeSlot currentTimeSlot) {
        this.currentTimeSlot = currentTimeSlot;
    }*/
}
