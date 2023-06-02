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
    private BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Transaction> outgoingTransactions = new LinkedBlockingQueue<>();
    private CommunicationExchange communication;
    private ExchangeMessageHandler messageHandler;
    private MessageBuilder messageBuilder;
    private boolean duplicated;


    public MSExchange(boolean duplicated) {
        this.duplicated = duplicated;
    }

    private void startCommunication() {
        communication = new CommunicationExchange(incomingMessages);
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
            messageBuilder.sendCapacityMessage();
        }
    }

    private void processOutgoingTransactions() {
        Transaction transaction = outgoingTransactions.poll();
        if (transaction != null) {
            logger.trace("Sending transaction: " + transaction);
            ;
            communication.sendMessage(messageBuilder.buildMessage(transaction));
        }
    }

    public boolean isDuplicated() {
        return duplicated;
    }
}
