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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSExchange implements Runnable {
    private static final Logger logger = LogManager.getLogger(MSExchange.class);
    private final int INSTANCE_NUMBER;
    private final boolean DUPLICATED;
    private final double TERMINATE_MINUTES;
    private final BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private final BlockingQueue<Transaction> outgoingTransactions = new LinkedBlockingQueue<>();
    private CommunicationExchange communication;
    private ExchangeMessageHandler messageHandler;
    private MessageBuilder messageBuilder;
    private Timer timer = null;
    private boolean atCapacity = false;


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
        this.TERMINATE_MINUTES = new PropertyFileReader().getTerminateMinutes();
    }

    private void startCommunication() {
        communication = new CommunicationExchange(incomingMessages, INSTANCE_NUMBER);
        Thread communicationThread = new Thread(() -> {
            communication.startBrokerRunner();
        }, "ExchangeCommunicationThread");
        communicationThread.start();

    }

    private void scheduleTermination(long delayInMilliseconds) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("EXCHANGE: Terminating MSExchange after {} milliseconds", delayInMilliseconds);
                System.exit(0);  // Alternativ können Sie anstelle von System.exit(0) eine andere Methode zum Beenden des Programms aufrufen.
            }
        }, delayInMilliseconds);
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
        if (incomingMessages.size() == 0 && isDuplicated()) {
            if (timer == null) {
                scheduleTermination((long) (TERMINATE_MINUTES * 60));
                return;
            }
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        int CAPACITY = Integer.parseInt(propertyFileReader.getCapacity());


        if (!atCapacity) {
            if (incomingMessages.size() >= CAPACITY) {
                logger.warn("EXCHANGE: Queue is full!");
                atCapacity = true;
                communication.sendMessage(messageBuilder.buildCapacityMessage());
            }
        } else {
            if (incomingMessages.size() < CAPACITY / 2) {
                atCapacity = false;
                communication.sendMessage(messageBuilder.buildCapacityMessage());
            }
        }

    }

    private void processOutgoingTransactions() {
        Transaction transaction = outgoingTransactions.poll();
        if (transaction != null) {
            logger.debug("EXCHANGE: Sending Transaction to: " + transaction.getBuyerID() + " and " + transaction.getSellerID() + " for volume: " + transaction.getAmount() + " with price/Wh: " + transaction.getPrice());
            communication.sendMessage(messageBuilder.buildMessage(transaction));
        }
    }

    public boolean isDuplicated() {
        return DUPLICATED;
    }
}
