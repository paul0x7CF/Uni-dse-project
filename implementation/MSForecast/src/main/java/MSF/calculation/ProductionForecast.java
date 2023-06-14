package MSF.calculation;

import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.EForecastType;
import MSF.data.ProsumerConsumptionRequest;
import CF.sendable.SolarRequest;
import MSF.data.ProsumerSolarRequest;

import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private BlockingQueue<ProsumerSolarRequest> incomingSolarRequest;
    //private BlockingQueue<ProsumerResponse> outputQueue;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private SolarRequest solarRequest;
    private EForecastType forecastType;
    private TimeSlot currentTimeSlot;

    public ProductionForecast(BlockingQueue<ProsumerSolarRequest> incomingSolarRequest, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot, EForecastType forecastType) {
        this.incomingSolarRequest = incomingSolarRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
        this.forecastType = forecastType;
    }
    @Override
    public void run() {
        System.out.println("ProductionForecast");

        while (true) {
            try {
                ProsumerSolarRequest prosumerSolarRequest = incomingSolarRequest.take();
                predictProduction(prosumerSolarRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void predictProduction(ProsumerSolarRequest prosumerSolarRequest) {

    }

    private SolarRequest getSolarRequest() {
        return solarRequest;
    }
}
