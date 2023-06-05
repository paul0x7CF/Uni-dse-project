package loadManager;

import Exceptions.IllegalSendableException;
import exceptions.MessageProcessingException;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.networkManagment.CommunicationLoadManager;
import loadManager.networkManagment.LoadManagerMessageHandler;
import loadManager.networkManagment.MessageBuilder;
import loadManager.networkManagment.MessageContent;
import loadManager.timeSlotManagement.TimeSlotBuilder;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;
import sendable.TimeSlot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements Runnable {
    private static final Logger logger = LogManager.getLogger(Controller.class);
    LoadManagerMessageHandler messageHandler;
    private BlockingQueue<Message> incomingQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<MessageContent> outgoingQueue = new LinkedBlockingQueue<>();
    private List<UUID> exchangeServiceIds = new ArrayList<>();
    private CommunicationLoadManager communication;
    private TimeSlotBuilder timeSlotBuilder = new TimeSlotBuilder();
    private MessageBuilder messageBuilder;

    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);
        communicationThread.start();

        Thread timeSlotThread = new Thread(this::addNewTimeSlotsPeriodically);
        timeSlotThread.start();

        try {
            logger.trace("Waiting for communication to start");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
                ExchangeServiceInformation exchangeServiceInformation = new ExchangeServiceInformation(exchange.getId(), exchange.getAddress(), exchange.getPort());
                messageHandler.addExchangeServiceInformation(exchangeServiceInformation);
            }
        }
    }

    private void startCommunication() {
        communication = new CommunicationLoadManager(incomingQueue);
        communication.startBrokerRunner();
        messageHandler = new LoadManagerMessageHandler(outgoingQueue, communication.getBroker().getCurrentService());
        messageBuilder = new MessageBuilder(communication);
    }


    private void addNewTimeSlotsPeriodically() {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        int slotDuration = Integer.parseInt(propertyFileReader.getDuration());
        int checkInterval = Integer.parseInt(propertyFileReader.getCheckInterval());

        while (true) {
            if (!timeSlotBuilder.getLastSlotsEndtime().isBefore(LocalDateTime.now())) {
                TimeSlot newTimeSlot = timeSlotBuilder.addNewTimeSlot();

                List<Message> messages = messageBuilder.buildTimeSlotMessages(newTimeSlot);

                for (Message message : messages) {
                    communication.sendMessage(message);
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
            try {
                messageHandler.handleMessage(message);
            } catch (MessageProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processOutgoingQueue() {
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
