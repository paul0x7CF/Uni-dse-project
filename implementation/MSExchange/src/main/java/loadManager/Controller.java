package loadManager;

import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import CF.sendable.TimeSlot;
import MSP.Exceptions.IllegalSendableException;
import MSP.Exceptions.InvalidTimeSlotException;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.networkManagment.CommunicationLoadManager;
import loadManager.networkManagment.LoadManagerMessageHandler;
import loadManager.networkManagment.MessageBuilder;
import loadManager.networkManagment.MessageContent;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Runnable {
    private static final Logger logger = LogManager.getLogger(Controller.class);
    private final BlockingQueue<Message> incomingQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();
    private final List<UUID> exchangeServiceIds = new ArrayList<>();
    private final TimeSlotBuilder timeSlotBuilder = new TimeSlotBuilder();
    private CommunicationLoadManager communication;
    private LoadManagerMessageHandler messageHandler;
    private MessageBuilder messageBuilder;

    @Override
    public void run() {
        startCommunication();

        messageHandler = new LoadManagerMessageHandler(outgoingQueue, communication.getBroker().getCurrentService());
        messageBuilder = new MessageBuilder(communication);

        // Continue with the remaining logic
        Thread timeSlotThread = new Thread(this::addNewTimeSlotsPeriodically);
        timeSlotThread.start();

        long lastCheckTime = System.currentTimeMillis(); // Track the last check time


        while (true) {
            processIncomingQueue();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCheckTime >= 2000) { // Check every 2 seconds
                checkExchangeService();
                lastCheckTime = currentTime; // Update the last check time
            }
            processOutgoingQueue();
        }
    }

    private synchronized void checkExchangeService() {
        List<MSData> exchanges = communication.getBroker().getServicesByType(EServiceType.ExchangeWorker);
        //logger.debug("Still working");
        for (MSData exchange : exchanges) {
            if (!exchangeServiceIds.contains(exchange.getId())) {
                exchangeServiceIds.add(exchange.getId());
                ExchangeServiceInformation exchangeServiceInformation = new ExchangeServiceInformation(exchange.getId());
                messageHandler.addExchangeServiceInformation(exchangeServiceInformation);
            }
        }
    }

    private void startCommunication() {
        logger.debug("LOAD_MANAGER: Starting communication");
        communication = new CommunicationLoadManager(incomingQueue);
        Thread communicationThread = new Thread(() -> {
            communication.startBrokerRunner();
        }, "LoadManagerCommunication");
        communicationThread.start();
    }

    private void addNewTimeSlotsPeriodically() {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        int SLOT_DURATION = Integer.parseInt(propertyFileReader.getDuration());
        int CHECK_INTERVAL = Integer.parseInt(propertyFileReader.getCheckInterval());
        boolean first = true;

        while (true) {
            try {
                Thread.sleep(SLOT_DURATION * 1000 / CHECK_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            LocalDateTime currentTime = LocalDateTime.now();
            if (!timeSlotBuilder.getLastSlotsEndtime().isAfter(currentTime)) {
                if (timeSlotBuilder.getLastTimeSlot().isPresent()) {
                    UUID endedTimeSlotID = timeSlotBuilder.getLastTimeSlot().get();
                    logger.info("----------------Time Slot {} ended!----------------", endedTimeSlotID);
                    try {
                        messageHandler.endTimeSlot(endedTimeSlotID);
                    } catch (InvalidTimeSlotException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (first) {
                    try {
                        TimeSlot newTimeSlot = timeSlotBuilder.addNewTimeSlot();
                        logger.info("----------------Timeslot {} started!----------------", newTimeSlot.getTimeSlotID());
                        List<Message> messages = messageBuilder.buildTimeSlotMessages(newTimeSlot);
                        for (Message message : messages) {
                            communication.sendMessage(message);
                        }
                    } catch (InvalidTimeSlotException e) {
                        throw new RuntimeException(e);
                    }
                    //first = false;
                }
            }
        }
    }

    private void processIncomingQueue() {
        Message message = (Message) incomingQueue.poll();
        if (message != null) {
            if (message.getReceiverID().equals(communication.getBroker().getCurrentService().getId())) {
                messageHandler.handleMessage(message);
            }
        }
    }

    private void processOutgoingQueue() {
        MessageContent messageContent = (MessageContent) outgoingQueue.poll();
        if (messageContent != null) {
            try {
                List<Message> messages = messageBuilder.buildMessage(messageContent);
                for (Message message : messages) {
                    logger.debug("LOAD_MANAGER: Sending messageContent {} ", messageContent.getBuildCategory().toString());
                    communication.sendMessage(message);
                }
            } catch (IllegalSendableException e) {
                logger.error("LOAD_MANAGER: Failed to send message: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
