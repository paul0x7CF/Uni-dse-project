package MSF.calculation;

import CF.sendable.ConsumptionResponse;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerConsumptionRequest;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest;
    //private BlockingQueue<ProsumerResponse> outputQueue;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private TimeSlot currentTimeSlot;

    public ConsumptionForecast(BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot) {
        this.incomingConsumptionRequest = incomingConsumptionRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
    }
    @Override
    public void run() {
        System.out.println("ConsumptionForecast");

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

        //TODO: calculate consumption

        //TEST

        //loop through consumptionMap in the prosumerRequest
        for (String key : prosumerConsumptionRequest.getConsumptionMap().keySet()) {
            //get the consumption value for the current key
            double consumptionValue = prosumerConsumptionRequest.getConsumptionMap().get(key);
            //add the consumption value to the consumption map
            //consumption.put(key, consumptionValue);

            //add random value to consumption
            consumption.put(key, Math.random() * 100);
        }

        //END TEST

        ConsumptionResponse consumptionResponse = new ConsumptionResponse(consumption, prosumerConsumptionRequest.getCurrentTimeSlotID());
        this.forecastCommunicationHandler.sendConsumptionResponseMessage(consumptionResponse, prosumerConsumptionRequest.getSenderAddress(), prosumerConsumptionRequest.getSenderPort(), prosumerConsumptionRequest.getSenderID());
    }
}
