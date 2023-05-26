package loadManager;

import broker.BrokerRunner;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.networkManagment.ExtendedMessageBuilder;
import loadManager.prosumerActionManagement.ProsumerManager;
import loadManager.timeSlotManagement.TimeSlotBuilder;
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
    private final int PORT;
    private final String ADDRESS;
    private final EServiceType SERVICE_TYPE;
    private ProsumerManager prosumerManager;
    private BrokerRunner broker;
    private TimeSlotBuilder timeSlotBuilder;
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;
    private LoadManager loadManager = new LoadManager();
    private ExecutorService executorService;
    private int TIME_SLOT_DURATION;
    private int NUM_NEW_TIME_SLOTS;
    private IMessageHandler messageHandler; //TODO: does it make sense to have a interface here? -> concrete implementation?

    public Controller() {
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("../config.properties");
            properties.load(configFile);
            configFile.close();

            TIME_SLOT_DURATION = Integer.parseInt(properties.getProperty("timeslot.duration"));
            NUM_NEW_TIME_SLOTS = Integer.parseInt(properties.getProperty("timeslot.numNewTimeSlots"));
            PORT = Integer.parseInt(properties.getProperty("loadmanager.port"));
            ADDRESS = properties.getProperty("loadmanager.address");
            SERVICE_TYPE = EServiceType.valueOf(properties.getProperty("loadmanager.serviceType"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        broker = new BrokerRunner(SERVICE_TYPE, PORT);
        broker.run();
        broker.addMessageHandler(ECategory.Exchange, messageHandler);

        timeSlotBuilder = new TimeSlotBuilder(TIME_SLOT_DURATION, NUM_NEW_TIME_SLOTS);
    }

    @Override
    public void run() {
        Thread timeSlotThread = new Thread(this::addNewTimeSlotsPeriodically);
        timeSlotThread.start();

        while (true) {
            processIncomingQueue();
        }
    }

    /**
     * Thread to creating new time slots command to build message to send time slots
     */
    private void addNewTimeSlotsPeriodically() {
        while (true) {
            timeSlotBuilder.addNewTimeSlots();
            //TODO: message-builder: Build message to send timeSlots
            Message message = ExtendedMessageBuilder.buildTimeSlotMessage(timeSlotBuilder.getTimeSlots());

            try {
                //Wait for the specified duration in secs
                Thread.sleep(TIME_SLOT_DURATION * NUM_NEW_TIME_SLOTS * 1000); //*1000 to convert to ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processIncomingQueue() {
        ProcessIncomingQueue processIncomingQueue = new ProcessIncomingQueue();
        Message message = (Message) incomingQueue.poll();
        if (message != null) {
            processIncomingQueue.process(message);

        }
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
