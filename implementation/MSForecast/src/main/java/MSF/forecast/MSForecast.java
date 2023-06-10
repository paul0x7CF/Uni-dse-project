package MSF.forecast;

import CF.protocol.Message;
import CF.sendable.TimeSlot;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class MSForecast implements Runnable {
    private List<TimeSlot> timeSlots;
    private EForecastType forecastType;
    private UUID forecastId;
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;

    public MSForecast(BlockingQueue<Message> inputQueue, BlockingQueue<Message> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public EForecastType getForecastType() {
        return forecastType;
    }

    public void run() {
        System.out.println("MSForecast");
    }

    private void updateTimeSlots() {

    }

    private void handleRequests() {

    }
}
