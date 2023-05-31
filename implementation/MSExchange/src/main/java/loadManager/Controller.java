package loadManager;

import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.networkManagment.Communication;
import loadManager.prosumerActionManagement.ProsumerManager;
import loadManager.timeSlotManagement.MessageBuilderTimeSlot;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Runnable {
    private static final Logger logger = LogManager.getLogger(Controller.class);
    private final int TIME_SLOT_DURATION;
    private final int NUM_NEW_TIME_SLOTS;

    private ProsumerManager prosumerManager;
    private Communication communication;
    private TimeSlotBuilder timeSlotBuilder;
    private BlockingQueue<Message> incomingQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outgoingQueue = new LinkedBlockingQueue<>();
    private LoadManager loadManager = new LoadManager();
    private ExecutorService executorService;
    private IMessageHandler messageHandler; //TODO: does it make sense to have a interface here? -> concrete implementation?

    public Controller() {
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties");
            properties.load(configFile);
            configFile.close();

            TIME_SLOT_DURATION = Integer.parseInt(properties.getProperty("timeslot.duration"));
            NUM_NEW_TIME_SLOTS = Integer.parseInt(properties.getProperty("timeslot.numNewTimeSlots"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        timeSlotBuilder = new TimeSlotBuilder(TIME_SLOT_DURATION, NUM_NEW_TIME_SLOTS);
    }

    private void startCommunication() {
        communication = new Communication(incomingQueue, outgoingQueue);
        communication.startBrokerRunner();
    }


    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);

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
            MessageBuilderTimeSlot messageBuilderTimeSlot = new MessageBuilderTimeSlot();
            List<Message> messages = messageBuilderTimeSlot.buildTimeSlotMessages(timeSlotBuilder.getTimeSlots());

            for (Message message : messages) {
                outgoingQueue.add(message);
            }

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
