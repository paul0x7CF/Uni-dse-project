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
import MSF.historicData.HistoricDataReader;
import MSF.propertyHandler.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSForecast implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSForecast.class);
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private EForecastType forecastType;
    private UUID forecastId;
    private TimeSlot currentTimeSlot;
    private BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest = new LinkedBlockingQueue<>();
    private BlockingQueue<ProsumerSolarRequest> incomingSolarRequest = new LinkedBlockingQueue<>();
    private BlockingQueue<TimeSlot> inputQueueTimeSlots = new LinkedBlockingQueue<>();

    public MSForecast(int port, EForecastType forecastType) {
        this.forecastCommunicationHandler = new ForecastCommunicationHandler(incomingConsumptionRequest, incomingSolarRequest, inputQueueTimeSlots, port, EServiceType.Forecast);
        this.forecastType = forecastType;
    }

    public EForecastType getForecastType() {
        return forecastType;
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(() -> {
            this.forecastCommunicationHandler.startBrokerRunner();
        }, "ForecastCommunicationThread");
        communicationThread.start();

        this.forecastCommunicationHandler.addMessageHandler(ECategory.Exchange);
        this.forecastCommunicationHandler.addMessageHandler(ECategory.Forecast);

        logger.info("MSForecast started");

        logger.info("Waiting for TimeSlot");

        try {
            TimeSlot currentTimeSlot = this.inputQueueTimeSlots.take();
            updateTimeSlots(currentTimeSlot);
        } catch (InterruptedException e) {
            logger.error("Error while taking from inputQueueTimeSlot: {}", e.getMessage());
        }

        for (int i = 0; i < 5; i++) {
            new Thread(new ConsumptionForecast(this.incomingConsumptionRequest, this.forecastCommunicationHandler, this.currentTimeSlot), "ConsumptionForecast-" + i).start();
        }

        for (int i = 0; i < 5; i++) {
            try {
                new Thread(new ProductionForecast(this.incomingSolarRequest, this.forecastCommunicationHandler, this.currentTimeSlot, this.forecastType), "ProductionForecast-" + i).start();
            } catch (UnknownForecastTypeException e) {
                throw new RuntimeException(e);
            }
        }

        while (true)
        {
            try {
                TimeSlot currentTimeSlot = this.inputQueueTimeSlots.take();
                updateTimeSlots(currentTimeSlot);
            } catch (InterruptedException e) {
                logger.error("Error while taking from inputQueueTimeSlot: {}", e.getMessage());
            }
        }
    }

    private void updateTimeSlots(TimeSlot currentTimeSlot) {
        this.currentTimeSlot = currentTimeSlot;
    }
}
