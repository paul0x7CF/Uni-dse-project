package MSF.calculation;

import MSF.communication.messageHandler.ProsumerRequest;
import MSF.historicData.HistoricData;
import CF.protocol.Message;
import CF.sendable.SolarRequest;
import CF.sendable.TimeSlot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private HistoricData historicData;
    private BlockingQueue<ProsumerRequest> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private SolarRequest solarRequest;

    public ProductionForecast(BlockingQueue<ProsumerRequest> inputQueue, BlockingQueue<Message> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
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
