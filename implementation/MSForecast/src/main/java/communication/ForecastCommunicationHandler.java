package communication;

import protocol.Message;
import broker.Broker;

import java.util.concurrent.BlockingQueue;

public class ForecastCommunicationHandler {
    private Broker broker; // TODO Günther: Don't instantiate broker here, use BrokerRunner
    private BlockingQueue<Message> inputQueue;
    private BlockingQueue<Message> outputQueue;
    private ForecastMessageHandler forecastMessageHandler;

    public ForecastCommunicationHandler(BlockingQueue<Message> inputQueue, BlockingQueue<Message> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void sendMessage(Message message) {

    }

    public void register() {

    }

    public void setUpBroker() {

    }

    public void addMessageHandler() {

    }

    private void handleForecastMessage(Message message) {

    }
}
