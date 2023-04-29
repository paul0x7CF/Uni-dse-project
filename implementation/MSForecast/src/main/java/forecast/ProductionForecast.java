package forecast;

import historicData.HistoricData;
import protocol.Message;
import sendable.SolarRequest;
import sendable.TimeSlot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private HistoricData historicData;
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private SolarRequest solarRequest;
    private List<TimeSlot> timeSlots;

    public ProductionForecast(BlockingQueue<Message> inputQueue, BlockingQueue<Message> outputQueue) {
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
