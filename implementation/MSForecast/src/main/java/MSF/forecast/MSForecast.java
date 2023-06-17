package MSF.forecast;

import CF.protocol.ECategory;
import CF.sendable.TimeSlot;
import MSF.calculation.ConsumptionForecast;
import MSF.calculation.ProductionForecast;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.EForecastType;
import MSF.data.ProsumerConsumptionRequest;
import MSF.data.ProsumerSolarRequest;
import MSF.exceptions.UnknownForecastTypeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSForecast implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSForecast.class);
    private final ForecastCommunicationHandler forecastCommunicationHandler;
    private final EForecastType forecastType;
    private UUID forecastId;
    private TimeSlot currentTimeSlot;
    private final BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest = new LinkedBlockingQueue<>();
    private final BlockingQueue<ProsumerSolarRequest> incomingSolarRequest = new LinkedBlockingQueue<>();
    private final BlockingQueue<TimeSlot> inputQueueTimeSlots = new LinkedBlockingQueue<>();

    public MSForecast(int port, EForecastType forecastType) {
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(incomingConsumptionRequest, incomingSolarRequest, inputQueueTimeSlots, port, EServiceType.Forecast);
        this.forecastType = forecastType;
    }

    public EForecastType getForecastType() {
        return forecastType;
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(this.forecastCommunicationHandler::startBrokerRunner, "ForecastCommunicationThread");
        communicationThread.start();

        this.forecastCommunicationHandler.addMessageHandler(ECategory.Exchange);
        this.forecastCommunicationHandler.addMessageHandler(ECategory.Forecast);

        logger.info("MSForecast started");
        logger.info("Waiting for TimeSlot");

        try {
            currentTimeSlot = this.inputQueueTimeSlots.take();
        } catch (InterruptedException e) {
            logger.error("Error while taking from inputQueueTimeSlot: {}", e.getMessage());
        }

        ConsumptionForecast consumptionForecast = new ConsumptionForecast(this.incomingConsumptionRequest, this.forecastCommunicationHandler, this.currentTimeSlot);
        new Thread(consumptionForecast, "ConsumptionForecast").start();

        ProductionForecast productionForecast = null;
        try {
            productionForecast = new ProductionForecast(this.incomingSolarRequest, this.forecastCommunicationHandler, this.currentTimeSlot, this.forecastType);
            new Thread(productionForecast, "ProductionForecast").start();
        } catch (UnknownForecastTypeException e) {
            throw new RuntimeException(e);
        }

        while (true)
        {
            try {
                currentTimeSlot = this.inputQueueTimeSlots.take();
                consumptionForecast.setCurrentTimeSlot(currentTimeSlot);
                productionForecast.setCurrentTimeSlot(currentTimeSlot);
            } catch (InterruptedException e) {
                logger.error("Error while taking from inputQueueTimeSlot: {}", e.getMessage());
            }
        }
    }
}
