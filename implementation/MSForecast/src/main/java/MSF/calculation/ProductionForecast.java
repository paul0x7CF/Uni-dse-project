package MSF.calculation;

import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.EForecastType;
import MSF.data.ProsumerRequest;
import CF.sendable.SolarRequest;

import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private BlockingQueue<ProsumerRequest> inputQueue;
    //private BlockingQueue<ProsumerResponse> outputQueue;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private SolarRequest solarRequest;
    private EForecastType forecastType;
    private TimeSlot currentTimeSlot;

    public ProductionForecast(BlockingQueue<ProsumerRequest> inputQueue, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot, EForecastType forecastType) {
        this.inputQueue = inputQueue;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
        this.forecastType = forecastType;
    }
    @Override
    public void run() {
        System.out.println("ProductionForecast");

        while (true) {
            try {
                ProsumerRequest prosumerRequest = inputQueue.take();
                predictProduction(prosumerRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void predictProduction(ProsumerRequest prosumerRequest) {

    }

    private SolarRequest getSolarRequest() {
        return solarRequest;
    }

    private void addToOutputQueueProduction(SolarRequest solarRequest) {

    }
}
