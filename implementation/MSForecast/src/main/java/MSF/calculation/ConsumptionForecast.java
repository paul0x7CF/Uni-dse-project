package MSF.calculation;

import CF.sendable.ConsumptionResponse;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerConsumptionRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private TimeSlot currentTimeSlot;

    public ConsumptionForecast(BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot) {
        this.incomingConsumptionRequest = incomingConsumptionRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
    }
    @Override
    public void run() {
        while (true) {
            try {
                ProsumerConsumptionRequest prosumerConsumptionRequest = incomingConsumptionRequest.take();
                predictConsumption(prosumerConsumptionRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void predictConsumption(ProsumerConsumptionRequest prosumerConsumptionRequest) {
        HashMap<String, Double> consumption = new HashMap<>();

        //TODO: calculate consumption (also check currentTimeSlotID)

        Duration duration = Duration.between(currentTimeSlot.getStartTime(), currentTimeSlot.getEndTime());

        for (String key : prosumerConsumptionRequest.getConsumptionMap().keySet()) {
            double consumptionValue = prosumerConsumptionRequest.getConsumptionMap().get(key);

            consumption.put(key, consumptionValue / (3600 / duration.getSeconds()));
        }

        ConsumptionResponse consumptionResponse = new ConsumptionResponse(consumption, prosumerConsumptionRequest.getCurrentTimeSlotID());
        this.forecastCommunicationHandler.sendConsumptionResponseMessage(consumptionResponse, prosumerConsumptionRequest.getSenderAddress(), prosumerConsumptionRequest.getSenderPort(), prosumerConsumptionRequest.getSenderID());
    }
}
