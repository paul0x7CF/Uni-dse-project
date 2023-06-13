package MSF.calculation;

import MSF.communication.ForecastCommunicationHandler;
import MSF.data.ProsumerRequest;
import MSF.data.ProsumerResponse;
import MSF.historicData.HistoricData;
import CF.protocol.Message;
import CF.sendable.SolarRequest;

import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private HistoricData historicData;
    private BlockingQueue<ProsumerRequest> inputQueue;
    //private BlockingQueue<ProsumerResponse> outputQueue;

    private ForecastCommunicationHandler forecastCommunicationHandler;
    private SolarRequest solarRequest;

    public ProductionForecast(BlockingQueue<ProsumerRequest> inputQueue, ForecastCommunicationHandler forecastCommunicationHandler) {
        this.inputQueue = inputQueue;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
    }

    public void run() {
        System.out.println("ProductionForecast");
    }

    private double predictProduction() {
        return 0;
    }

    private SolarRequest getSolarRequest() {
        return solarRequest;
    }

    private HistoricData getHistoricData() {
        return historicData;
    }

    private void addToOutputQueueProduction(SolarRequest solarRequest) {

    }
}
