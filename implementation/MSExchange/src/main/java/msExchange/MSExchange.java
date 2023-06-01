package msExchange;

import exceptions.MessageProcessingException;
import mainPackage.PropertyFileReader;
import msExchange.auctionManagement.AuctionManager;
import msExchange.messageHandling.ExchangeMessageHandler;
import msExchange.messageHandling.MessageBuilder;
import msExchange.networkCommunication.CommunicationExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSExchange implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSExchange.class);
    private BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outgoingMessages = new LinkedBlockingQueue<>();

    private AuctionManager auctionManager;

    private CommunicationExchange communication;
    private ExchangeMessageHandler messageHandler;
    private MessageBuilder messageBuilder;
    private boolean duplicated;


    public MSExchange(boolean duplicated) {
        this.duplicated = duplicated;
    }

    private void startCommunication() {
        communication = new CommunicationExchange(incomingMessages, outgoingMessages);
        communication.startBrokerRunner();
        messageBuilder = new MessageBuilder(communication.getBroker());
        messageHandler = new ExchangeMessageHandler();
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);
        communicationThread.start();

        while (true) {
            processIncomingMessages();
            processOutgoingMessages();
        }

    }

    private void processIncomingMessages() {
        checkCapacity();
        Message message = incomingMessages.poll();
        if (message != null) {
            logger.debug("Received message: " + message);
            try {
                messageHandler.handleMessage(message);
            } catch (MessageProcessingException e) {
                messageBuilder.sendErrorMessage(message, e);
                logger.error("SubCategory was incorrect: " + message);
            }
        }
    }

    private void checkCapacity() {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        int CAPACITY = Integer.parseInt(propertyFileReader.getCapacity());
        if (incomingMessages.size() >= CAPACITY) {
            logger.warn("BidQueue is full!");
            messageBuilder.sendCapacityMessage();
        }
    }

    private void processOutgoingMessages() {
        Message message = outgoingMessages.poll();
        if (message != null) {
            logger.trace("Sending message: " + message);
            communication.sendMessage(message);
        }
    }

    public boolean isDuplicated() {
        return duplicated;
    }
}
