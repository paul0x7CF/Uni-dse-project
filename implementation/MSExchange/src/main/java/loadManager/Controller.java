package loadManager;

import exceptions.MessageProcessingException;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.networkManagment.CommunicationLoadManager;
import loadManager.networkManagment.LoadManagerMessageHandler;
import loadManager.timeSlotManagement.MessageBuilderTimeSlot;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Runnable {
    private static final Logger logger = LogManager.getLogger(Controller.class);
    private final int TIME_SLOT_DURATION;
    private final int NUM_NEW_TIME_SLOTS;
    LoadManagerMessageHandler messageHandler;
    private BlockingQueue<Message> incomingQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outgoingQueue = new LinkedBlockingQueue<>();
    private List<UUID> exchangeServiceIds = new ArrayList<>();
    private CommunicationLoadManager communication;
    private TimeSlotBuilder timeSlotBuilder;

    public Controller() {
        //read Properties
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        TIME_SLOT_DURATION = Integer.parseInt(propertyFileReader.getDuration());
        NUM_NEW_TIME_SLOTS = Integer.parseInt(propertyFileReader.getNumNewTimeSlots());

        timeSlotBuilder = new TimeSlotBuilder(TIME_SLOT_DURATION, NUM_NEW_TIME_SLOTS);
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);
        communicationThread.start();

        Thread timeSlotThread = new Thread(this::addNewTimeSlotsPeriodically);
        timeSlotThread.start();

        while (true) {
            processIncomingQueue();
            checkExchangeService();
        }
    }

    private void checkExchangeService() {
        List<MSData> exchanges = communication.getBroker().getServicesByType(EServiceType.ExchangeWorker);
        for (MSData exchange : exchanges) {
            if (!exchangeServiceIds.contains(exchange.getId())) {
                exchangeServiceIds.add(exchange.getId());
                ExchangeServiceInformation exchangeServiceInformation = new ExchangeServiceInformation(exchange.getId(), exchange.getAddress(), exchange.getPort());
                messageHandler.addExchangeServiceInformation(exchangeServiceInformation);
            }
        }
    }

    private void startCommunication() {
        communication = new CommunicationLoadManager(incomingQueue, outgoingQueue);
        communication.startBrokerRunner();
        messageHandler = new LoadManagerMessageHandler(outgoingQueue, communication.getBroker().getCurrentService());
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
        Message message = (Message) incomingQueue.poll();
        if (message != null) {
            try {
                messageHandler.handleMessage(message);
            } catch (MessageProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
