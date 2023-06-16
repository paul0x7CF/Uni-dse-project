package MSS.communication;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import CF.protocol.Message;
import CF.sendable.MSData;
import CF.sendable.TimeSlot;
import CF.sendable.Transaction;

import MSS.exceptions.MessageNotSupportedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;


public class ExchangeMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);

    private final MSData myMSData;
    private final BlockingQueue<Transaction> incomingTransactionQueue;

    public ExchangeMessageHandler(MSData myMSData, BlockingQueue<Transaction> incomingTransactionQueue) {
        this.myMSData = myMSData;
        this.incomingTransactionQueue = incomingTransactionQueue;
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        try {
            switch (message.getSubCategory()) {
                case "Transaction" -> handleTransaction(message);
                default -> throw new MessageNotSupportedException();
            }
        } catch (MessageNotSupportedException e) {
            logger.warn(e.getMessage());
        }

    }

    private void handleTransaction(Message message) {
        logger.debug("Transaction message received");
        Transaction newTransaction = (Transaction) message.getSendable(TimeSlot.class);
        try {
            this.incomingTransactionQueue.put(newTransaction);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
