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
    private LoadManagerMessageHandler messageHandler;
    private BlockingQueue<Message> incomingQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();
    private List<UUID> exchangeServiceIds = new ArrayList<>();
    private CommunicationLoadManager communication;
    private TimeSlotBuilder timeSlotBuilder = new TimeSlotBuilder();
    private MessageBuilder messageBuilder;

    @Override
    public void run() {
        startCommunication();

        messageHandler = new LoadManagerMessageHandler(outgoingQueue, communication.getBroker().getCurrentService());
        logger.trace("message Handler initialized");
        messageBuilder = new MessageBuilder(communication);
        logger.trace("Message builder initialized");


        // Continue with the remaining logic
        Thread timeSlotThread = new Thread(this::addNewTimeSlotsPeriodically);
        timeSlotThread.start();

        while (true) {
            processIncomingQueue();
            checkExchangeService();
            processOutgoingQueue();
        }
    }

    private void checkExchangeService() {
        List<MSData> exchanges = communication.getBroker().getServicesByType(EServiceType.ExchangeWorker);
        for (MSData exchange : exchanges) {
            if (!exchangeServiceIds.contains(exchange.getId())) {
                exchangeServiceIds.add(exchange.getId());
                ExchangeServiceInformation exchangeServiceInformation = new ExchangeServiceInformation(exchange.getId());
                messageHandler.addExchangeServiceInformation(exchangeServiceInformation);
            }
        }
    }

    private void startCommunication() {
        logger.debug("Starting communication");
        communication = new CommunicationLoadManager(incomingQueue);
        Thread communicationThread = new Thread(() -> {
            communication.startBrokerRunner();
        }, "LoadManagerCommunicationThread");
        communicationThread.start();
    }

    private void addNewTimeSlotsPeriodically() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        int slotDuration = Integer.parseInt(propertyFileReader.getDuration());
        int checkInterval = Integer.parseInt(propertyFileReader.getCheckInterval());

        while (true) {
            if (!timeSlotBuilder.getLastSlotsEndtime().isAfter(LocalDateTime.now())) {
                if (timeSlotBuilder.getLastTimeSlot().isPresent()) {
                    UUID endedTimeSlotID = timeSlotBuilder.getLastTimeSlot().get();
                    logger.debug("TimeSlot has ended: " + endedTimeSlotID);
                    try {
                        messageHandler.endTimeSlot(endedTimeSlotID);
                    } catch (InvalidTimeSlotException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    TimeSlot newTimeSlot = timeSlotBuilder.addNewTimeSlot();
                    logger.debug("Timeslot adding: " + newTimeSlot.getTimeSlotID());
                    List<Message> messages = messageBuilder.buildTimeSlotMessages(newTimeSlot);
                    for (Message message : messages) {
                        communication.sendMessage(message);
                    }

                } catch (InvalidTimeSlotException e) {
                    throw new RuntimeException(e);
                }

            }
            try {
                //Wait for the specified duration in secs
                Thread.sleep(slotDuration / checkInterval * 1000); //*1000 to convert to ms
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        //TODO: test if bid/sell works properply in exchange - mocking
        MessageContent messageContent = (MessageContent) outgoingQueue.poll();
        if (messageContent != null) {
            try {
                List<Message> messages = messageBuilder.buildMessage(messageContent);
                for (Message message : messages) {
                    communication.sendMessage(message);
                }
            } catch (IllegalSendableException e) {
                //TODO: throw RuntimeException?
                System.err.println("Failed to send message: " + e.getMessage());
                logger.error("Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
