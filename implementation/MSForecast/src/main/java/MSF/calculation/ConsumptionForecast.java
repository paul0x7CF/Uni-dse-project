package MSF.calculation;

import CF.protocol.Message;
import CF.sendable.TimeSlot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private List<TimeSlot> timeSlots;

    public ConsumptionForecast(BlockingQueue<Message> inputQueue, BlockingQueue<Message> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void run() {
        System.out.println("ConsumptionForecast");
    }

    private double predictConsumption() {
        return 0;
    }

    private void addToOutputQueueConsumption() {

    }
}
