package MSF.calculation;

import CF.protocol.Message;
import CF.sendable.TimeSlot;
import MSF.communication.messageHandler.ProsumerRequest;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConsumptionForecast implements Runnable {
    private BlockingQueue<ProsumerRequest> inputQueue;
    private BlockingQueue<Message> outputQueue;

    public ConsumptionForecast(BlockingQueue<ProsumerRequest> inputQueue, BlockingQueue<Message> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void run() {
        System.out.println("ConsumptionForecast");

        //take from inputQueue
        try {
            ProsumerRequest prosumerRequest = inputQueue.take();
            double consumption = predictConsumption(prosumerRequest);
            // TODO: create Message ConsumptionResponse
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double predictConsumption(ProsumerRequest prosumerRequest) {
        // calculate consumption

        return 0;
    }

    private void addToOutputQueueConsumption() {

    }
}
