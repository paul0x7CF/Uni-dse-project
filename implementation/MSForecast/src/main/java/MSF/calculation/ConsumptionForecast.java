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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConsumptionForecast.class);
    private final BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest;
    private final ForecastCommunicationHandler forecastCommunicationHandler;
    private Map<UUID, TimeSlot> currentTimeSlots;

    public ConsumptionForecast(BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest, ForecastCommunicationHandler forecastCommunicationHandler, Map<UUID, TimeSlot> currentTimeSlot) {
        this.incomingConsumptionRequest = incomingConsumptionRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlots = currentTimeSlot;
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

        UUID timeSlotID = UUID.randomUUID();

        boolean timeSlotFound = false;

        for (var entry : currentTimeSlots.entrySet()) {
            timeSlotID = entry.getKey();

            if (timeSlotID.equals(prosumerConsumptionRequest.getCurrentTimeSlotID())) {
                timeSlotFound = true;
                break;
            }
        }

        if (!timeSlotFound) {
            logger.warn("Received consumption request for timeslot " + prosumerConsumptionRequest.getCurrentTimeSlotID());
        }

        logger.trace("Forecasting consumption for prosumer " + prosumerConsumptionRequest.getSenderID() + " for timeslot " + prosumerConsumptionRequest.getCurrentTimeSlotID());

        Duration duration = Duration.between(currentTimeSlots.get(timeSlotID).getStartTime(), currentTimeSlots.get(timeSlotID).getEndTime());

        for (String key : prosumerConsumptionRequest.getConsumptionMap().keySet()) {
            double consumptionValue = prosumerConsumptionRequest.getConsumptionMap().get(key);

            consumption.put(key, consumptionValue / (3600 / duration.getSeconds()));
        }

        logger.trace("Forecasted consumption: " + consumption);

        ConsumptionResponse consumptionResponse = new ConsumptionResponse(consumption, prosumerConsumptionRequest.getCurrentTimeSlotID());
        this.forecastCommunicationHandler.sendConsumptionResponseMessage(consumptionResponse, prosumerConsumptionRequest.getSenderAddress(), prosumerConsumptionRequest.getSenderPort(), prosumerConsumptionRequest.getSenderID());
    }

    public void setCurrentTimeSlot(TimeSlot currentTimeSlot) {
        this.currentTimeSlots.put(currentTimeSlot.getTimeSlotID(), currentTimeSlot);
    }
}
