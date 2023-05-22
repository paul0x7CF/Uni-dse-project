package loadManager;

import broker.BrokerRunner;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.prosumerActionManagement.ProsumerManager;
import loadManager.timeSlotManagement.TimeSlotManager;
import messageHandling.IMessageHandler;
import protocol.ECategory;
import protocol.Message;
import sendable.Bid;
import sendable.EServiceType;
import sendable.Sell;
import sendable.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class Controller implements Runnable {
    private final int PORT = 1608;//TODO: change to correct port
    private final String ADDRESS = "10.102.102.13";//TODO: change to correct IP
    private final EServiceType SERVICE_TYPE = EServiceType.Exchange;
    private ProsumerManager prosumerManager;
    private BrokerRunner broker;
    private TimeSlotManager timeSlotManager;
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<?> outgoingQueue;
    private LoadManager loadManager = new LoadManager();
    private ExecutorService executorService;
    private IMessageHandler messageHandler;

    public Controller() {
        broker = new BrokerRunner(SERVICE_TYPE, PORT);
        broker.run();
        broker.addMessageHandler(ECategory.Exchange, messageHandler);

        initiateTimeSlotManager();
    }

    private void initiateTimeSlotManager() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("../config.properties");
            properties.load(configFile);
            configFile.close();

            int timeSlotDuration = Integer.parseInt(properties.getProperty("timeslot.duration"));
            int numNewTimeSlots = Integer.parseInt(properties.getProperty("timeslot.numNewTimeSlots"));

            timeSlotManager = new TimeSlotManager(timeSlotDuration, numNewTimeSlots);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        while (true) {
            processIncomingQueue();
            processOutgoingQueue();

        }
    }

    private void processIncomingQueue() {
        ProcessIncomingQueue processIncomingQueue = new ProcessIncomingQueue();
        Message message = (Message) incomingQueue.poll();
        if (message != null) {
            processIncomingQueue.process(message);
        }
    }

    private void processOutgoingQueue() {

    }

    private void handleBid(Bid bid) {
    }

    private void handleSell(Sell sell, UUID exchangeID) {
    }

    private void handleExchangeService(ExchangeServiceInformation exchangeServiceInformation) {
    }

    private void handleIncomingTransaction(Transaction transaction) {
    }
}
