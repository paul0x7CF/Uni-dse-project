package msExchange;

import exceptions.MessageProcessingException;
import mainPackage.PropertyFileReader;
import msExchange.messageHandling.ExchangeMessageHandler;
import msExchange.messageHandling.MessageBuilder;
import msExchange.networkCommunication.CommunicationExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Transaction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSExchange implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSExchange.class);
    private final int INSTANCE_NUMBER;
    private final boolean DUPLICATED;

    private BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Transaction> outgoingTransactions = new LinkedBlockingQueue<>();

    private CommunicationExchange communication;
    private ExchangeMessageHandler messageHandler;
    private MessageBuilder messageBuilder;


    public MSExchange(boolean duplicated, int instanceNumber) {
        this.DUPLICATED = duplicated;
        this.INSTANCE_NUMBER = instanceNumber;
    }

    private void startCommunication() {
        communication = new CommunicationExchange(incomingMessages, INSTANCE_NUMBER);
        communication.startBrokerRunner();
        messageBuilder = new MessageBuilder(communication.getBroker());
        messageHandler = new ExchangeMessageHandler(outgoingTransactions);
    }

    //TODO: think about deleting a service
    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);
        communicationThread.start();


        while (true) {
            processIncomingMessages();
            processOutgoingTransactions();
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
            communication.sendMessage(messageBuilder.buildCapacityMessage());
        }
    }

    private void processOutgoingTransactions() {
        Transaction transaction = outgoingTransactions.poll();
        if (transaction != null) {
            logger.trace("Sending transaction: " + transaction);
            for (Message message : messageBuilder.buildMessage(transaction)) {
                communication.sendMessage(message);
            }
        }
    }

    public boolean isDuplicated() {
        return DUPLICATED;
    }
}
