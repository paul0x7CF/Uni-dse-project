package MSF.calculation;

import CF.protocol.Message;
import CF.sendable.ConsumptionResponse;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerRequest;
import MSF.data.ProsumerResponse;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private BlockingQueue<ProsumerRequest> inputQueue;
    //private BlockingQueue<ProsumerResponse> outputQueue;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private TimeSlot currentTimeSlot;

    public ConsumptionForecast(BlockingQueue<ProsumerRequest> inputQueue, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot) {
        this.inputQueue = inputQueue;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
    }
    @Override
    public void run() {
        System.out.println("ConsumptionForecast");

        while (true) {
            try {
                ProsumerRequest prosumerRequest = inputQueue.take();
                predictConsumption(prosumerRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void predictConsumption(ProsumerRequest prosumerRequest) {
        HashMap<String, Double> consumption = new HashMap<>();

        //TODO: calculate consumption

        //TEST

        //loop through consumptionMap in the prosumerRequest
        for (String key : prosumerRequest.getConsumptionMap().keySet()) {
            //get the consumption value for the current key
            double consumptionValue = prosumerRequest.getConsumptionMap().get(key);
            //add the consumption value to the consumption map
            //consumption.put(key, consumptionValue);

            //add random value to consumption
            consumption.put(key, Math.random() * 100);
        }

        //END TEST

        ConsumptionResponse consumptionResponse = new ConsumptionResponse(consumption, prosumerRequest.getCurrentTimeSlotID());
        this.forecastCommunicationHandler.sendConsumptionResponseMessage(consumptionResponse, prosumerRequest.getSenderAddress(), prosumerRequest.getSenderPort(), prosumerRequest.getSenderID());
    }
}
