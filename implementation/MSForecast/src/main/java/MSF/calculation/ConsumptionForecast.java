package MSF.calculation;

import CF.protocol.Message;
import CF.sendable.ConsumptionResponse;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerRequest;
import MSF.data.ProsumerResponse;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private BlockingQueue<ProsumerRequest> inputQueue;
    //private BlockingQueue<ProsumerResponse> outputQueue;
    private ForecastCommunicationHandler forecastCommunicationHandler;

    public ConsumptionForecast(BlockingQueue<ProsumerRequest> inputQueue, ForecastCommunicationHandler forecastCommunicationHandler) {
        this.inputQueue = inputQueue;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
    }

    public void run() {
        System.out.println("ConsumptionForecast");

        try {
            ProsumerRequest prosumerRequest = inputQueue.take();
            predictConsumption(prosumerRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void predictConsumption(ProsumerRequest prosumerRequest) {
        HashMap<String, Double> consumption = new HashMap<>();

        //TODO: calculate consumption

        ConsumptionResponse consumptionResponse = new ConsumptionResponse(consumption, prosumerRequest.getCurrentTimeSlotID());
        this.forecastCommunicationHandler.sendConsumptionResponseMessage(consumptionResponse, prosumerRequest.getSenderAddress(), prosumerRequest.getSenderPort(), prosumerRequest.getSenderID());
    }
}
