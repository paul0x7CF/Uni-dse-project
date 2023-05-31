package msExchange;

import msExchange.messageHandling.ExchangeMessageHandler;
import msExchange.networkCommunication.CommunicationExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MSExchange implements IExchange, Runnable {
    private static final Logger logger = LogManager.getLogger(MSExchange.class);
    private BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outgoingMessages = new LinkedBlockingQueue<>();
    private BlockingQueue<Bid> bidQueue;
    private BlockingQueue<Sell> sellQueue;
    private BlockingQueue<Transaction> transactionQueue;

    private CommunicationExchange communication;
    private ExchangeMessageHandler messageHandler;
    private boolean duplicated;


    public MSExchange(boolean duplicated) {
        this.duplicated = duplicated;
    }

    private void startCommunication() {
        communication = new CommunicationExchange(incomingMessages, outgoingMessages);
        communication.startBrokerRunner();
    }

    @Override
    public void run() {
        Thread communicationThread = new Thread(this::startCommunication);
        communicationThread.start();

        while (true) {
            Message message = incomingMessages.poll();
            if (message != null) {
                logger.debug("Received message: " + message);
                messageHandler.handleMessage(message);
            }
        }

    }

    @Override
    public void processBidQueue() {

    }

    @Override
    public void receivedBid(Bid bid) {

    }

    @Override
    public void receivedSell(Sell sell) {

    }


    public boolean isDuplicated() {
        return duplicated;
    }
}
