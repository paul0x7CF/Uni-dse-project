package msExchange;

import CF.exceptions.MessageProcessingException;
import CF.protocol.Message;
import CF.sendable.Transaction;
import mainPackage.PropertyFileReader;
import msExchange.messageHandling.ExchangeMessageHandler;
import msExchange.messageHandling.MessageBuilder;
import msExchange.networkCommunication.CommunicationExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSExchange implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSExchange.class);
    private final int INSTANCE_NUMBER;
    private final boolean DUPLICATED;
    private boolean atCapacity = false;
    private boolean first = true; //TODO: remove after testing
    private BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Transaction> outgoingTransactions = new LinkedBlockingQueue<>();
    private CommunicationExchange communication;
    private ExchangeMessageHandler messageHandler;
    private MessageBuilder messageBuilder;


    public MSExchange(boolean duplicated, int instanceNumber) {
        String loggingMessage = "";
        if (duplicated) {
            loggingMessage += "duplicated ";
        } else {
            loggingMessage += "non-duplicated ";
        }
        logger.info("EXCHANGE: Starting Exchange as {}, instance. Number {}", loggingMessage, instanceNumber);
        this.DUPLICATED = duplicated;
        this.INSTANCE_NUMBER = instanceNumber;
    }

    private void startCommunication() {
        communication = new CommunicationExchange(incomingMessages, INSTANCE_NUMBER);
        Thread communicationThread = new Thread(() -> {
            communication.startBrokerRunner();
        }, "ExchangeCommunicationThread");
        communicationThread.start();

    }

    @Override
    public void run() {
        startCommunication();

        messageBuilder = new MessageBuilder(communication);
        messageHandler = new ExchangeMessageHandler(outgoingTransactions);

        while (true) {
            processIncomingMessages();
            processOutgoingTransactions();
        }
    }

    private void processIncomingMessages() {
        checkCapacity();
        Message message = incomingMessages.poll();
        if (message != null) {
            if (message.getReceiverID().equals(communication.getBroker().getCurrentService().getId())) {
                try {
                    messageHandler.handleMessage(message);
                } catch (MessageProcessingException e) {
                    logger.error("EXCHANGE: Message wasn't correct " + message.getSubCategory() + ", problem: " + e.getMessage());
                }
            }
        }
    }

    private void checkCapacity() {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        int CAPACITY = Integer.parseInt(propertyFileReader.getCapacity());

        if (!atCapacity) {
            if (incomingMessages.size() >= CAPACITY) {
                logger.warn("EXCHANGE: BidQueue is full!");
                communication.sendMessage(messageBuilder.buildCapacityMessage());
            }
            if (!isDuplicated() && first) {
                //TODO: remove this statement after testing
                first = false;
                communication.sendMessage(messageBuilder.buildCapacityMessage());
            }
        } else {
            if (incomingMessages.size() < CAPACITY / 2) {
                communication.sendMessage(messageBuilder.buildCapacityMessage());
            }
        }

    }

    private void processOutgoingTransactions() {
        Transaction transaction = outgoingTransactions.poll();
        if (transaction != null) {
            logger.debug("EXCHANGE: taking outgoing transaction: " + transaction);
            for (Message message : messageBuilder.buildMessage(transaction)) {
                logger.info("EXCHANGE: Sending Transaction to: " + transaction.getBuyerID() + " and " + transaction.getSellerID() + " for volume: " + transaction.getAmount() + " with price/kwh: " + transaction.getPrice());
                communication.sendMessage(message);
            }
        }
    }

    public boolean isDuplicated() {
        return DUPLICATED;
    }
}
