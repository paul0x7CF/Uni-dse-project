package MSF.calculation;

import CF.sendable.ConsumptionResponse;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerConsumptionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import MSF.exceptions.InvalidTimeSlotException;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConsumptionForecast.class);
    private final BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest;
    private final ForecastCommunicationHandler forecastCommunicationHandler;
    private TimeSlot currentTimeSlot;

    public ConsumptionForecast(BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot) {
        this.incomingConsumptionRequest = incomingConsumptionRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
    }
    @Override
    public void run() {
        logger.info("ConsumptionForecast started");

        while (true) {
            try {
                ProsumerConsumptionRequest prosumerConsumptionRequest = incomingConsumptionRequest.take();
                predictConsumption(prosumerConsumptionRequest);
            } catch (InterruptedException | InvalidTimeSlotException e) {
                e.printStackTrace();
            }
        }
    }

    private void predictConsumption(ProsumerConsumptionRequest prosumerConsumptionRequest) throws InvalidTimeSlotException {
        HashMap<String, Double> consumption = new HashMap<>();

        if (!prosumerConsumptionRequest.getCurrentTimeSlotID().equals(currentTimeSlot.getTimeSlotID())) {
            logger.warn("Received consumption request for timeslot " + prosumerConsumptionRequest.getCurrentTimeSlotID() + " but current timeslot is " + currentTimeSlot.getTimeSlotID());
        }

        logger.trace("Forecasting consumption for prosumer " + prosumerConsumptionRequest.getSenderID() + " for timeslot " + prosumerConsumptionRequest.getCurrentTimeSlotID());

        Duration duration = Duration.between(currentTimeSlot.getStartTime(), currentTimeSlot.getEndTime());

        for (String key : prosumerConsumptionRequest.getConsumptionMap().keySet()) {
            double consumptionValue = prosumerConsumptionRequest.getConsumptionMap().get(key);

            consumption.put(key, consumptionValue / (3600 / duration.getSeconds()));
        }

        logger.trace("Forecasted consumption: " + consumption);

        ConsumptionResponse consumptionResponse = new ConsumptionResponse(consumption, prosumerConsumptionRequest.getCurrentTimeSlotID());
        this.forecastCommunicationHandler.sendConsumptionResponseMessage(consumptionResponse, prosumerConsumptionRequest.getSenderAddress(), prosumerConsumptionRequest.getSenderPort(), prosumerConsumptionRequest.getSenderID());
    }
}
